package ru.gb.antonov.j71.beans.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.gb.antonov.j71.beans.services.OurUserService;
import ru.gb.antonov.j71.beans.services.ProductService;
import ru.gb.antonov.j71.entities.OurUser;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.dtos.ProductDto;

import java.security.Principal;

import static ru.gb.antonov.j71.Factory.PRODUCTS_PAGESIZE_DEFAULT;

@RequestMapping ("/api/v1/cart")
@RestController
@RequiredArgsConstructor    //< создаёт конструктор для инициализации всех final-полей.
public class CartController
{
    private final ProductService productService;
    private final OurUserService ourUserService;
    private int pageSize = PRODUCTS_PAGESIZE_DEFAULT;

/*
    //http://localhost:8189/market/api/v1/cart/add/18
    @GetMapping ("/add/{id}")
    public Integer addProductToCart (@PathVariable Long id, Principal principal)
    {
        return productService.addToCart (id, principal, 1);
    }

    //http://localhost:8189/market/api/v1/cart/itemscount
    @GetMapping ("/itemscount")
    public Integer getCartItemsCount (Principal principal)
    {
        return ourUserService.getCartItemsCount (principal);
    }

    //http://localhost:8189/market/api/v1/cart/remove/18
    @GetMapping ("/remove/{id}")
    public Integer removeProductFromCart (@PathVariable Long id, Principal principal)
    {
        return productService.removeFromCart (id, principal);
    }

    //http://localhost:8189/market/api/v1/cart/page
    //http://localhost:8189/market/api/v1/cart/page?p=0
    @GetMapping ("/page")
    public Page<ProductDto> getProductsCartPage (
                   @RequestParam (defaultValue="0", name="p", required = false) Integer pageIndex,
                   Principal principal)
    {
        return productService.getCartPage (pageIndex, pageSize, principal)
                             .map(ProductService::dtoFromProduct);
    }*/
}
