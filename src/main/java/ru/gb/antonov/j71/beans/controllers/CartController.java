package ru.gb.antonov.j71.beans.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException;
import ru.gb.antonov.j71.beans.services.CartService;
import ru.gb.antonov.j71.beans.services.ProductService;
import ru.gb.antonov.j71.entities.dtos.CartDto;
import ru.gb.antonov.j71.entities.dtos.StringResponse;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping ("/api/v1/cart")    //http://localhost:8189/market/api/v1/cart
@RequiredArgsConstructor
public class CartController
{
    private final ProductService productService;
    private final CartService cartService;
//------------------------------------------------------------------------

    @GetMapping({"/uuid"})
    public StringResponse generateCartUuid (/*Principal principal*/)
    {
        return new StringResponse (UUID.randomUUID().toString());
    }

    @GetMapping ("/{uuid}")
    public CartDto getProductsCart (Principal principal, @PathVariable String uuid)
    {
        return cartService.getUsersCartDto (principal, uuid);
    }

    @GetMapping ("/load/{uuid}")
    public Integer getCartLoad (Principal principal, @PathVariable String uuid)
    {
        return cartService.getCartLoad (principal, uuid);
    }

/*    @GetMapping ("/cost/{uuid}")
    public Double getCartCost (Principal principal, @PathVariable String uuid)
    {
        return cartService.getCartCost (principal, uuid);
    }*/

    @GetMapping ("/plus/{productId}/{uuid}")
    public void increaseProductQuantity (Principal principal, @PathVariable Long productId, @PathVariable String uuid)
    {
        if (productId == null)
            throw new UnableToPerformException ("Не могу изменить количество для товара id: "+ productId);
        cartService.changeProductQuantity (principal, uuid, productId, 1);
    }

    @GetMapping ("/minus/{productId}/{uuid}")
    public void decreseProductQuantity (Principal principal, @PathVariable Long productId, @PathVariable String uuid)
    {
        if (productId == null)
            throw new UnableToPerformException ("Не могу изменить количество для товара id: "+ productId);
        cartService.changeProductQuantity (principal, uuid, productId, -1);
    }

    @GetMapping ("/remove/{productId}/{uuid}")
    public void removeProduct (Principal principal, @PathVariable Long productId, @PathVariable String uuid)
    {
        if (productId == null)
            throw new UnableToPerformException ("Не могу удалить из корзины товар id: "+ productId);
        cartService.removeProductFromCart (principal, uuid, productId);
    }

    @GetMapping ("/clear/{uuid}")
    public void clearCart (Principal principal, @PathVariable String uuid)
    {
        cartService.clearCart (principal, uuid);
    }

    @GetMapping({"/merge/{uuid}"})
    public void mergeCarts (Principal principal, @PathVariable String uuid)
    {
        this.cartService.mergeCarts (principal, uuid);
    }
}
