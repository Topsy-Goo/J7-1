package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.gb.antonov.j71.Factory.*;

@Data
public class OrderDetalesDto {

    private Long            orderNumber;
    private String          orderCreationTime;
    private String          orderState;
    private ShippingInfoDto shippingInfoDto;

    @NotNull (message="\rПолучена пустая корзина.\rЗаказ не может быть оформлен.")
    private CartDto         cartDto;
//-------------------------------------------------------------------------------------
    public OrderDetalesDto () {}

    public static OrderDetalesDto dummyOrderDetalesDto (ShippingInfoDto sidto) {

        OrderDetalesDto oddto = new OrderDetalesDto();
        LocalDateTime ldt       = LocalDateTime.now();
        oddto.cartDto           = CartDto.dummyCartDto();
        oddto.shippingInfoDto   = sidto;
        oddto.orderNumber       = 0L;
        oddto.orderCreationTime = orderCreationTimeToString(ldt);
        oddto.orderState        = STR_EMPTY;
        return oddto;
    }
}
