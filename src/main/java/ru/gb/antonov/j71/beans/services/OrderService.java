package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;
import ru.gb.antonov.j71.beans.repositos.OrdersRepo;
import ru.gb.antonov.j71.beans.utils.Cart;
import ru.gb.antonov.j71.entities.Order;
import ru.gb.antonov.j71.entities.OrderItem;
import ru.gb.antonov.j71.entities.OurUser;
import ru.gb.antonov.j71.entities.dtos.OrderDetalesDto;
import ru.gb.antonov.j71.entities.dtos.OrderItemDto;

import java.security.Principal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService
{
    private final CartService cartService;
    private final OrdersRepo ordersRepo;
    private final OurUserService ourUserService;
    private final ProductService productService;
//---------------------------------------------------------------------------------------

    private OurUser userFromPrincipal (Principal principal) //черновик
    {
        OurUser ourUser = ourUserService.findUserByPrincipal (principal);
        //System.out.printf ("\n*********mylog********* CartController.userFromPrincipal returns %s\n", ourUser);
        return ourUser;
    }

    @Transactional
    public OrderDetalesDto getOrderDetales (Principal principal) //черновик
    {
        OurUser ourUser = userFromPrincipal (principal);
        Cart dryCart = cartService.getUsersDryCart (ourUser);
        OrderDetalesDto odt = new OrderDetalesDto();
        odt.setCart (dryCart);
        return odt;
    }

    @Transactional
    public OrderDetalesDto applyOrderDetails (OrderDetalesDto detales, Principal principal)
    {
        Cart cart = detales.getCart();
        OurUser ourUser = userFromPrincipal (principal);
        Order o = new Order();

        o.setOuruser (ourUser);
        o.setPhone (detales.getPhone());
        o.setAddress (detales.getAddress());
        o.setOrderItems (cart.getOitems()
                             .stream()
                             .map ((dto)->orderItemFromDto (o, dto))
                             .collect (Collectors.toList()));
        //o.setState (statesService.getStatePending());
        ordersRepo.save (o);
        // чтобы юзер мог на неё посмотреть перед уходом со страницы заказа.

        detales.setOrderNumber (o.getId());
        detales.setOrderCreatedAt (o.getCreatedAt());
        //detales.setDeliveryType ("Самовывоз");
        detales.setDeliveryCost (0.0);
        detales.setOverallCost (detales.getCart().getCost() + detales.getDeliveryCost());

        cartService.clearCart (ourUser); //< очищаем корзину юзера, но оставляем dryCart в OrderDetalesDto,
        return detales;
    }

    private OrderItem orderItemFromDto (Order o, OrderItemDto dto)
    {
        if (o == null || dto == null)
            throw new BadCreationParameterException ("ой!..");

        OrderItem oi = new OrderItem();
        oi.setOrder(o);
        oi.setProduct (productService.findById (dto.getProductId()));
        oi.setByingPrice (dto.getPrice());
        oi.setQuantity (dto.getQuantity());
        return oi;
    }
}
