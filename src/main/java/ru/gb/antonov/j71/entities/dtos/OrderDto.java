package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDto
{
    private Long               orderNumber;
    private String             address;
    private String             phone;
    private BigDecimal         cost;    //< общая стоимость выбранных/купленных товаров
    private List<OrderItemDto> oitems;
    private int                load;       //< общее количество единиц выбранных/купленных товаров
    private String             state;

    public OrderDto ()
    {   cost = BigDecimal.ZERO;
    }
}
