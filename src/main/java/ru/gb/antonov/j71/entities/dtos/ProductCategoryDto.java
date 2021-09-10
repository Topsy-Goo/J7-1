package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gb.antonov.j71.entities.ProductsCategory;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProductCategoryDto
{
    private Long             id;
    private String           name;
    private List<ProductDto> products;

    public ProductCategoryDto (ProductsCategory category)
    {
        this.id = category.getId();
        this.name = category.getName();
        this.products = category.getProducts()
                                .stream()
                                .map(ProductDto::new)
                                .collect (Collectors.toList ());
    }
}
