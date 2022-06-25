package ru.gb.antonov.j71.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.TestOnly;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.gb.antonov.j71.Factory.*;

@Entity     @Data    @Table(name="categories")
public class ProductsCategory {

    @Id    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column(name="name", nullable=false, length= PRODCAT_NAMELEN_MAX)
    private String name;

    @CreationTimestamp    @Column(name= COLNAME_CREATED_AT)
    private LocalDateTime createdAt;

    @UpdateTimestamp    @Column(name= COLNAME_UPDATED_AT)
    private LocalDateTime updatedAt;
//-------- неколонки
//    @OneToMany(mappedBy="category")
//    private List<Product> products;
//------------------------------------------------------------ конструкторы
    public ProductsCategory (){}
    public ProductsCategory (String name) {
        this.name = validateName (name);
        if (this.name == null)
            throw new BadCreationParameterException ("Некорректное название категории: "+ name);
    }
//------------------------------------------------------------ геттеры и сеттеры
    private void setId (Long value) { id = value; }
    private void setUpdatedAt (LocalDateTime value) { updatedAt = value; }
    private void setCreatedAt (LocalDateTime value) { createdAt = value; }
//------------------------------------------------------------
    public static String validateName (String name) {
        String result = null;
        if (name != null)
        {
            name = name.trim();
            if (!name.isEmpty())
                result = name;
        }
        return result;
    }
    public String toString()    {   return ProductsCategory.class.getSimpleName() +":"+ name;   }
//------------------------------------------------------------
//(метод используется в тестах, где корректность аргументов зависит от целей тестирования)
    @TestOnly
    public static ProductsCategory dummyProductsCategory (
                        Long id, String name, /*List<Product> products,*/
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        ProductsCategory pc = new ProductsCategory();
        pc.id        = id;
        pc.name      = name;
        pc.createdAt = createdAt;
        pc.updatedAt = updatedAt;
        //pc.products  = products;
        return pc;
    }
}
