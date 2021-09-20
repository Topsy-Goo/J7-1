package ru.gb.antonov.j71.beans.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.antonov.j71.beans.services.OrderService;
import ru.gb.antonov.j71.beans.services.OurUserService;
import ru.gb.antonov.j71.entities.dtos.OrderDto;
import ru.gb.antonov.j71.entities.dtos.UserInfoDto;

import java.security.Principal;
import java.util.Collection;

@RequestMapping ("/api/v1/user_profile")
@RestController
@RequiredArgsConstructor
public class UserProfileController
{
    private final OurUserService ourUserService;
    private final OrderService orderService;
//--------------------------------------------------------------------------

    @GetMapping ("/userinfo")
    public UserInfoDto getUserInfo (Principal principal)
    {
        return ourUserService.getUserInfoDto (principal);
    }

    @GetMapping ("/orders")
    public Collection<OrderDto> getOrders (Principal principal)
    {
        return orderService.getUserOrdersAsOrderDtos (principal);
    }
}
