package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.gb.antonov.j71.beans.utils.Cart;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderDetalesDto
{
    @NotNull (message="Получена пустая корзина.\rЗаказ не может быть оформлен.")
    private Cart cart;

    @NotNull (message="Укажите номер телефона.")
    @Length (/*min=10, max=16,*/ message="Номер телефона должен содержать 10 цифр.\rПример: 8006004050.")
    private String phone;

    @NotNull (message="Укажите адрес доставки.")
    @Length (max=255, message="Максимальная длина адреса — 255 символов.")
    private String address;

    private Long          orderNumber;
    private LocalDateTime orderCreatedAt;
    //private String        deliveryType;
    private double        deliveryCost;
    private double        overallCost;
/*    @Length (max=255, message="Максимальная длина текста комментария — 255 символов.")
    private String comment;*/
}
