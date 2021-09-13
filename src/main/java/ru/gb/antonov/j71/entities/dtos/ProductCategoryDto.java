package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.gb.antonov.j71.entities.ProductsCategory;

import javax.validation.constraints.NotNull;

import static ru.gb.antonov.j71.Factory.PRODCAT_NAMELEN_MAX;
import static ru.gb.antonov.j71.Factory.PRODCAT_NAMELEN_MIN;

@Data
@NoArgsConstructor
public class ProductCategoryDto
{
    private Long             id;

    @NotNull (message="Не указано название категории товара!")
    @Length (min= PRODCAT_NAMELEN_MIN,
             max= PRODCAT_NAMELEN_MAX,
             message="Длина названия категории товара: 1…255 символов!")
    private String           name;
//-----------------------------------------------------------------------
    public ProductCategoryDto (ProductsCategory category)
    {
        this.id = category.getId();
        this.name = category.getName();
    }
}
