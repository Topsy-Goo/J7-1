package ru.gb.antonov.j71.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.NotNull;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;
import ru.gb.antonov.j71.beans.soap.products.ProductSoap;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Objects;

import static ru.gb.antonov.j71.Factory.MAX_PRICE;
import static ru.gb.antonov.j71.Factory.MIN_PRICE;

@Entity
@Table (name="products")
public class Product implements Buildable<Product> {

    @Id  @Getter
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column(name="title", nullable=false)            @Getter
    private String title;

    public static String getTitleFieldName ()  {   return "title";   }
    public static String getPriceFieldName ()  {   return "price";   }

    @Column(name="price", nullable=false)            @Getter
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name="rest", nullable=false)             @Getter
    private Integer rest;

    @ManyToOne
    @JoinColumn(name="measure_id", nullable=false)   @Getter
    private Measure measure;

    @ManyToOne
    @JoinColumn(name="category_id", nullable=false)  @Getter
    private ProductsCategory category;

    @CreationTimestamp    @Column(name="created_at") @Getter @Setter
    private LocalDateTime createdAt;

    @CreationTimestamp    @Column(name="updated_at") @Getter @Setter
    private LocalDateTime updatedAt;

//----------------------------------------------------------------------
    private Product () {}

//---------------- создание и обновление объектов ----------------------
/** Создаёт пустой объект Product и начинает цепочку методов, каждый из которых проверяет валидность
изменяемого параметра.
@return ссылка на объект Product */
    public static Product create () {
        return new Product();
    }

 /** Начинает цепочку методов, каждый из которых проверяет валидность изменяемого параметра. Цепочка не
обязана начинаться с этого метода, но его использование улучшает читаемость кода.
@return this
@throws BadCreationParameterException */
    public Product strictUpdate () {
        return this;
    }
/**
@return this
@throws BadCreationParameterException */
   public Product withTitle (String newTitle) {
        if (!setTitle (newTitle))
            throw new BadCreationParameterException ("\rнекорректное название продукта : "+ newTitle);
        return this;
    }
/**
@return this
@throws BadCreationParameterException */
    public Product withPrice (BigDecimal newPrice) {
        if (!setPrice (newPrice))
            throw new BadCreationParameterException ("\rнекорректная цена продукта : "+ newPrice);
        return this;
    }
/**
@return this
@throws BadCreationParameterException */
    public Product withRest (Integer newRest) {
        if (!setRest (newRest))
            throw new BadCreationParameterException ("\rнекорректный остаток продукта : "+ newRest);
        return this;
    }
    /**
@return this
@throws BadCreationParameterException */
    public Product withMeasure (Measure newMeasure) {
        if (!setMeasure(newMeasure))
            throw new BadCreationParameterException ("\rнекорректная еденица измерения : "+ newMeasure);
        return this;
    }
/**
@return this
@throws BadCreationParameterException */
    public Product withProductsCategory (ProductsCategory newProductCategory) {
        if (!setCategory (newProductCategory))
            throw new BadCreationParameterException ("\rнекорректная категория продукта : "+ newProductCategory);
        return this;
    }
/**
@return this */
    @Override public Product build () {
        if (title == null || category == null || price == null || measure == null || rest == null)
            throw new BadCreationParameterException ("\rнедостаточная инициализация");
        return this;
    }

/** Метод используется в тестах, где корректность аргументов зависит от целей тестирования.    */
    public static Product dummyProduct (Long id, String title, BigDecimal price, Integer rest,
                                        Measure measure, ProductsCategory category,
                                        LocalDateTime createdAt, LocalDateTime updatedAt)
    {   Product p = new Product();
        p.id        = id;
        p.title     = title;
        p.price     = price;
        p.rest      = rest;
        p.measure   = measure;
        p.category  = category;
        p.createdAt = createdAt;
        p.updatedAt = updatedAt;
        return p;
    }

/** Любой из параметров может быть {@code null}. Равенство параметра {@code null} расценивается как
нежелание изменять соответствующее ему свойство товара.
@return void
@throws BadCreationParameterException если ненулевой параметр оказался некооректным. */
    public void update (String newTitle, BigDecimal newPrice, Integer newRest, Measure newMeasure,
                        ProductsCategory newProductCategory)
    {
        if (newTitle != null && !setTitle (newTitle))
            throw new BadCreationParameterException ("\rнекорректное название продукта : "+ newTitle);

        if (newPrice != null && !setPrice (newPrice))
            throw new BadCreationParameterException ("\rнекорректная цена продукта : "+ newPrice);

        if (newRest != null && !setRest (newRest))
            throw new BadCreationParameterException ("\rнекорректный остаток продукта : "+ newRest);

        if (newMeasure != null && !setMeasure (newMeasure))
            throw new BadCreationParameterException ("\rнекорректная еденица измерения продукта : "+ newMeasure);

        if (newProductCategory != null && !setCategory (newProductCategory))
            throw new BadCreationParameterException ("\rнекорректная категория продукта : "+ newProductCategory);
    }
//----------------- Геттеры и сеттеры -----------------------------------

    private void setId (Long id)   {   this.id = id;   }

    public boolean setTitle (String title) {
        boolean ok = isTitleValid (title);
        if (ok)
            this.title = title.trim();
        return ok;
    }

    public boolean setPrice (BigDecimal newvalue) {
        boolean ok = isPriceValid (newvalue);
        if (ok)
            this.price = newvalue;
        return ok;
    }

    public boolean setRest (Integer newvalue) {
        boolean ok = newvalue != null && newvalue >= 0;
        if (ok)
            rest = newvalue;
        return ok;
    }

    private boolean setMeasure (Measure value) {
        boolean ok = Measure.isMeasureValid (value);
        if (ok)
            measure = value;
        return ok;
    }

    private boolean setCategory (ProductsCategory newcategory) {
        boolean ok = newcategory != null;
        if (ok)
            category = newcategory;
        return ok;
    }

    private void setUpdatedAt (LocalDateTime value) { updatedAt = value; }
    private void setCreatedAt (LocalDateTime value) { createdAt = value; }
//-----------------------------------------------------------------------

    public static boolean isTitleValid (String title) {
        return title != null  &&  !title.trim().isEmpty();
    }

    public static boolean isPriceValid (BigDecimal value) {
        return value.compareTo(MIN_PRICE) >= 0  &&  value.compareTo(MAX_PRICE) <= 0;
    }

    @Override public boolean equals (Object o) {
        if (o == this)  return true;
        if (o == null || getClass() != o.getClass())  return false;
        Product p = (Product) o;
        return this.id.equals (p.getId());
    }

    @Override public int hashCode()    {   return Objects.hash (id);   }

    @Override public String toString() {
        return String.format ("prod:[id:%d, «%s», %.2f, rt:%d, msr:%s, cat:%s]",
                              id, title, price, rest, measure, category);
    }

    @NotNull public static ProductSoap toProductSoap (Product p) {

        if (p != null && p.id != null)
        return new ProductSoap (p.id,
                                p.title,
                                p.price,
                                p.rest,
                                p.measure.getName(),
                                p.category.getName(),
                                p.createdAt.getLong (ChronoField.MILLI_OF_SECOND),
                                p.updatedAt.getLong (ChronoField.MILLI_OF_SECOND));
        return new ProductSoap();
    }
}
