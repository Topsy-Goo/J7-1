package ru.gb.antonov.j71.beans.services;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.antonov.j71.beans.errorhandlers.ResourceNotFoundException;
import ru.gb.antonov.j71.beans.repositos.ProductRepo;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.ProductsCategory;
import ru.gb.antonov.j71.entities.dtos.ProductDto;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService
{
    private final ProductRepo productRepo;
    private final OurUserService ourUserService;
    private final ProductCategoryService productCategoryService;

    private static int pageIndexLast = 0;

//-----------------------------------------------------------------------

    @NotNull
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
//-------------- Корзина ------------------------------------------------

    //@Transactional    TODO: раскомментировать, если будет вызываться непосредственно из контроллера.
    public Integer addToCart (Long pid, Principal principal, int quantity)
    {
        Product product = findById (pid); //< бросает ResourceNotFoundException
        return ourUserService.addToCart (product, principal, quantity);
    }
/*
    @Transactional
    public Integer removeFromCart (Long pid, Principal principal)
    {
        Product product = findById (pid); //< бросает ResourceNotFoundException
        return ourUserService.removeFromCart (product, principal);
    }

    @Transactional
    public Integer getCartItemsCount (Principal principal)
    {
        return ourUserService.getCartItemsCount (principal);
    }

    @Transactional
    public Page<Product> getCartPage (int pageIndex, int pageSize, Principal principal)
    {
        List<Product> cart = ourUserService.getUnmodifiableCart (principal);
        int cartSize = cart.size();
        pageIndex = validatePageIndex (pageIndex, pageSize, cartSize);

        int fromIndex = pageSize * pageIndex;
        int toIndex = fromIndex + pageSize;
        if (toIndex > cartSize)
            toIndex = cartSize;

        Page<Product> page = new PageImpl<> (cart.subList (fromIndex, toIndex),
                                             PageRequest.of (pageIndex, pageSize),
                                             cartSize);
        Pageable pageable = page.getPageable ();
        return page;
    }*/
//-------------- Редактирование товаров ---------------------------------

    @Transactional
    public void deleteById (Long id)
    {
        Product p = findById (id);  //< бросает ResourceNotFoundException
        //cart.removeFromCart (p);    //< сперва удаляем из корзины
        productRepo.delete(p);
    }

    @Transactional
    public Product createProduct (String title, double cost, String productCategoryName)
    {
        Product p = new Product();
        ProductsCategory category = productCategoryService.findByName (productCategoryName); //< бросает ResourceNotFoundException
        p.update (title, cost, category);     //< бросает ProductUpdatingException
        return productRepo.save (p);
    }

    @Transactional
    public Product updateProduct (long id, String title, double cost, String productCategoryName)
    {
        Product p = findById (id);  //< бросает ResourceNotFoundException
        ProductsCategory category = p.getProductsCategory();

        if (!category.getName().equals(productCategoryName))
        {
            category = productCategoryService.findByName (productCategoryName); //< бросает ResourceNotFoundException
        }
        p.update (title, cost, category);     //< бросает ProductUpdatingException
        //cart.updateProduct (p);
        return productRepo.save (p);
    }

  //TODO  теперь нужно посмотреть, что с категориями происходит на фронте.

//-------------- Фильтры ------------------------------------------------
    /*public List<Product> getProductsByPriceRange (Integer min, Integer max)
    {
        double minPrice = min != null ? min.doubleValue() : Product.MIN_PRICE;
        double maxPrice = max != null ? max.doubleValue() : Product.MAX_PRICE;

        return productRepo.findAllByCostBetween (minPrice, maxPrice);
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
