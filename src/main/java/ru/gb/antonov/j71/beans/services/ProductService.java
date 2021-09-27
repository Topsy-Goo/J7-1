package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.antonov.j71.beans.errorhandlers.ResourceNotFoundException;
import ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException;
import ru.gb.antonov.j71.beans.repositos.ProductRepo;
import ru.gb.antonov.j71.beans.soap.products.ProductSoap;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.ProductsCategory;
import ru.gb.antonov.j71.entities.dtos.ProductDto;

import java.util.ArrayList;
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

/** @param from {@code productId} первого элемента интервала.
    @param to {@code productId} последнего элемента интервала (включительно). */
    public List<Product> findAllByIdBetween (Long from, Long to)
    {
        String errMsg = String.format ("Не могу получить все товары из диапазона id: %d…%d.", from, to);
        return productRepo.findAllByIdBetween (from, to)
                          .orElseThrow (()->new ResourceNotFoundException (errMsg));
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
        ProductsCategory category = p.getCategory();

        if (!category.getName().equals (productCategoryName))
        {
            category = productCategoryService.findByName (productCategoryName); //< бросает ResourceNotFoundException
        }
        p.update (title, price, category);     //< бросает UnableToPerformException
        return productRepo.save (p);
    }

/** Товар не удаляем, а обнуляем у него поле {@code rest}. В дальнейшем, при попытке добавить
    этот товар в корзину, проверяется его количество, и, т.к. оно равно 0, добавление не происходит.<p>
    Логично будет добавить к этому НЕвозможность показывать такой товар на витрине.*/
    @Transactional
    public void deleteById (Long id)
    {
        //TODO: Редактирование товаров пока не входит в план проекта.
        //TODO: добавить к этому НЕвозможность показывать такой товар на витрине.
        Product p = findById (id);  //< бросает ResourceNotFoundException
        p.setRest (0);
        //productRepo.delete(p);
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

    public void onProductDeletion (Long pid)
    {   //Обнулим количество товара, чтобы его невозможно было купить.
        Product p = findById (pid);
        p.setRest (0);
        productRepo.save (p);
    }
//-------------- SOAP ---------------------------------------------------

    @Transactional
    public ProductSoap getProductSoapByProductId (Long pid)
    {
        return Product.toProductSoap (findById (pid));
    }

/** @param from {@code productId} первого элемента интервала.
    @param to {@code productId} последнего элемента интервала (включительно). */
    @Transactional
    @NotNull
    public List<ProductSoap> getProductSoapRangeByProductIdRange (Long from, Long to)
    {
        List<ProductSoap> result = Collections.emptyList();
        if (from != null && to != null && from > 0L && from.compareTo(to) <= 0)
        {
            long range = to - from +1L;
            if (range <= (long)(Integer.MAX_VALUE))
            {
                List<Product> products = findAllByIdBetween (from, to);
                result = new ArrayList<>((int)range);

                for (Product p : products)
                    result.add (Product.toProductSoap (p));
                /*result = findAllByIdBetween (from, to).stream().map (Product::toProductSoap).collect (Collectors.toList ());*/
            }
        }
        return result;
    }
}
