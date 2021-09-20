package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;
import ru.gb.antonov.j71.beans.repositos.OrdersRepo;
import ru.gb.antonov.j71.beans.utils.Cart;
import ru.gb.antonov.j71.entities.*;
import ru.gb.antonov.j71.entities.dtos.OrderDetalesDto;
import ru.gb.antonov.j71.entities.dtos.OrderDto;
import ru.gb.antonov.j71.entities.dtos.OrderItemDto;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.gb.antonov.j71.Factory.*;

@Service
@RequiredArgsConstructor
public class OrderService
{
    private final CartService cartService;
    private final OrdersRepo ordersRepo;
    private final OurUserService ourUserService;
    private final ProductService productService;
    private final OrderStatesService orderStatesService;
//---------------------------------------------------------------------------------------

/** Метод бросает исключение {@code UserNotFoundException}, если не может достать {@code OurUser} из БД.
 */
    private OurUser userByPrincipal (Principal principal)
    {
        return ourUserService.userByPrincipal (principal);
    }

    @Transactional
    public OrderDetalesDto getOrderDetales (Principal principal)
    {
        OurUser ourUser = userByPrincipal (principal);
        Cart dryCart = cartService.getUsersDryCart (ourUser);
        OrderDetalesDto odt = new OrderDetalesDto();
        odt.setCart (dryCart);
        return odt;
    }

    @Transactional
    public OrderDetalesDto applyOrderDetails (OrderDetalesDto detales, Principal principal)
    {
        Cart cart = detales.getCart();
        OurUser ourUser = userByPrincipal (principal);
        OrderState oState = orderStatesService.getOrderStateByShortName (ORDERSTATE_PENDING);
        Order o = new Order();

        o.setState (oState);
        o.setOuruser (ourUser);
        o.setPhone (detales.getPhone());
        o.setAddress (detales.getAddress());
        o.setOrderItems (cart.getOitems()
                             .stream()
                             .map ((dto)->orderItemFromDto (o, dto))
                             .collect (Collectors.toList()));
        o.setCost (cart.getCost());
        //o.setState (statesService.getStatePending());

        ordersRepo.save (o);

/*  Всё время на странице заказа показываем юзеру товары, которые он заказал. Мне не сложно,
    а у юзера есть возможность кинуть взгляд на заказанные товары. К тому же отправку сообщения
    на почту мы ещё не сделали.
*/
        detales.setOrderNumber (o.getId());
        detales.setOrderState (oState.getFriendlyName());
        detales.setOrderCreationTime (orderCreationTimeToString (o.getCreatedAt()));
        detales.setDeliveryCost (0.0);
        detales.setOverallCost (detales.getCart().getCost() + detales.getDeliveryCost());
        //detales.setDeliveryType ("Самовывоз");

        cartService.clearCart (ourUser); //< очищаем корзину юзера, но оставляем dryCart в OrderDetalesDto, чтобы юзер мог на неё посмотреть перед уходом со страницы заказа.
        return detales;
    }

    private OrderItem orderItemFromDto (Order o, OrderItemDto dto)
    {
        if (o == null || dto == null)
            throw new BadCreationParameterException ("ой!..");

        OrderItem oi = new OrderItem();
        oi.setOrder(o);
        oi.setProduct (productService.findById (dto.getProductId()));
        oi.setBuyingPrice (dto.getPrice());
        oi.setQuantity (dto.getQuantity());
        return oi;
    }

    @Transactional
    public Collection<OrderDto> getUserOrdersAsOrderDtos (Principal principal)
    {
        OurUser ourUser    = userByPrincipal (principal);
        List<Order> orders = ourUser.getOrders();
        Collection<OrderDto> list   = newOrderDtosCollection ((orders != null) ? orders.size() : 0);

        if (orders != null)
        for (Order o : orders)
        {
            list.add (orderToDto (o));
        }
        return list;
    }

    private OrderDto orderToDto (Order o)
    {
        if (o == null)
            throw new BadCreationParameterException ("Не удалось прочитать инф-цию о заказе.");

        OrderDto dto = new OrderDto();
        int[] oitemLoad = {0};  //< накопительный счётчик товаров в заказе

        dto.setOrderNumber (o.getId());
        dto.setState (o.getState().getFriendlyName());
        dto.setAddress (o.getAddress());
        dto.setPhone (o.getPhone());
        dto.setCost (o.getCost());
        dto.setOitems (o.getOrderItems()
                        .stream()
                        .map ((oi)->orderItemToDto (oi, oitemLoad))
                        .collect (Collectors.toList()));
        dto.setLoad (oitemLoad[0]);
        return dto;
    }

    private OrderItemDto orderItemToDto (OrderItem oi, int[] oitemLoad)
    {
        if (oi == null || oitemLoad == null || oitemLoad.length == 0)
            throw new BadCreationParameterException ("Не удалось прочитать инф-цию об элементе заказа.");

        OrderItemDto dto = new OrderItemDto();
        double price = oi.getBuyingPrice();
        int quantity = oi.getQuantity();
        Product product = oi.getProduct();

        dto.setProductId (product.getId());
        dto.setCategory (product.getCategory().getName());
        dto.setTitle (product.getTitle());
        dto.setPrice (price);
        dto.setQuantity (quantity);
        dto.setCost (price * quantity);
        oitemLoad[0] += quantity;
        return dto;
    }
}
