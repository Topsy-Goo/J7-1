package ru.gb.antonov.j71.beans.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.gb.antonov.j71.beans.errorhandlers.OurValidationException;
import ru.gb.antonov.j71.beans.errorhandlers.UnauthorizedAccessException;
import ru.gb.antonov.j71.beans.services.OrderService;
import ru.gb.antonov.j71.beans.services.OurUserService;
import ru.gb.antonov.j71.entities.dtos.OrderDetalesDto;
import ru.gb.antonov.j71.entities.dtos.OrderDto;

import java.security.Principal;
import java.util.Collection;
import java.util.stream.Collectors;

@RequestMapping ("/api/v1/order")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService   orderService;
    private final OurUserService ourUserService;
//-------------------------------------------------------------------------------------------

/** Фронт запрашивает информацию по заказу, который пользователь собирается сделать.
На данный момент у нас есть только пользователь и его корзина. Нам предстоит определить,
как должен выглядеть заказ, и показать результат наших «исследований» пользователю для
подтверждения. */
    @GetMapping ("/details")
    public OrderDetalesDto getOrderDetales (Principal principal)  {

        checkRightsToMakeOrder (principal);
        return orderService.getOrderDetales (principal);
    }

/** Пользователь оценил наше вИдение его заказа и нажал кнопку «Оформить заказ». */
    @PostMapping ("/confirm")
    @ResponseStatus (HttpStatus.CREATED)
    public OrderDetalesDto applyOrderDetails (@RequestBody @Validated OrderDetalesDto orderDetalesDto,
                                              BindingResult br, Principal principal)
    {
        checkRightsToMakeOrder (principal);
        if (br.hasErrors())
            //преобразуем набор ошибок в список сообщений, и пакуем в одно общее исключение (в наше заранее для это приготовленное исключение).
            throw new OurValidationException (br.getAllErrors().stream()
                                                .map (ObjectError::getDefaultMessage)
                                                .collect (Collectors.toList ()));

        return orderService.applyOrderDetails (orderDetalesDto, principal);
    }

/** Фронт запрашивает список заказов пользователя. */
    @GetMapping ("/orders")
    public Collection<OrderDto> getOrders (Principal principal)  {
        return orderService.getUserOrdersAsOrderDtos (principal);
    }

/** Проверяем, зарегистрирован ли пользователь и бросаем исключение, если он не зарегистрирован.
    @throws UnauthorizedAccessException */
    private void checkRightsToMakeOrder (Principal principal) {
        if (principal == null)
            throw new UnauthorizedAccessException (
                "Заказ может оформить только авторизованый пользователь "
                + "(It's only authorized user can make order.).");
    }

    @PostMapping ("/pay")
    @ResponseStatus (HttpStatus.OK)
    public void payOrder (@RequestBody OrderDetalesDto orderDetalesDto, Principal principal)   {
        orderService.payOrder(orderDetalesDto, principal);
    }
}
