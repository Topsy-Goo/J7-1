package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;
import ru.gb.antonov.j71.entities.OrderItem;
import ru.gb.antonov.j71.entities.Product;

import java.io.Serializable;

@Data
public class OrderItemDto implements Serializable
{
//TODO: Кажется, здесь validator.constraints не нужны, т.к. ожидаем, что все поля будут заполнятся НЕ юзером.
    private Long productId;
    private String category;
    private String title;
    private double price;
    private int    quantity;
    private double cost;
    public static final long serialVersionUID = 1L;
//------------------------------------------------------------------

    public OrderItemDto ()
    {}
    public OrderItemDto (Product p)  //< создаём «пустой» объект : без количества и цен.
    {
        if (p == null)
            throw new BadCreationParameterException ("A new OrderItemDto() have got null as parameter.");
        productId = p.getId();
        updateFromProduct (p);
    }
    public OrderItemDto (OrderItemDto oi)
    {
        productId = oi.productId;
        title     = oi.title;
        category  = oi.category;
        price     = oi.price;
        quantity  = oi.quantity;
        cost      = oi.cost;
    }
//--------- Геттеры и сеттеры (JSON работает с публичными полями!) --------------

    public boolean setQuantity (int newQuantity)
    {
        boolean ok = newQuantity >= 0;
        if (ok)
        {
            quantity = newQuantity;
            calcCost();
        }
        return ok;
    }
//----------------- Другие методы ----------------------------------

    public boolean changeQuantity (int delta) {   return setQuantity (quantity + delta);   }

    private void calcCost () {   setCost (price * quantity);   }

    public void updateFromProduct (Product p)
    {
        if (productId.equals (p.getId())) //< TODO: выглядит немного избыточно!
        {
            title     = p.getTitle();
            category  = p.getCategory().getName();
            price     = p.getPrice();
    //Следующие поля не нужно заполнять при создании объекта, а при обновлении их заполнять ещё и не рекомендуется!
            //quantity = ?;
            //cost = ?;
        }
    }
}
