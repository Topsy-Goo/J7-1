package ru.gb.antonov.j71.beans.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.antonov.j71.beans.services.CartService;
import ru.gb.antonov.j71.beans.services.ProductService;
import ru.gb.antonov.j71.entities.dtos.CartDto;

import java.security.Principal;

@RestController
@RequestMapping ("/api/v1/cart")    //http://localhost:8189/market/api/v1/cart
@RequiredArgsConstructor
public class CartController
{
    private final ProductService productService;
    private final CartService cartService;
//------------------------------------------------------------------------

    @GetMapping
    public CartDto getProductsCart (Principal principal) { return cartService.getUsersCartDto (principal); }

    @GetMapping ("/load")
    public Integer getCartLoad (Principal principal) { return cartService.getCartLoad (principal); }

    @GetMapping ("/cost")
    public Double getCartCost (Principal principal) { return cartService.getCartCost (principal); }

    @GetMapping ("/plus/{productId}")
    public void increaseProductQuantity (@PathVariable Long productId, Principal principal)
    {
        cartService.changeProductQuantity (productId, 1, principal);
    }

    @GetMapping ("/minus/{productId}")
    public void decreseProductQuantity (@PathVariable Long productId, Principal principal)
    {
        cartService.changeProductQuantity (productId, -1, principal);
    }

    @GetMapping ("/remove/{productId}")
    public void removeProduct (@PathVariable Long productId, Principal principal)
    {
        cartService.removeProduct (productId, principal);
    }

    @GetMapping ("/clear")
    public void clearCart (Principal principal) { cartService.clearCart (principal); }
}
