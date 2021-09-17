package ru.gb.antonov.j71.beans.utils;

import lombok.Setter;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.dtos.OrderItemDto;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.gb.antonov.j71.Factory.RECALC_CART;

//@Data
public class Cart
{
    private List<OrderItemDto> oitems;
    @Setter private double cost;
    @Setter private int load;
    @Setter private int titlesCount;

    public Cart() {   oitems = new ArrayList<>();   }
    //public Cart (List<OrderItemDto> list) {   oitems = list;   }

//-------------- Геттеры и сеттеры (JSON работает с публичными полями!) ----------------

    public List<OrderItemDto> getOitems ()          {   return  Collections.unmodifiableList (oitems);   }
    public void setOitems (List<OrderItemDto> list) {   if (list != null)   oitems = list;   }

    public double getCost () {   return (RECALC_CART) ? cost : calcCost();   }
    public int getLoad () {   return (RECALC_CART) ? load : calcLoad();   }

    public int getTitlesCount () {   return oitems.size();   }

//-------------- Другие методы --------------------------------------

/* (Если этот метод будет что-то возвращать, то он будет вызываться бесконечное количество раз.
    А размещать его вне класса не велит метод getOitems(), который возвращает unmodifiableList.)
*/
    public Cart fillDrylCart ()
    {
        Cart drycart = new Cart();
        for (OrderItemDto oi : oitems)
        {
            if (oi.getQuantity() > 0)
                drycart.oitems.add (oi); //< вне класса так сделать не получится у unmodifiableList'а.
        }
        drycart.cost = cost;
        drycart.load = load;
        return drycart;
    }

    public String toString()
    {   return String.format("Cart:[cost:%.2f, titles:%d, load:%d].oitems:%s",
        cost, titlesCount, load, oitems);
    }
//Функция «Всё-в-одном» : ищем запись с productId, а если не нашли, то создаём её (если вызывающая сторона
//  снабдила нас корректным значением function и положительным значением delta). В найденноом/созданом
//  элементе изменяем количетво.
    public boolean changeQuantity (long productId, Function<Long, Product> function, int delta)
    {
        for (OrderItemDto oitem : oitems)
        {
            if (oitem.getProductId().equals (productId))
                return oitem.changeQuantity (delta);
            /* Если у товара количество обнулилось, то не удаляем его из Cart.oitems, чтобы юзер мог
  снова его добавить, не выходя из «Корзины».  */
        }
        if (function != null && delta > 0)
        {
            Product product = function.apply (productId); //< function бросает ResourceNotFoundException
            return addProduct (product, delta);
        }
        return false;
    }

    private boolean addProduct (Product product, int quantity)
    {
        OrderItemDto oitem = new OrderItemDto (product);
        return oitem.setQuantity (quantity) && oitems.add (oitem);
    }

    private OrderItemDto findByProductId (long productId)
    {
        for (OrderItemDto oitem : oitems)
        {
            if (oitem.getProductId().equals (productId))
   return oitem;
        }
        return null;
    }

    public Optional<OrderItemDto> removeProduct (long productId)
    {
        OrderItemDto result = findByProductId (productId);
        if (result != null && oitems.remove (result))
        {
            if (RECALC_CART)   cost = calcCost ();
        }
        return Optional.ofNullable (result);
    }

    private double calcCost ()
    {
        int sum = 0;
        for (OrderItemDto oitem : oitems)
            sum += oitem.getCost ();

        return sum;
    }

    private int calcLoad ()
    {
        int count = 0;
        for (OrderItemDto oitem : oitems)
            count += oitem.getQuantity();

        return count;
    }

    public void clear ()
    {
        oitems.clear ();
        cost = 0;
    }

/*    public void clearEmptyLines ()
    {
        //oitems.stream()
        //      .filter((oi)->oi.getQuantity()<=0)
        //      .forEach((oi)->oitems.remove(oi));
        oitems.removeIf (oi->oi.getQuantity() <= 0);
    }*/

/*    public boolean changeQuantity (long productId, int delta) //TODO: пока не пригодилась.
    {
        boolean ok = false;
        OrderItemDto oitem = findByProductId (productId);

        if (oitem != null)
        {
            ok = delta == 0;
            if (!ok)
            {
                ok = oitem.changeQuantity (delta);
                if (oitem.getQuantity() <= 0)
                {
                    oitems.remove (oitem);
                }
                if (RECALC_CART && ok)   cost = calcCost();
            }
        }
        return ok;
    }*/
}
