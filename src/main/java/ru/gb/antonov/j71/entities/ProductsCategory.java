package ru.gb.antonov.j71.entities;


import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name="categories")
public class ProductsCategory
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="id")
    private Long id;

    @Column(name="name", nullable=false)
    private String name;

    @CreationTimestamp
    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt;
//-------- неколонки
    @OneToMany(mappedBy="category")
    private List<Product> products;
//----------------------------------------------------------------------
    public ProductsCategory (){}
    public ProductsCategory (String name)
    {
        this.name = validateName (name);
        if (this.name == null)
            throw new BadCreationParameterException ("Некорректное название категории: "+ name);
    }
//(метод используется в тестах, где корректность аргументов зависит от целей тестирования)
    public static ProductsCategory dummyProductsCategory (Long id, String name, List<Product> products,
                                                          LocalDateTime createdAt, LocalDateTime updatedAt)
    {   ProductsCategory pc = new ProductsCategory();
        pc.id = id;
        pc.name = name;
        pc.createdAt = createdAt;
        pc.updatedAt = updatedAt;
        pc.products = products;
        return pc;
    }
//----------------------------------------------------------------------
    public static String validateName (String name)
    {
        String result = null;
        if (name != null)
        {
            name = name.trim();
            if (!name.isEmpty())
                result = name;
        }
        return result;
    }
    public String toString()    {   return name;   }
}
