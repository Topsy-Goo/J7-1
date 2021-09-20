package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.gb.antonov.j71.entities.Product;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import static ru.gb.antonov.j71.Factory.PROD_TITLELEN_MAX;
import static ru.gb.antonov.j71.Factory.PROD_TITLELEN_MIN;

@Data
public class ProductDto
{
    private Long productId;

    @NotNull (message="Не задано название товара!")
    @Length (min= PROD_TITLELEN_MIN, max= PROD_TITLELEN_MAX,
             message="Длина названия товара: 3…255 символов!")
    private String title;

    //@Min (value=0, message="…")
    @PositiveOrZero (message="Цена товара должна быть НЕОТРИЦАТЕЛЬНЫМ числом!")
    private double price;

    @NotNull (message="Не указано название категории товара!")
    private String category;
//--------------------------------------------------------------
    public ProductDto (){}

    public ProductDto (Product product)
    {
        if (product != null)
        {   productId = product.getId();
            title     = product.getTitle();
            price     = product.getPrice();
            category  = product.getCategory ().getName ();
        }
    }
}
