package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException;
import ru.gb.antonov.j71.beans.utils.Cart;
import ru.gb.antonov.j71.entities.OurUser;
import ru.gb.antonov.j71.entities.Product;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CartService
{
    private final ProductService productService;
    private final OurUserService ourUserService;
//-------------------- Временная реализация (начало) ---------------------
    private Cart cart;

    @PostConstruct
    public void init() {   this.cart = new Cart();   }

    private OurUser userFromPrincipal (Principal principal) //черновик
    {
        OurUser ourUser = ourUserService.findUserByPrincipal (principal);
        //System.out.printf ("\n*********mylog********* CartController.userFromPrincipal returns %s\n\n", ourUser);
        return ourUser;
    }

    @Transactional
    public Cart getUsersCart (Principal principal)
    {
        OurUser ourUser = userFromPrincipal (principal);
        return getUsersCart (ourUser);
    }

    private Cart getUsersCart (OurUser ourUser) { return cart; } //черновик

    @Transactional
    public int getCartLoad (Principal principal) //черновик
    {
        OurUser ourUser = userFromPrincipal (principal);
        return cart.getLoad();
    }

    @Transactional
    public double getCartCost (Principal principal) //черновик
    {
        OurUser ourUser = userFromPrincipal (principal);
        return cart.getCost();
    }

    @Transactional
    public void changeProductQuantity (long productId, int delta, Principal principal) //черновик
    {
        //(Считаем, что нет смысла создавать новую товарн.позицию для неположительных значений delta.)
        Function<Long, Product> f = (delta <= 0) ? null : new Function<>()
                {
                    public Product apply (Long id)
                    {
                        return productService.findById (productId);
                    }
                };
        OurUser ourUser = userFromPrincipal (principal);
        if (!cart.changeQuantity (productId, f, delta))
        {
            throw new UnableToPerformException ("Не удалось изменить количество для: pID."+ productId);
        }
    }

    @Transactional
    public void removeProduct (long productId, Principal principal) //черновик
    {
        OurUser ourUser = userFromPrincipal (principal);
        cart.removeProduct (productId);
    }

    @Transactional
    public void clearCart (Principal principal) //черновик
    {
        OurUser ourUser = userFromPrincipal (principal);
        clearCart (ourUser);
    }

    //вызывается в рамках транзакции (в т.ч. из OrderService.applyOrderDetails())
    public void clearCart (OurUser ourUser) //черновик
    {
        cart.clear();
    }

    public Cart getUsersDryCart (OurUser ourUser) //черновик
    {
        Cart dryCart = new Cart();
        getUsersCart (ourUser).fillDrylCart (dryCart);
        return dryCart;
    }

    public void deleteById (Long productId) //черновик
    {
        //TODO: (Кажется, из базы не удаляются даже самые древние товары.
        //      И на фронет это сложно как-то провернуть.)
    }
//-------------------- Временная реализация (конец) ----------------------
}
