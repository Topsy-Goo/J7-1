package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrderDto
{
    private Long orderNumber;
    private String address;
    private String phone;
    private double cost;    //< общая стоимость выбранных/купленных товаров
    private List<OrderItemDto> oitems;
    private int load;       //< общее количество единиц выбранных/купленных товаров
    private String state;
}
