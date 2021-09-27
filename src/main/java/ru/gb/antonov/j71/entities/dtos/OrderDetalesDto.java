package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import static ru.gb.antonov.j71.Factory.*;

@Data
@NoArgsConstructor
public class OrderDetalesDto
{
    @NotNull (message="\rПолучена пустая корзина.\rЗаказ не может быть оформлен.")
    private CartDto cartDto;

    @NotNull (message="\rУкажите номер телефона.")
    @Length (min=DELIVERING_PHONE_LEN_MIN, max=DELIVERING_PHONE_LEN_MAX, message="\rНомер телефона должен содержать 10…16 цифр.\rПример: 8006004050.")//TODO: поправить длину номера.
    private String phone;

    @NotNull (message="\rУкажите адрес доставки.")
    @Length (max=DELIVERING_ADDRESS_LEN_MAX, message="\rМаксимальная длина адреса — 255 символов.")
    private String address;

    private Long          orderNumber;
//  private LocalDateTime orderCreatedAt;
    private String        orderCreationTime;
//  private String        deliveryType;
    private double        deliveryCost;
    private String        orderState;
    private double        overallCost;

/*  @Length (max=255, message="Максимальная длина текста комментария — 255 символов.")
    private String comment;*/
}
