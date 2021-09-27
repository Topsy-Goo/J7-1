package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;
import ru.gb.antonov.j71.beans.repositos.OrdersRepo;
import ru.gb.antonov.j71.entities.*;
import ru.gb.antonov.j71.entities.dtos.CartDto;
import ru.gb.antonov.j71.entities.dtos.OrderDetalesDto;
import ru.gb.antonov.j71.entities.dtos.OrderDto;
import ru.gb.antonov.j71.entities.dtos.OrderItemDto;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.gb.antonov.j71.Factory.orderCreationTimeToString;

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
/*    private OurUser userByPrincipal (Principal principal)
    {
        return ourUserService.userByPrincipal (principal);
    }*/

    @Transactional
    public OrderDetalesDto getOrderDetales (Principal principal)
    {
        OurUser ourUser = ourUserService.userByPrincipal (principal);
        CartDto dryCartDto = cartService.getUsersDryCartDto (ourUser.getLogin());
        OrderDetalesDto odt = new OrderDetalesDto();
        odt.setCartDto (dryCartDto);
        return odt;
    }

/** <p>Вызывается из {@code OrderController} при получении запроса на оформление заказа. </p>

    <p>Всё время на странице заказа показываем юзеру товары, которые он заказал. Мы должны вернуть в
    {@code OrderDetalesDto} ту же {@code OrderDetalesDto}, но с дозаполненными полями, чтобы показать
    юзеру детали оформленного заказа.</p>

    @param detales содержит всю необходимую информацию для оформления заказа, включая «сухую» корзину.

    @return та же {@code OrderDetalesDto}, но с дозаполненными полями.
*/
    @Transactional
    public OrderDetalesDto applyOrderDetails (OrderDetalesDto detales, Principal principal)
    {
        CartDto cartDto = detales.getCartDto();
        OurUser ourUser = ourUserService.userByPrincipal (principal);
        OrderState oState = orderStatesService.getStatePending();

        Order o = new Order();
        o.setState (oState);
        o.setOuruser (ourUser);
        o.setPhone (detales.getPhone());
        o.setAddress (detales.getAddress());
        o.setOrderItems (cartDto.getOitems()
                                .stream()
                                .map ((dto)->orderItemFromDto (o, dto))
                                .collect (Collectors.toList()));
        o.setCost (cartDto.getCost());
        ordersRepo.save (o);

        detales.setOrderNumber (o.getId());
        detales.setOrderState (oState.getFriendlyName());
        detales.setOrderCreationTime (orderCreationTimeToString (o.getCreatedAt()));
        //detales.setDeliveryType ("Самовывоз");  TODO: сделать ниспадающий список на стр.оформления заказа.
        //detales.setDeliveryCost (0.0);        //TODO: поменять на чтение стоимости выбранной доставки.
        detales.setOverallCost (o.getCost() + detales.getDeliveryCost());

        //(Оставляем dryCart в OrderDetalesDto, чтобы юзер мог на неё посмотреть перед уходом со
        // страницы заказа.)
        //Удаляем из корзины юзера НЕпустые позиции, — они были перенесены в dryCart. Пустые —
        // оставляем, чтобы юзер их сам удалил, если захочет:
        cartService./*clearCart*/removeNonEmptyItems (ourUser.getLogin());
        return detales;
    }

    private OrderItem orderItemFromDto (Order o, OrderItemDto dto)
    {
        if (o == null || dto == null)
            throw new BadCreationParameterException ("orderItemFromDto(): не удалось сформировать строку заказа.");

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
        OurUser ourUser    = ourUserService.userByPrincipal (principal);
        List<Order> orders = ourUser.getOrders();
        Collection<OrderDto> list = new ArrayList<>((orders != null) ? orders.size () : 0);

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

        OrderDto odto = new OrderDto();
        int[] oitemLoad = {0};  //< накопительный счётчик товаров в заказе

        odto.setOrderNumber (o.getId());
        odto.setState (o.getState().getFriendlyName());
        odto.setAddress (o.getAddress());
        odto.setPhone (o.getPhone());
        odto.setCost (o.getCost());
        odto.setOitems (o.getOrderItems()
                        .stream()
                        .map ((oi)->orderItemToDto (oi, oitemLoad))
                        .collect (Collectors.toList()));
        odto.setLoad (oitemLoad[0]);
        return odto;
    }

    private OrderItemDto orderItemToDto (OrderItem oi, int[] oitemLoad)
    {
        if (oi == null || oitemLoad == null || oitemLoad.length == 0)
            throw new BadCreationParameterException ("Не удалось прочитать инф-цию об элементе заказа.");

        OrderItemDto oidto = new OrderItemDto();
        double price = oi.getBuyingPrice();
        int quantity = oi.getQuantity();
        Product product = oi.getProduct();

        oidto.setProductId (product.getId());
        oidto.setCategory (product.getCategory().getName());
        oidto.setTitle (product.getTitle());
        oidto.setPrice (price);
        oidto.setQuantity (quantity);
        oidto.setCost (price * quantity);
        oitemLoad[0] += quantity;
        return oidto;
    }
}
