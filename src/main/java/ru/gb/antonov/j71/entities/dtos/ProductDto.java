package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.gb.antonov.j71.entities.Product;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

import static ru.gb.antonov.j71.Factory.PROD_TITLELEN_MAX;
import static ru.gb.antonov.j71.Factory.PROD_TITLELEN_MIN;

@Data
public class ProductDto {

    private Long productId;

    @NotNull (message="\rНе задано название товара!")
    @Length (min= PROD_TITLELEN_MIN, max= PROD_TITLELEN_MAX, message="Длина названия товара: 3…255 символов!")
    private String title;

    private BigDecimal price = BigDecimal.ZERO;

    @PositiveOrZero (message="\rОстаток товара должен быть НЕОТРИЦАТЕЛЬНЫМ числом!")
    private Integer rest;

    @NotNull (message="\rНе указана еденица измерения!")
    private String measure;

    @NotNull (message="\rНе указано название категории товара!")
    private String category;
//--------------------------------------------------------------
    public ProductDto () {}

    public ProductDto (Product product) {
        if (product != null) {
            productId = product.getId();
            title     = product.getTitle();
            price     = product.getPrice();
            rest      = product.getRest();
            measure   = product.getMeasure().getName();
            category  = product.getCategory().getName();
        }
    }

/** Используется для тестов. Корректность значений определяется потребностями тестов.   */
    public static ProductDto dummyProductDto (Long pProductId, String pTitle, BigDecimal pPrice,
                                              Integer pRest, String measure, String pCategory)
    {   ProductDto pdt = new ProductDto();
        pdt.productId  = pProductId;
        pdt.title      = pTitle;
        pdt.price      = pPrice;
        pdt.rest       = pRest;
        pdt.measure    = measure;
        pdt.category   = pCategory;
        return pdt;
    }

    @Override public String toString() {
        return String.format ("prodDto:[pid:%d, title:«%s», price:%f, rest:%d, msr:«%s», categ:«%s»]",
                                      productId, title,     price,    rest,    measure,  category);
    }
}
