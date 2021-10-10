package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.antonov.j71.Factory;
import ru.gb.antonov.j71.beans.errorhandlers.ResourceNotFoundException;
import ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException;
import ru.gb.antonov.j71.entities.OurUser;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.dtos.CartDto;
import ru.gb.antonov.j71.entities.dtos.OrderItemDto;

import java.security.Principal;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

import static ru.gb.antonov.j71.Factory.*;

@Service
@RequiredArgsConstructor
public class CartService
{
/** <b>Как устроена корзина</b><p>
        При запуске приложения на фронте, клиент ищет строковую переменную {@code gbj7MarketGuestCartId} (UUID) в харнилище браузера и, если не находит, запрашивает для неё значение на бэке. Во все «корзинные» запросы клиент вставляет этот UUID, независимо от того, авторзован юзер или нет.<p>
        В запросах клиента к бэку логин юзера указывает на одну корзину, а UUID на другую. Если юзер авторизован, используется первая из корзин. Если неавторизован — вторая.<p>
        При авторизации юзера ВСЕ товары из UUID-корзины перемещаются в логин-корзину, после чего UUID-корзина удаляется, но её UUID остаётся у клиента. UUID-корзина создаётся только по мере необходимости. До этого момента реально существует только UUID для неё. UUID-корзина, в отличие от логин-корзины, имеет таймер бездействи, по истечении которого она удаляется, но её UUID остаётся у клиента.<p>
        Оформить заказ можно только будучи авторизованным.<p>
        Имя корзины состоит из прификса и постфикса. Префикс одинаков для всех корзин приложения, а постфиксом является логин или UUID. Корзины хранятся в Memurai-кэше (аналог Redis'а), а имя корзины является ключом для поиска корзины в этом кэше.<p>
        Любая корзина реализована в виде 2 частей: InMemoryCart (хранится в Memurai-е) и CartDto (формируется и отдаётся клиенту каждый раз, когда он запрашивает информацию о корзине).<p>
        В InMemoryCart храним только id товаров и количества. Всю остальную инф-цию о товаре считываем из базы во время формирования CartDto. Таким образом мы решаем проблему устаревших цен и названий для товаров. (Если этот подход слишком нагружает БД, то, возможно, БД тоже нужен какой-то кэшь.)
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
        private CartItem (long p, int q) { pid = p;   quantity = q; }

        public void setPid (long value) { pid = value; }
        public void setQuantity (int value) { quantity = Math.max(value, 0); }

        public long getPid () { return pid; }
        public int getQuantity () { return quantity; }

        public String toString() { return String.format ("CartItem:[pid:%d, qty:%d]", pid, quantity); }
    }

    public static class InMemoryCart
    {
        List<CartItem> citems;

        private InMemoryCart () { citems = new LinkedList<>(); }

        public void setCitems (List<CartItem> list) { citems = list; }
        public List<CartItem> getCitems () { return citems; }

/** @return {@code true}, только если корзина не была пустой в момент вызова метода. */
        private boolean clear()
        {
            boolean ok = citems.size() > 0;
            citems.clear();
            return ok;
        }

/** Добавляем к орзину элемент, даже если его кол-во ==0.
    @return {@code true}, только если корзина была изменена: добавлен новый элемент или увеличено количество уже существующего элемента.
*/
        private boolean add (@NotNull CartItem cartItem)
        {
            long p = cartItem.pid;
            int q = cartItem.quantity;
            if (q >= 0)
            {
                CartItem ci = getItemByPid (p);
                if (ci != null)
                {
                    ci.quantity += q;
                    return q > 0;
                }
                return citems.add (new CartItem (p, q));
            }
            return false;
        }

        public int calcLoad ()
        {
            int load = 0;
            for (CartItem ci : citems)
                load += ci.quantity;
            return load;
        }

        private CartItem getItemByPid (long pid)
        {
            for (CartItem ci : citems)
                if (ci.pid == pid)
                    return ci;
            return null;
        }

        private boolean removeNonEmptyItems() { return citems.removeIf (ci->ci.quantity > 0); }

        private boolean removeItemByProductId (long pid)  { return citems.removeIf (ci->ci.pid == pid); }

        public String toString() { return citems.toString(); }
    }
//------------------------------------------------------------------------

    @Transactional @NotNull
    public CartDto getUsersCartDto (Principal principal, String uuid)
    {
        CartsEntry ce = getUsersCartEntry (principal, uuid);
        return inMemoryCartToDto (ce.imcart, !DRYCART);
    }

    @NotNull
    private CartsEntry getUsersCartEntry (Principal principal, String uuid)
    {
        String postfix = uuid;
        Duration cartLife = CART_LIFE;
        if (principal != null)
        {
            postfix = ourUserService.userByPrincipal (principal).getLogin();
            cartLife = null;
        }
        return getUsersCartEntry (postfix, cartLife);
    }

    @NotNull
    private CartsEntry getUsersCartEntry (String postfix, Duration cartLife)
    {
        if ((postfix = validateString (postfix, LOGIN_LEN_MIN, LOGIN_LEN_MAX)) == null)
            throw new UnableToPerformException ("getUsersCart(): нет ключа — нет корзины!");

        String key = cartKeyByLogin (postfix);
        if (!redisTemplate.hasKey (key))
        {
            redisTemplate.opsForValue().set (key, new InMemoryCart());
            if (cartLife != null)
                redisTemplate.expire (key, cartLife);
        }
        InMemoryCart imcart = (InMemoryCart) redisTemplate.opsForValue().get (key);
        if (imcart == null)
            throw new UnableToPerformException ("getUsersCart(): не могу извлечь корзину пользователя: " + postfix);

        return new CartsEntry (key, imcart);
    }

/*    @NotNull
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
    }*/

    private void updateCart (CartsEntry cartsEntry)
    {
        redisTemplate.opsForValue().set(cartsEntry.key, cartsEntry.imcart);
    }

    @Transactional
    public int getCartLoad (Principal principal, String uuid)
    {
        return getUsersCartEntry (principal, uuid).imcart.calcLoad();
    }

    @Transactional
    public double getCartCost (Principal principal, String uuid)
    {
        return calcCost (getUsersCartEntry (principal, uuid).imcart.citems);
    }

/** Метод должен вызываться в рамках к.-л. транзакции. */
    public double calcCost (List<CartItem> citems) //TODO: перенести обратно в InMemoryCart.
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
    public void changeProductQuantity (Principal principal, String uuid, long productId, int delta)
    {
/*  Считаем, что нет смысла создавать новую товарн.позицию для неположительных значений delta. Кроме того, мы не ждём, что нам придёт значение delta == 0.
    Товар не резервируем, при добавлении его в корзину. */
        if (delta == 0)
            return;
        boolean ok = false;
        Product product = productService.findById (productId);

        CartsEntry ce = getUsersCartEntry (principal, uuid);
        CartItem ci = ce.imcart.getItemByPid (productId);

        if (delta < 0)
        {
            if (ci != null)
            {
                ok = ci.quantity > 0;
                delta += ci.quantity;
                ci.quantity = (delta < 0) ? 0 : delta;
            }
        }
        else //delta > 0
        {
            int rest = product.getRest();
            if (rest > 0)
            {
                if (ci == null)
                    ce.imcart.citems.add (ci = new CartItem (productId, 0));

                int initialQuantity = ci.quantity;
                ci.quantity += delta;

                if (rest < ci.quantity)
                    ci.quantity = rest;
                ok = initialQuantity != ci.quantity;
            }
        }
        if (ok)
            updateCart (ce);
        else
            throw new ResourceNotFoundException ("Не удалось изменить количество товара:\r"+ product);
    }

    @Transactional
    public void removeProductFromCart (Principal principal, String uuid, long productId)
    {
        CartsEntry ce = getUsersCartEntry (principal, uuid);
        if (ce.imcart.removeItemByProductId (productId))
            updateCart (ce);
    }

    @Transactional
    public void clearCart (Principal principal, String uuid)
    {
        clearCart (getUsersCartEntry (principal, uuid));
    }

    public void clearCart (String login)
    {
        clearCart (getUsersCartEntry(login, null));
    }

    private void clearCart (CartsEntry ce)
    {
        if (ce.imcart.clear())     //TODO: удалять корзины нужно поручить Memurai-ю.
            updateCart(ce);
    }

    public CartDto getUsersDryCartDto (String login)
    {
        return inMemoryCartToDto (getUsersCartEntry (login, null).imcart, DRYCART);
    }

    @NotNull
    private CartDto inMemoryCartToDto (InMemoryCart imcart, boolean dtycart)
    {
        CartDto cdto = new CartDto();
        for (CartItem ci : imcart.citems)
        {
            int quantity = ci.quantity;
            if (quantity > 0 || !dtycart)
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

    @Transactional
    public void mergeCarts (Principal principal, String uuid)
    {
        String postfixPr = null;
        String postfixUu = uuid == null ? null : validateString (uuid, LOGIN_LEN_MIN, LOGIN_LEN_MAX);

        if (postfixUu == null)
            throw new UnableToPerformException ("merge carts: не могу получить гостевую корзину.");

        if (principal != null)
            postfixPr = validateString (principal.getName(), LOGIN_LEN_MIN, LOGIN_LEN_MAX);

        if (postfixPr == null)
            throw new UnableToPerformException ("merge carts: вызов для НЕавторизованного пользователя.");

        if (redisTemplate.hasKey (Factory.cartKeyByLogin (postfixUu)))
        {
            CartsEntry ceUu = getUsersCartEntry (postfixUu, null);
            CartsEntry cePr = getUsersCartEntry (postfixPr, null); //< если корзины нет, она будет создана

            if (inlineMergeCarts (ceUu.imcart, cePr.imcart))
            {
                updateCart (cePr);
                /*ceUu.imcart.clear();
                updateCart (ceUu);*/
                redisTemplate.delete (ceUu.key); //TODO: это не удаляет корзину из кэша!!!!
            }
        }
    }

    private boolean inlineMergeCarts (@NotNull InMemoryCart srcCart, @NotNull InMemoryCart dstCart)
    {
        boolean ok = false;
        for (CartItem ciS : srcCart.citems)
        {
            if (ciS.quantity >= 0)
                ok = dstCart.add (ciS);
        }
        return ok;
    }

    public void removeNonEmptyItems(String login)
    {
        CartsEntry ce = getUsersCartEntry (login, null);
        if (ce.imcart.removeNonEmptyItems())
            updateCart(ce);
    }

    @SuppressWarnings ("all")
    public boolean deleteCart (String postfix)
    {
        String key = cartKeyByLogin (postfix);
        if (!redisTemplate.hasKey (key))
            return redisTemplate.delete (getUsersCartEntry (postfix, null).key);
        return false;
    }

    public int getCartItemsCount (Principal principal, String uuid)
    {
        CartsEntry ce = getUsersCartEntry (principal, uuid);
        return ce.imcart.citems.size();
    }
}
