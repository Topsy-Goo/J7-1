package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException;
import ru.gb.antonov.j71.beans.utils.Cart;
import ru.gb.antonov.j71.entities.OurUser;
import ru.gb.antonov.j71.entities.Product;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import static ru.gb.antonov.j71.Factory.cartKeyByLogin;

@Service
@RequiredArgsConstructor
public class CartService
{
    private final ProductService productService;
    private final OurUserService ourUserService;
    private final RedisTemplate<String, Object> redisTemplate;
    private Cart ccc;
//--------------------------------------- classes
    public static class CartsEntry
    {
        private final String key;
        private final Cart cart;

        private CartsEntry (String k, Cart c)
        {
            key = k;
            cart = c;
        }
        public Cart getCart() { return cart; }
    }
    private static class CartItem
    {
        private long pid;
        private int quantity;
    }
    private static class InMemoryCart
    {
        List<CartItem> citems;
        int load;

    }
//------------------------------------------------------------------------

    private OurUser userByPrincipal (Principal principal)
    {
        return ourUserService.userByPrincipal (principal);
    }

//---------------------------------------
/*  private TreeMap<Long, Cart> carts;

    @PostConstruct
    public void init() //черновик
    {
        this.carts = new TreeMap<>();
    }*/
//-------------------- Временная реализация (начало) ---------------------

    @PostConstruct
    public void init()
    {
        ccc = new Cart();
    }

    @Transactional
    public Cart/*sEntry*/ getUsersCart (Principal principal)
    {
        return getUsersCart (userByPrincipal (principal));
    }

    private Cart/*sEntry*/ getUsersCart (OurUser ourUser)
    {
        return ccc;
/*        if (ourUser == null)
            throw new UnableToPerformException ("getUsersCart(): нет юзера — нет корзины!");

        String key = cartKeyByLogin (ourUser.getLogin());
        if (!redisTemplate.hasKey (key))
        {
            redisTemplate.opsForValue().set(key, new Cart());
        }
        Cart cart = (Cart) redisTemplate.opsForValue().get(key);
        if (cart == null)
            throw new UnableToPerformException ("getUsersCart(): не могу извлечь корзину пользователя: "
                                                + ourUser.getLogin());
        return new CartsEntry (key, cart);*/
    }

    private void updateCart (CartsEntry cartsEntry)
    {
        redisTemplate.opsForValue().set (cartsEntry.key, cartsEntry.cart);
    }
//---------------------------------------------------------------------
    @Transactional
    public int getCartLoad (Principal principal)
    {
        return getUsersCart (principal).cart.getLoad();
    }

    @Transactional
    public double getCartCost (Principal principal)
    {
        return getUsersCart (principal).cart.getCost();
    }

    @Transactional
    public void changeProductQuantity (long productId, int delta, Principal principal)
    {
        //(Считаем, что нет смысла создавать новую товарн.позицию для неположительных значений delta.)
        Function<Long, Product> f = (delta <= 0) ? null : new Function<>()
                {
                    public Product apply (Long id)
                    {
                        return productService.findById (productId);
                    }
                };
        CartsEntry ce = getUsersCart (principal);
        if (!ce.cart.changeQuantity (productId, f, delta))
        {
            throw new UnableToPerformException ("Не удалось изменить количество для: pID."+ productId);
        }
        updateCart (ce);
    }

    @Transactional
    public void removeProduct (long productId, Principal principal)
    {
        CartsEntry ce = getUsersCart (principal);
        ce.cart.removeProduct (productId);
        updateCart (ce);
    }

    @Transactional
    public void clearCart (Principal principal)
    {
        clearCart (userByPrincipal (principal));
    }

    //вызывается в рамках транзакции (в т.ч. из OrderService.applyOrderDetails())
    public void clearCart (OurUser ourUser) //черновик
    {
        CartsEntry ce = getUsersCart (ourUser);
        ce.cart.clear();
        redisTemplate.delete (ce.key); ??????????
    }

    public Cart getUsersDryCart (OurUser ourUser)
    {
        return getUsersCart (ourUser).cart.fillDrylCart();
    }

    public void updateProductInCarts (Product product) //черновик
    {
        if (carts == null) return;

        Set<Long> keyset = carts.keySet();
        for (Long uid : keyset)
        {
            Cart cart = carts.get (uid);
            if (cart != null)
                cart.updateProduct (product);
        }
    }

    @Transactional
    public void deleteById (Long productId) //черновик
    {
        //TODO: Редактирование товаров пока не входит в план проекта.
        //TODO: (Кажется, из базы не удаляются даже самые древние товары.
        //       И на фронте это сложно как-то провернуть.)
        productService.onProductDeletion (productId);
        //TODO: «Удалённый» товар может находиться в одной или нескольких корзинах.
    }
//-------------------- Временная реализация (конец) ----------------------
}
