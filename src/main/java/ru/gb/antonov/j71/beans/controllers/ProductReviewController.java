package ru.gb.antonov.j71.beans.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException;
import ru.gb.antonov.j71.beans.services.ProductReviewService;
import ru.gb.antonov.j71.entities.dtos.ProductReviewDto;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping ("/api/v1/productreviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @GetMapping ("/load_reviews/{id}")
    public List<ProductReviewDto> loadProductReviews (@PathVariable (name="id") Long pid)
    {
        if (pid == null)
            throw new BadCreationParameterException("Не могу выполнить поиск для товара id: " + pid);
        return productReviewService.getReviewListById (pid);
    }

    @PostMapping ("/new_review")
    public void newProductReview (@RequestBody ProductReviewDto reviewDto, Principal principal)
    {
        productReviewService.newProductReview (reviewDto.getProductId(), reviewDto.getText(), principal);
    }

    @GetMapping ("/can_review/{pid}")
    public Boolean canReviews (@PathVariable Long pid, Principal principal)
    {
        return productReviewService.canReview (principal, pid);
    }
}
