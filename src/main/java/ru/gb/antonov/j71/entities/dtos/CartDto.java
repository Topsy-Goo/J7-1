package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Data
public class CartDto
{
    private List<OrderItemDto> oitems;
    private int titlesCount; //< используется клиентом для проверки, пустая ли корзина.

    //следующ. 2 поля никогда не хранят актуальные значения! При попытке получить их значения, последние
    // вычисляются и возвращаются геттерами.
    private BigDecimal cost;
    private int        load;
//-----------------------------------------------------------------------------
    public CartDto ()
    {   oitems = new LinkedList<> ();
        cost = BigDecimal.ZERO;
    }

    public static CartDto dummyCartDto (/*Метод готов переварить любые параметры.*/)
    {
        CartDto cdt     = new CartDto();
        cdt.titlesCount = 0;
        cdt.cost        = BigDecimal.ZERO;
        cdt.load        = 0;
        //cdt. = ;
        //cdt.oitems = ;
        return cdt;
    }
//--------------------- геттеры и сеттеры -------------------------------------
    public BigDecimal getCost ()
    {
        BigDecimal cost = BigDecimal.ZERO;
        for (OrderItemDto oitem : oitems)
        {
            cost = cost.add (oitem.getCost());
        }
        return cost;
    }

    public int getTitlesCount () { return oitems.size(); }
//-----------------------------------------------------------------------------
    public boolean addItem (OrderItemDto oitem, int quantity /* может быть 0 */)
    {
        boolean ok = false;
        if (oitem != null && quantity >= 0)
        {
            for (OrderItemDto oi : oitems)
            {
                if (oi.getProductId().equals (oitem.getProductId()))
                {

                    if (oi.changeQuantity (quantity))
                        /*recalcCost()*/;
                    ok = true;
                    break;
                }
            }
            if (!ok)
            {
                if (quantity > 0)
                    oitem.setQuantity (quantity);

                ok = oitems.add (oitem);
                load += quantity;
                cost = cost.add (oitem.getCost());
            }
        }
        return ok;
    }

    public String toString()
    {   return String.format("CartDto:[cst:%.2f, tls:%d, ld:%d, ois:%s]",
                             cost, titlesCount, load, oitems);
    }
}
