package ru.gb.antonov.j71.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.NotNull;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;
import ru.gb.antonov.j71.beans.soap.products.ProductSoap;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Objects;

import static ru.gb.antonov.j71.Factory.MAX_PRICE;
import static ru.gb.antonov.j71.Factory.MIN_PRICE;

@Entity
@Table (name="products")
public class Product
{
    @Id  @Getter
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column(name="title", nullable=false)  @Getter
    private String title;

    @Column(name="price")  @Getter
    private double price;

    @Column(name="rest")  @Getter
    private int rest;

    @ManyToOne
    @JoinColumn(name="category_id", nullable=false)  @Getter
    private ProductsCategory category;

    @CreationTimestamp
    @Column(name="created_at", nullable=false)  @Getter @Setter
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name="updated_at", nullable=false)  @Getter @Setter
    private LocalDateTime updatedAt;
//----------------------------------------------------------------------
    public Product(){}

    public void update (String t, double c, ProductsCategory category)
    {
        if (!setTitle (t) || !setPrice (c) || !setCategory (category))
        {
            throw new BadCreationParameterException (String.format (
                "Недопустимый набор значений:\r    " +
                "• название продукта = %s,\r    " +
                "• цена = %.2f,\r" +
                "• категория = %s.", t, c, category));
        }
    }
//----------------- Геттеры и сеттеры -----------------------------------

    private void setId (Long id)   {   this.id = id;   }

    public boolean setTitle (String title)
    {
        boolean ok = isTitleValid (title);
        if (ok)
            this.title = title.trim();
        return ok;
    }

    public boolean setPrice (double newvalue)
    {
        boolean ok = isPriceValid (newvalue);
        if (ok)
            this.price = newvalue;
        return ok;
    }

    private boolean setCategory (ProductsCategory newcategory)
    {
        boolean ok = newcategory != null;
        if (ok)
            category = newcategory;
        return ok;
    }

    public boolean setRest (int newvalue)
    {
        boolean ok = newvalue >= 0;
        if (ok)
            rest = newvalue;
        return ok;
    }
//-----------------------------------------------------------------------

    public static boolean isTitleValid (String title)
    {
        return title != null  &&  !title.trim().isEmpty();
    }

    public static boolean isPriceValid (double value)
    {
        return value >= MIN_PRICE  &&  value <= MAX_PRICE;
    }

    @Override public boolean equals (Object o)
    {
        if (o == this)  return true;
        if (o == null || getClass() != o.getClass())  return false;
        Product p = (Product) o;
        return this.id.equals(p.getId());
    }

    @Override public int hashCode()    {   return Objects.hash (id);   }

    @Override public String toString()
    {   return String.format ("[id:%d, «%s», %.2f]", id, title, price);
    }

    @NotNull
    public static ProductSoap toProductSoap (Product p)
    {
        if (p != null && p.id != null)
        return new ProductSoap (p.id,
                                p.title,
                                p.price,
                                p.rest,
                                p.getCategory().getName(),
                                p.createdAt.getLong (ChronoField.MILLI_OF_SECOND),
                                p.updatedAt.getLong (ChronoField.MILLI_OF_SECOND));
        return new ProductSoap();
    }
}
