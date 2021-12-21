package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;
import ru.gb.antonov.j71.entities.Product;

import java.math.BigDecimal;

@Data
public class OrderItemDto {

    private Long       productId;
    private String     title;
    private BigDecimal price = BigDecimal.ZERO;
    private int        quantity;
    private String     measure;     //< единицы измерения есть OrderItemDto, но отсутствуют в OrderItem
    private int        rest;
    private String     category;
    private BigDecimal cost = BigDecimal.ZERO;
//------------------------------------------------------------------
    public OrderItemDto () {}
    public OrderItemDto (Product p) {  //< создаём «пустой» объект : без количества и общей стоимости.

        if (p == null)
            throw new BadCreationParameterException ("A new OrderItemDto() have got null as parameter.");
        productId = p.getId();
        updateFromProduct (p);
    }
//--------- Геттеры и сеттеры (JSON работает с публичными полями!) --------------

/** Возвращает true, если количество было изменено. */
    public boolean setQuantity (int newQuantity) {

        boolean ok = newQuantity >= 0 && quantity != newQuantity;
        if (ok)
            quantity = newQuantity;
        return ok;
    }

    public BigDecimal getCost () { return price.multiply(BigDecimal.valueOf(quantity)); }
//----------------- Другие методы ----------------------------------

/** Возвращает true, если количество было изменено. */
    public boolean changeQuantity (int delta) {   return setQuantity (quantity + delta);   }

    //private void calcCost () {   setCost (price * quantity);   }

    public boolean updateFromProduct (Product p) {
        if (productId.equals (p.getId())) { //< TODO: выглядит немного избыточно!

            title     = p.getTitle();
            price     = p.getPrice();
            rest      = p.getRest();
            measure   = p.getMeasure().getName();
            category  = p.getCategory().getName();
            //Следующие поля не нужно заполнять при создании объекта, а при обновлении их заполнять ещё и не рекомендуется!
            //quantity = ?;
            //cost = ?;
            return true;
        }
        return false;
    }
}
