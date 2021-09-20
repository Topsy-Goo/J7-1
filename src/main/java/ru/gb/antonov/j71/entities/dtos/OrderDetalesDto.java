package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.gb.antonov.j71.beans.utils.Cart;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class OrderDetalesDto
{
    @NotNull (message="\nПолучена пустая корзина.\rЗаказ не может быть оформлен.")
    private Cart cart;

    @NotNull (message="\nУкажите номер телефона.")
    @Length (/*min=10, max=16,*/ message="\nНомер телефона должен содержать 10 цифр.\rПример: 8006004050.")
    private String phone;

    @NotNull (message="\rУкажите адрес доставки.")
    @Length (max=255, message="\nМаксимальная длина адреса — 255 символов.")
    private String address;

    private Long          orderNumber;
//  private LocalDateTime orderCreatedAt;
    private String        orderCreationTime;
//  private String        deliveryType; TODO: Кажется, это несложно.
    private String        orderState;
    private double        deliveryCost;
    private double        overallCost;
/*  @Length (max=255, message="Максимальная длина текста комментария — 255 символов.")
    private String comment;*/
}
