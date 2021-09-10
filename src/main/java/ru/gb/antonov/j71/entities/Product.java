package ru.gb.antonov.j71.entities;

import org.hibernate.annotations.CreationTimestamp;
import ru.gb.antonov.j71.beans.errorhandlers.ProductUpdatingException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/*  Сущность для хранения инф-ции о товаре, доступном для заказа.
*/
@Entity
@Table (name="products")
public class Product
{
    public static final Double MIN_PRICE = 0.0;
    public static final Double MAX_PRICE = Double.MAX_VALUE;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column(name="title", nullable=false)
    private String title;

    @Column(name="price")
    private double cost;

    @Column(name="rest")
    private int rest;

    @ManyToOne
    @JoinColumn(name="category_id", nullable=false)
    private ProductsCategory productsCategory;

    @CreationTimestamp
    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt;

/*    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;*/


    public Product(){}

    public void update (String t, double c, ProductsCategory category)
    {
        if (!setTitle (t) || !setCost (c) || !setProductsCategory (category))
        {
            throw new ProductUpdatingException (String.format (
                "Недопустимый набор значений:\r    название продукта = %s,\r    цена = %.2f.", t, c));
        }
    }
//----------------- Геттеры и сеттеры -----------------------------------

    public Long getId ()    {   return id;   }
    private void setId (Long id)   {   this.id = id;   }


    public String getTitle ()   {   return title;   }
    public boolean setTitle (String title)
    {
        boolean ok = isTitleValid (title);
        if (ok)
            this.title = title.trim();
        return ok;
    }

    public double getCost ()    {   return cost;   }
    public boolean setCost (double cost)
    {
        boolean ok = isCostValid (cost);
        if (ok)
            this.cost = cost;
        return ok;
    }

    public ProductsCategory getProductsCategory() {   return productsCategory;   }
    private boolean setProductsCategory (ProductsCategory category)
    {
        boolean ok = category != null;
        if (ok)
            productsCategory = category;
        return ok;
    }

/*    public Collection<CartItem> getCartItems()  {   return Collections.unmodifiableCollection (cartItems);   }
    private void setCartItems (Collection<CartItem> collection)
    {
        if (collection != null)
            cartItems = collection;
    }*/
//-----------------------------------------------------------------------

    public static boolean isTitleValid (String title)
    {
        return title != null  &&  !title.trim().isEmpty();
    }

    public static boolean isCostValid (double cost)
    {
        return cost >= MIN_PRICE  &&  cost <= MAX_PRICE;
    }

    public String toString()
    {
        return String.format("[id:%d, «%s», %.2f]", id, title, cost);
    }

    @Override
    public boolean equals (Object o)
    {
        Product p = (Product) o;
        return p!=null && this.id.equals(p.getId());
    }

    @Override
    public int hashCode()    {   return Objects.hash (id);   }
}
