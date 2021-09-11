package ru.gb.antonov.j71.beans.utils;

import lombok.Data;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.dtos.OrderItemDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static ru.gb.antonov.j71.Factory.RECALC_CART;

//@Data
public class Cart
{
    private List<OrderItemDto> items;   //TODO: Очень хочется сделать эту коллекцию Set'ом !!! А на фронт можно передавать страницы Dto'шек.
    private double totalPrice;

    public Cart() {   this.items = new ArrayList<>();   }
//-------------- Геттеры и сеттеры (в основном для JSON'ов) ---------

    private List<OrderItemDto> getItems() {   return Collections.unmodifiableList (items);   }

    private void setItems (List<OrderItemDto> list) {   if (list != null)   items = list;   }

    private void setTotalPrice (double newvalue) {   this.totalPrice = newvalue;   }

    public double getTotalPrice() {   return (RECALC_CART) ? totalPrice : calculateCartCost();   }

//-------------- Другие методы --------------------------------------

    public Optional<OrderItemDto> findByProductId (long productId)
    {
        //return items.stream().filter((oid)->oid.getProductId().equals(productId)).findFirst();
        for (OrderItemDto oitem : items)
        {
            if (oitem.getProductId().equals (productId))
                return Optional.of (oitem);
        }
        return Optional.empty();
    }

/* Метод добавляет в корзину OrderItemDto, соответствующий товару, и возвращает OrderItemDto. */
    private Optional<OrderItemDto> addProduct (Product product)
    {
        if (product == null)
            return Optional.empty();

        long pid = product.getId ();
        OrderItemDto oitem = findByProductId (pid).orElse (null);

        if (oitem == null)
        {
            oitem = new OrderItemDto (product);
            items.add (oitem);
        }
        return Optional.of (oitem);
    }

/* Метод удаляет продукт из корзины. Возвращает удалённую OrderItemDto, или null, если ничего не было
   удалено. */
    public Optional<OrderItemDto> removeProduct (long productId)
    {
        OrderItemDto result = findByProductId (productId).orElse (null);
        if (result != null && items.remove (result))
        {
            if (RECALC_CART)   totalPrice = calculateCartCost();
        }
        return Optional.ofNullable (result);
    }

    public boolean changeQuantity (long productId, int delta)
    {
        boolean ok = false;
        OrderItemDto oitem = findByProductId (productId).orElse (null);

        if (oitem != null)
        {
            ok = delta == 0;
            if (!ok)
            {
                ok = oitem.changeQuantity (delta);
                if (oitem.getQuantity() <= 0)
                {
                    items.remove (oitem);
                }
                if (RECALC_CART && ok)   totalPrice = calculateCartCost();
            }
        }
        return ok;
    }

    public double calculateCartCost ()
    {
        double sum = 0.0;
        for (OrderItemDto oitem : items)
            sum += oitem.getQuantityCost();

        return sum;
    }

    public void clear ()
    {
        items.clear();
        totalPrice = 0;
    }
}
