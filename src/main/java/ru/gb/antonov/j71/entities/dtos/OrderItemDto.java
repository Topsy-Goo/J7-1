package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;
import ru.gb.antonov.j71.entities.Product;

@Data
public class OrderItemDto
{
//TODO: Кажется, здесь validator.constraints не нужны, т.к. ожидаем, что все поля будут заполнятся НЕ юзером.
    private Long productId;
    private String category;
    private String title;
    private double price;
    private int    quantity;
    private double cost;
//------------------------------------------------------------------

    private OrderItemDto () {}
    public OrderItemDto (Product p)  //< создаём «пустой» объект : без количества и цен.
    {
        if (p == null)
            throw new BadCreationParameterException ("new OrderItemDto() have got null as parameter.");
        productId = p.getId();
        title     = p.getTitle();
        category  = p.getCategory ().getName ();
        price     = p.getPrice();
        //quantity = 0;
        //cost = 0;
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
/*
    public void setProductId (Long productId) {   this.productId = productId;   }
    public void setTitle (String newtitle) {   title = newtitle;  }
    public void setCategory (String name)  {   category = name;   }
    public void setPrice (double newvalue) {   price = newvalue;  }
    public void setCost (double newValue)  {   cost = newValue;   }*/

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
}
