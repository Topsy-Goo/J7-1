package ru.gb.antonov.j71.beans.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.gb.antonov.j71.beans.errorhandlers.OurValidationException;
import ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException;
import ru.gb.antonov.j71.beans.errorhandlers.UnauthorizedAccessException;
import ru.gb.antonov.j71.beans.services.OurUserService;
import ru.gb.antonov.j71.beans.services.ProductService;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.dtos.ProductDto;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.gb.antonov.j71.Factory.PROD_PAGESIZE_DEF;

@RestController
@RequestMapping ("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final OurUserService ourUserService;

    //@Value ("${views.shop.items-per-page-def}")
    private final        int    pageSize = PROD_PAGESIZE_DEF;
    private final static Logger LOGGER   = Logger.getLogger ("ru.gb.antonov.j71.beans.controllers.ProductController");
//--------------------------------------------------------------------

    //http://localhost:12440/market/api/v1/products/page?p=0
    @GetMapping ("/page")
    public Page<ProductDto> getProductsPage (
            @RequestParam (defaultValue="0", name="p", required=false) Integer pageIndex,
            @RequestParam MultiValueMap<String, String> filters)
    {
        LOGGER.info ("Получен GET-запрос: /api/v1/products/page?p="+pageIndex);
        return productService.getPageOfProducts (pageIndex, pageSize, filters);
    }
//------------------- Страница описания товара -------------------------

    //http://localhost:12440/market/api/v1/products/11
    @GetMapping ("/{id}")
    public ProductDto findById (@PathVariable Long id) {

        LOGGER.info ("Получен GET-запрос: /api/v1/products/"+id);
        if (id == null)
            throw new UnableToPerformException ("Не могу выполнить поиск для товара id: "+ id);
        return ProductService.dtoFromProduct (productService.findById (id));
    }
//------------------- Редактирование товара ----------------------------

   //http://localhost:12440/market/api/v1/products   POST
    @PostMapping
    public Optional<ProductDto> createProduct (@RequestBody @Validated ProductDto pdto, BindingResult br,
                                               Principal principal)
    {//  Нельзя изменять последовательность следующих параметров: @Validated ProductDto pdto, BindingResult br
        LOGGER.info ("Получен POST-запрос: /api/v1/products + "+ pdto);
        chechAccessToEditProducts (principal);
        if (br.hasErrors())
            //преобразуем набор ошибок в список сообщений, и пакуем в одно общее исключение (в наше заранее для это приготовленное исключение).
            throw new OurValidationException (br.getAllErrors().stream()
                                                .map (ObjectError::getDefaultMessage)
                                                .collect (Collectors.toList()));

        Product p = productService.createProduct (pdto.getTitle(), pdto.getPrice(), pdto.getRest(),
                                                  pdto.getCategory(), pdto.getMeasure());
        return toOptionalProductDto (p);
    }

   //http://localhost:12440/market/api/v1/products   PUT
    @PutMapping
    public Optional<ProductDto> updateProduct (@RequestBody ProductDto pdto, Principal principal) {

        LOGGER.info ("Получен PUT-запрос: /api/v1/products + "+ pdto);
        chechAccessToEditProducts (principal);
        Product p = productService.updateProduct (pdto.getProductId(), pdto.getTitle(), pdto.getPrice(),
                                                  pdto.getRest(), pdto.getMeasure(), pdto.getCategory());
        return toOptionalProductDto (p);
    }

    //http://localhost:12440/market/api/v1/products/delete/11
    @GetMapping ("/delete/{id}")
    public void deleteProductById (@PathVariable Long id, Principal principal) {

        LOGGER.info ("Получен GET-запрос: /api/v1/products/delete/"+ id);
        chechAccessToEditProducts (principal);
        productService.deleteById (id);
    }

    //http://localhost:12440/market/api/v1/product/categories_list
    @GetMapping ("/categories_list")
    public List<String> getCategories () {
        return productService.getCategoriesList();
    }

    //http://localhost:12440/market/api/v1/product/measures_list
    @GetMapping ("/measures_list")
    public List<String> getMeasures () {
        return productService.getMeasuresList();
    }
//----------------------------------------------------------------------
    private static Optional<ProductDto> toOptionalProductDto (Product p) {

        return p != null ? Optional.of (ProductService.dtoFromProduct(p))
                         : Optional.empty();
    }

    private void chechAccessToEditProducts (Principal principal) {
        if (!ourUserService.canEditProduct (principal))
            throw new UnauthorizedAccessException ("Вам это нельзя!");

        //Это дополнительная проверка. Первый раз проверка выполняется на фронте — через
        // AuthController.checkPermissionEditProducts(), — когда фронт принимает решение, показывать ли
        // юзеру доп.элементы управления.
    }
}
