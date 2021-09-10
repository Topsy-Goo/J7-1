package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.ProductsCategory;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/*  Dto-шка для демонстрации товара на витрине.
*/
@Data
public class ProductDto
{
    private Long   productId;

    @NotNull (message="задайте название для продукта")
    @Length (min=3, max=255, message="длина названия [3…255] символов")
    private String productTitle;

    @Min (value=0, message="НЕотрицательная цена продукта")
    private double productCost;

    private String productsCategoryName;

//--------------------------------------------------------------
    public ProductDto (){}

    public ProductDto (Product product)
    {
        if (product != null)
        {
            productId    = product.getId();
            productTitle = product.getTitle();
            productCost  = product.getCost();
            productsCategoryName = product.getProductsCategory().getName ();
        }
    }
}
