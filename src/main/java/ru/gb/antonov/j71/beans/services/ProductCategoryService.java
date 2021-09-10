package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.antonov.j71.beans.errorhandlers.ResourceNotFoundException;
import ru.gb.antonov.j71.beans.errorhandlers.UserNotFoundException;
import ru.gb.antonov.j71.beans.repositos.ProductCategoryRepo;
import ru.gb.antonov.j71.entities.ProductsCategory;

@Service
@RequiredArgsConstructor
public class ProductCategoryService
{
    private final ProductCategoryRepo productCategoryRepo;


    public ProductsCategory findByName (String name)
    {
        String errMsg = "Товарная категория не найдена: " + name;
        return productCategoryRepo.findByName (name)
                                  .orElseThrow (()->new ResourceNotFoundException (errMsg));
    }
}
