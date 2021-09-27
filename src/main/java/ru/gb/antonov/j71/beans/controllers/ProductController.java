package ru.gb.antonov.j71.beans.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.gb.antonov.j71.beans.errorhandlers.OurValidationException;
import ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException;
import ru.gb.antonov.j71.beans.services.CartService;
import ru.gb.antonov.j71.beans.services.ProductService;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.dtos.ProductDto;

import java.util.Optional;
import java.util.stream.Collectors;

import static ru.gb.antonov.j71.Factory.PROD_PAGESIZE_DEF;

@RestController
@RequestMapping ("/api/v1/products")    //http://localhost:8189/market/api/v1/products
@RequiredArgsConstructor
public class ProductController
{
    private final ProductService productService;
    private final CartService cartService;

    //@Value ("${views.shop.items-per-page-def}")
    private final int pageSize = PROD_PAGESIZE_DEF;
//--------------------------------------------------------------------

    //http://localhost:8189/market/api/v1/products/page?p=0
    @GetMapping ("/page")
    public Page<ProductDto> getProductsPage (
                   @RequestParam (defaultValue="0", name="p", required=false) Integer pageIndex)
    {
        return productService.findAll (pageIndex, pageSize).map(ProductService::dtoFromProduct);
    }
//------------------- Редактирование продуктов -------------------------
//TODO: редактирование товаров пока не входит в план проекта.

    //http://localhost:8189/market/api/v1/products/11
    @GetMapping ("/{id}")
    public ProductDto findById (@PathVariable Long id)
    {
        if (id == null)
            throw new UnableToPerformException ("Не могу выполнить поиск для товара id: "+ id);
        return ProductService.dtoFromProduct (productService.findById (id));
    }

   //http://localhost:8189/market/api/v1/products   POST
    @PostMapping
    public Optional<ProductDto> createProduct (@RequestBody @Validated ProductDto pdto, BindingResult br)
    {
//  Нельзя изменять последовательность следующих параметров:
//    @Validated ProductDto pdto, BindingResult br
        if (br.hasErrors())
        {
            //преобразуем набор ошибок в список сообщений, и пакуем в одно общее исключение (в наше заранее для это приготовленное исключение).
            throw new OurValidationException (br.getAllErrors ()
                                                .stream()
                                                .map (ObjectError::getDefaultMessage)
                                                .collect (Collectors.toList ()));
        }
        Product p = productService.createProduct (pdto.getTitle (),
                                                  pdto.getPrice (),
                                                  pdto.getCategory ());
        return toOptionalProductDto (p);
    }

   //http://localhost:8189/market/api/v1/products   PUT
    @PutMapping
    public Optional<ProductDto> updateProduct (@RequestBody ProductDto pdto)
    {
        Product p = productService.updateProduct (pdto.getProductId(),
                                                  pdto.getTitle (),
                                                  pdto.getPrice (),
                                                  pdto.getCategory ());
        return toOptionalProductDto (p);
    }

    //http://localhost:8189/market/api/v1/products/delete/11
    @GetMapping ("/delete/{id}")
    public void deleteProductById (@PathVariable Long id)
    {
        if (id == null)
            throw new UnableToPerformException ("Не могу выполнить удаление товара id: "+ id);
        productService.deleteById (id);
    }

    private static Optional<ProductDto> toOptionalProductDto (Product p)
    {
        return p != null ? Optional.of (ProductService.dtoFromProduct(p))
                         : Optional.empty();
    }
}
