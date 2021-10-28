package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;
import ru.gb.antonov.j71.entities.Product;

@Data
public class OrderItemDto
{
    private Long   productId;
    private String category;
    private String title;
    private double price;
    private int    rest;
    private int    quantity;
    private double cost;
//------------------------------------------------------------------

    public OrderItemDto () {}

    public OrderItemDto (Product p)  //< создаём «пустой» объект : без количества и общей стоимости.
    {
        if (p == null)
            throw new BadCreationParameterException ("A new OrderItemDto() have got null as parameter.");
        productId = p.getId();
        updateFromProduct (p);
    }

/*    public OrderItemDto (OrderItemDto oi) //TODO: проверить, используется ли этот метод ?
    {
        productId = oi.productId;
        title     = oi.title;
        category  = oi.category;
        price     = oi.price;
        rest      = oi.rest;
        quantity  = oi.quantity;
        cost      = oi.cost;
    }*/
//--------- Геттеры и сеттеры (JSON работает с публичными полями!) --------------

/** Возвращает true, если количество было изменено. */
    public boolean setQuantity (int newQuantity)
    {
        boolean ok = newQuantity >= 0 && quantity != newQuantity;
        if (ok)
            quantity = newQuantity;
        return ok;
    }

    public double getCost () { return price * quantity; }
//----------------- Другие методы ----------------------------------

/** Возвращает true, если количество было изменено. */
    public boolean changeQuantity (int delta) {   return setQuantity (quantity + delta);   }

    //private void calcCost () {   setCost (price * quantity);   }

    public boolean updateFromProduct (Product p)
    {
        if (productId.equals (p.getId())) //< TODO: выглядит немного избыточно!
        {
            title     = p.getTitle();
            category  = p.getCategory().getName();
            price     = p.getPrice();
            rest      = p.getRest();
    //Следующие поля не нужно заполнять при создании объекта, а при обновлении их заполнять ещё и не рекомендуется!
            //quantity = ?;
            //cost = ?;
            return true;
        }
        return false;
    }
}
