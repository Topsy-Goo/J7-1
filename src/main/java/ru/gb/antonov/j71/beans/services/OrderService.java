package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.antonov.j71.beans.errorhandlers.AccessDeniedException;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;
import ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException;
import ru.gb.antonov.j71.beans.errorhandlers.UnauthorizedAccessException;
//import ru.gb.antonov.j71.beans.repositos.DeliveryTypeRepo;
import ru.gb.antonov.j71.beans.repositos.OrderItemRepo;
import ru.gb.antonov.j71.beans.repositos.OrdersRepo;
import ru.gb.antonov.j71.entities.*;
import ru.gb.antonov.j71.entities.dtos.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.gb.antonov.j71.Factory.ORDER_IS_EMPTY;
import static ru.gb.antonov.j71.Factory.orderCreationTimeToString;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepo         ordersRepo;
    private final OrderItemRepo      orderItemRepo;
    private final ProductService     productService;
    private final OurUserService     ourUserService;
    private final CartService        cartService;
    private final OrderStatesService orderStatesService;

//---------------------------------------------------------------------------------------
    @Transactional
    public OrderDetalesDto getOrderDetales (Principal principal) {

        OurUser ourUser    = ourUserService.userByPrincipal (principal);
        CartDto dryCartDto = cartService.getUsersDryCartDto (ourUser.getLogin());

        if (dryCartDto.getTitlesCount() <= 0)
            throw new BadCreationParameterException (ORDER_IS_EMPTY);

        OrderDetalesDto odt = new OrderDetalesDto();
        odt.setCartDto (dryCartDto);
        odt.setShippingInfoDto (new ShippingInfoDto());
        return odt;
    }

/** Вызывается из {@code OrderController} при получении запроса на оформление заказа. </p>

 <p>Всё время на странице заказа показываем юзеру товары, которые он заказал. Мы должны вернуть в
 {@code OrderDetalesDto} ту же {@code OrderDetalesDto}, но с дозаполненными полями, чтобы показать
 юзеру детали оформленного заказа.</p>

 @param detales содержит всю необходимую информацию для оформления заказа, включая «сухую» корзину.

 @return та же {@code OrderDetalesDto}, но с дозаполненными полями.
 */
    @Transactional
    public OrderDetalesDto applyOrderDetails (OrderDetalesDto detales, Principal principal) {

        CartDto cartDto = detales.getCartDto();
        if (cartDto.getTitlesCount() <= 0)
            throw new BadCreationParameterException (ORDER_IS_EMPTY);

        OurUser      ourUser      = ourUserService.userByPrincipal (principal);
        OrderState   oState       = orderStatesService.getOrderStatePending();
        ShippingInfo shippingInfo = ShippingInfo.fromShippingInfoDto (detales.getShippingInfoDto()).adjust();

        Order o = new Order();
        List<OrderItem> oitems = cartDto.getOitems()
                                        .stream()
                                        .map((dto)->orderItemFromDto(o, dto))
                                        .collect(Collectors.toList());
        o.setOrderItems (oitems);
        o.setAllItemsCost (cartDto.getCost()); //< только стоимость товаров
        o.setOuruser (ourUser);
        o.setState (oState);
        o.setShippingInfo (shippingInfo);
        ordersRepo.save (o);

        detales.setOrderNumber (o.getId());
        detales.setOrderState (oState.getFriendlyName());
        detales.setOrderCreationTime (orderCreationTimeToString(o.getCreatedAt()));

        //(Оставляем dryCart в OrderDetalesDto, чтобы юзер мог на неё посмотреть перед уходом со страницы заказа.)
        //Удаляем из корзины юзера НЕпустые позиции, — они были перенесены в dryCart. Пустые — оставляем, чтобы юзер их сам удалил, если захочет.
        cartService.removeNonEmptyItems(ourUser.getLogin());
        return detales;
    }

    private OrderItem orderItemFromDto (Order o, OrderItemDto dto) {

        if (o == null || dto == null)
            throw new BadCreationParameterException (
                "orderItemFromDto(): не удалось сформировать строку заказа.");

        OrderItem oi = new OrderItem();
        oi.setOrder(o);
        oi.setProduct(productService.findById(dto.getProductId()));
        oi.setBuyingPrice(dto.getPrice());
        oi.setQuantity(dto.getQuantity());
        return oi;
    }

    @Transactional
    public Collection<OrderDto> getUserOrdersAsOrderDtos (Principal principal) {

        OurUser              ourUser = ourUserService.userByPrincipal (principal);
        List<Order>          orders  = ordersRepo.findAllByOuruser (ourUser);
        Collection<OrderDto> list    = new ArrayList<> (orders != null ? orders.size() : 0);

        if (orders != null)
        for (Order o : orders)
            list.add(orderToDto(o));
        return list;
    }

/** Составляем DTO-шку для сделанного ранее заказа. Используется в лином кабинете пользователя
 для демонстрации пользователю списка его заказов.
 */
    private OrderDto orderToDto (Order o) {

        if (o == null)
            throw new BadCreationParameterException("Не удалось прочитать инф-цию о заказе.");

        OrderDto odto = new OrderDto();
        int[] oitemLoad = {0};  //< накопительный счётчик товаров в заказе

        odto.setOrderNumber (o.getId());
        odto.setState   (o.getState().getFriendlyName());
        odto.setAddress (o.getShippingInfo().getAddress());
        odto.setPhone   (o.getShippingInfo().getPhone());
        odto.setCost    (o.getAllItemsCost());
        odto.setOitems  (o.getOrderItems()
                          .stream()
                          .map((oi)->orderItemToDto(oi, oitemLoad))
                          .collect(Collectors.toList()));
        odto.setLoad (oitemLoad[0]);
        return odto;
    }

    private OrderItemDto orderItemToDto (OrderItem oi, int[] oitemLoad) {

        if (oi == null || oitemLoad == null || oitemLoad.length == 0)
            throw new BadCreationParameterException("Не удалось прочитать инф-цию об элементе заказа.");

        OrderItemDto oidto    = new OrderItemDto();
        BigDecimal   price    = oi.getBuyingPrice();
        int          quantity = oi.getQuantity();
        Product      product  = oi.getProduct();

        oidto.setProductId (product.getId());
        oidto.setCategory (product.getCategory().getName());
        oidto.setTitle (product.getTitle());
        oidto.setPrice (price);
        oidto.setQuantity (quantity);
        oidto.setCost (price.multiply (BigDecimal.valueOf (quantity)));
        oitemLoad[0] += quantity;
        return oidto;
    }

    public List<OrderItem> userOrderItemsByProductId (Long uid, Long pid, Integer stateId) {
        return orderItemRepo.userOrderItemsByProductId (uid, pid, stateId);
    }

    @Transactional
    public void payOrder (OrderDetalesDto orderDetalesDto, Principal principal) {

        if (orderDetalesDto == null)
            throw new BadCreationParameterException(null);

        if (principal == null)
            throw new UnauthorizedAccessException(null);

        Order   order   = ordersRepo.getById (orderDetalesDto.getOrderNumber());
        OurUser ourUser = ourUserService.userByPrincipal (principal);

        if (ourUser.getId().equals (order.getOuruser().getId())) order.setState (orderStatesService.getOrderStatePayed());
        else throw new AccessDeniedException (null);
    }
}
