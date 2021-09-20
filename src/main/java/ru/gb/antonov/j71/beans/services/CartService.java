package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException;
import ru.gb.antonov.j71.entities.OurUser;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.dtos.CartDto;
import ru.gb.antonov.j71.entities.dtos.OrderItemDto;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import static ru.gb.antonov.j71.Factory.DRYCART;
import static ru.gb.antonov.j71.Factory.cartKeyByLogin;

@Service
@RequiredArgsConstructor
public class CartService
{
/*      Корзина разделена на 2 части: InMemoryCart (хранится в Redis-е) и CartDto (формируется
    каждый раз, когда клиент запрашивает корзину).
        В кэше в корзине храним только id товаров и количества. Всю остальную инф-цию о товаре
    считываем из базы во время формирования CartDto.
        Таким образом мы решаем проблему устаревших цен и названий для товаров. Если этот подход слишком
    нагружает БД, то, возможно, БД тоже нужен какой-то кэшь. Во всяком случае выбранный мною подход
    выглядит вполне логичным.
*/
    private final ProductService productService;
    private final OurUserService ourUserService;
    private final RedisTemplate<String, Object> redisTemplate;
//--------------------- классы для хранения корзин в кэше ----------------

    public static class CartsEntry //Это не Entry; просто носить ключ рядом с корзиной очень удобно.
    {
        private final String key;
        private final InMemoryCart imcart;

        private CartsEntry (String k, InMemoryCart v) {  key = k;    imcart = v;  }
    }

    public static class CartItem
    {
        private long pid;
        private int quantity;

        public CartItem (){}
        private CartItem (long p, int q) { pid = p;quantity = q; }

        public void setPid (long value) { pid = value; }
        public void setQuantity (int value) { quantity = value; }
        public long getPid () { return pid; }
        public int getQuantity () { return quantity; }
        //public String toString() { return String.format ("CartItem:[pid:%d, qty:%d]", pid, quantity); }
    }

    public static class InMemoryCart
    {
        List<CartItem> citems;

        private InMemoryCart () { citems = new LinkedList<>(); }
        private void clear()    { citems.clear(); }

        public void setCitems (List<CartItem> list) { citems = list; }
        public List<CartItem> getCitems () { return citems; }
        //public String toString() { return citems.toString(); }
    }
//------------------------------------------------------------------------
    private OurUser userByPrincipal (Principal principal)
    {
        return ourUserService.userByPrincipal (principal);
    }

    @Transactional
    public CartDto getUsersCartDto (Principal principal)
    {
        CartsEntry ce = getUsersCartEntry (principal);
        return inMemoryCartToDto (ce.imcart, !DRYCART);
    }

    @NotNull
    private CartsEntry getUsersCartEntry (Principal principal)
    {
        return getUsersCartEntry (userByPrincipal (principal));
    }

    @NotNull
    private CartsEntry getUsersCartEntry (OurUser ourUser)
    {
        if (ourUser == null)
            throw new UnableToPerformException ("getUsersCart(): нет юзера — нет корзины!");

        String key = cartKeyByLogin (ourUser.getLogin());
        if (!redisTemplate.hasKey (key))
        {
            redisTemplate.opsForValue().set (key, new InMemoryCart());
        }
        InMemoryCart imcart = (InMemoryCart) redisTemplate.opsForValue().get(key);
        if (imcart == null)
            throw new UnableToPerformException ("getUsersCart(): не могу извлечь корзину пользователя: "
                                                + ourUser.getLogin());
        return new CartsEntry (key, imcart);
    }

    private void updateCart (CartsEntry cartsEntry)
    {
        redisTemplate.opsForValue().set (cartsEntry.key, cartsEntry.imcart);
    }

    @Transactional
    public int getCartLoad (Principal principal)
    {
        CartsEntry ce = getUsersCartEntry (principal);
        return inlineCalcImcLoad (ce.imcart.citems);
    }

    private int inlineCalcImcLoad (List<CartItem> citems)
    {
        int load = 0;
        for (CartItem ci : citems)
        {
            load += ci.quantity;
        }
        return load;
    }

    @Transactional
    public double getCartCost (Principal principal)
    {
        CartsEntry ce = getUsersCartEntry (principal);
        return inlineCalcCartCost (ce.imcart.citems);
    }

    private double inlineCalcCartCost (List<CartItem> citems)
    {
        double cartcost = 0.0;
        for (CartItem ci : citems)
        {
            int quantity = ci.quantity;
            if (quantity > 0)
                cartcost += productService.findById (ci.pid).getPrice() * quantity;
        }
        return cartcost;
    }

    @Transactional
    public void changeProductQuantity (long productId, int delta, Principal principal)
    {
    //(Считаем, что нет смысла создавать новую товарн.позицию для неположительных значений delta.)
        if (delta == 0)
            return;
        CartsEntry ce = getUsersCartEntry (principal);
        if (delta < 0)
        {
            for (CartItem ci : ce.imcart.citems) //предполагаем, что productId может встретиться в корзине больше 1-го раза.
            {
                if (delta >= 0)
                    break;
                if (ci.pid == productId)
                {
                    delta += ci.quantity;
                    ci.quantity = (delta < 0) ? 0 : delta;
                }
            }
        }
        else
        {   for (CartItem ci : ce.imcart.citems)
            if (ci.pid == productId)
            {
                ci.quantity += delta;
                delta = 0;
                break;
            }
            if (delta > 0)
                ce.imcart.citems.add (new CartItem (productId, delta));
        }
        updateCart (ce);
    }

    @Transactional
    public void removeProduct (long productId, Principal principal)
    {
        CartsEntry ce = getUsersCartEntry (principal);
        if (ce.imcart.citems.removeIf (ci->ci.pid == productId))
            updateCart (ce);
    }

    @Transactional
    public void clearCart (Principal principal) { clearCart (userByPrincipal (principal)); }

    public void clearCart (OurUser ourUser)
    {
        CartsEntry ce = getUsersCartEntry (ourUser);
        ce.imcart.clear();
        updateCart (ce);     //TODO: удалять корзины нужно поручить Memurai-ю.
    }

    public CartDto getUsersDryCartDto (OurUser ourUser)
    {
        return inMemoryCartToDto (getUsersCartEntry (ourUser).imcart, DRYCART);
    }

    @NotNull
    private CartDto inMemoryCartToDto (InMemoryCart imcart, boolean dtycart)
    {
        CartDto cdto = new CartDto ();
        for (CartItem ci : imcart.citems)
        {
            int quantity = ci.quantity;
            if (quantity > 0 || dtycart != DRYCART)
            {
                Product p = productService.findById (ci.pid);
                int rest = p.getRest();
                if (rest > 0)   //< если остаток товара <= 0, то считаем, что товара нет
                {
                    quantity = Math.min (rest, quantity);
                    cdto.addItem (new OrderItemDto (p), quantity);
                }
            }
        }
        return cdto;
    }

/** Товар не удаляем, а обнуляем у него поле {@code rest}. В дальнейшем, при попытке добавить
    этот товар в корзину, проверяется его количество, и, т.к. оно равно 0, добавление не происходит.<p>
    Логично будет добавить к этому НЕвозможность показывать такой товар на витрине.
*/
    @Transactional
    public void deleteById (Long productId) //черновик
    {
        //TODO: Редактирование товаров пока не входит в план проекта.
        //TODO: добавить к этому НЕвозможность показывать такой товар на витрине.
        productService.onProductDeletion (productId);
    }
}
