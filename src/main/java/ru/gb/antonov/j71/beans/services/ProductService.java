package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.antonov.j71.beans.errorhandlers.ResourceNotFoundException;
import ru.gb.antonov.j71.beans.repositos.ProductRepo;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.ProductsCategory;
import ru.gb.antonov.j71.entities.dtos.ProductDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService
{
    private final ProductRepo productRepo;
    private final ProductCategoryService productCategoryService;

    private static int pageIndexLast = 0;

//-----------------------------------------------------------------------

    public Product findById (Long id)
    {
        String errMessage = "Не найден продукт с id = "+ id;
        return productRepo.findById(id)
                          .orElseThrow (()->new ResourceNotFoundException (errMessage));
    }

    public Page<Product> findAll (int pageIndex, int pageSize)
    {
        pageIndex = validatePageIndex (pageIndex, pageSize, productRepo.count());
        return productRepo.findAll (PageRequest.of (pageIndex, pageSize));
    }

    private int validatePageIndex (int pageIndex, int pageSize, long productsCount)
    {
        int pagesCount    = (int)(productsCount / pageSize);

        if (productsCount % pageSize > 0)
            pagesCount ++;

        if (pageIndex >= pagesCount)
            pageIndex = pagesCount -1;

        return Math.max(pageIndex, 0);
    }
//-------------- Редактирование товаров ---------------------------------

    @Transactional
    public Product createProduct (String title, double price, String productCategoryName)
    {
        Product p = new Product();
        ProductsCategory category = productCategoryService.findByName (productCategoryName); //< бросает ResourceNotFoundException
        p.update (title, price, category);     //< бросает UnableToPerformException
        return productRepo.save (p);
    }

    @Transactional
    public Product updateProduct (long id, String title, double price, String productCategoryName)
    {
        Product p = findById (id);  //< бросает ResourceNotFoundException
        ProductsCategory category = p.getCategory ();

        if (!category.getName().equals(productCategoryName))
        {
            category = productCategoryService.findByName (productCategoryName); //< бросает ResourceNotFoundException
        }
        p.update (title, price, category);     //< бросает UnableToPerformException
        //cartService.updateProduct (p);
        return productRepo.save (p);
    }
//TODO: Успешное удаление или редактирование товаров должно как-то отражаться и в корзинах, эти товары содержащих.
    @Transactional
    public void deleteById (Long id)
    {
        Product p = findById (id);  //< бросает ResourceNotFoundException
        //cartService.onProductDeletion (p);
        productRepo.delete(p);
    }
//-------------- Фильтры ------------------------------------------------
    /*public List<Product> getProductsByPriceRange (Integer min, Integer max)
    {
        double minPrice = min != null ? min.doubleValue() : Product.MIN_PRICE;
        double maxPrice = max != null ? max.doubleValue() : Product.MAX_PRICE;

        return productRepo.findAllByPriceBetween (minPrice, maxPrice);
    }*/
//--------- Методы для преобразований Product в ProductDto --------------

    public static ProductDto dtoFromProduct (Product product)
    {
        return new ProductDto (product);
    }

    public static List<ProductDto> productListToDtoList (List<Product> pp)
    {
        if (pp != null)
        {
            return pp.stream()
                     .map(ProductService::dtoFromProduct)
                     .collect (Collectors.toList ());
        }
        return Collections.emptyList ();
    }
}
