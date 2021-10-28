package ru.gb.antonov.j71.beans.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.antonov.j71.beans.services.OurUserService;
import ru.gb.antonov.j71.entities.dtos.UserInfoDto;

import java.security.Principal;

@RequestMapping ("/api/v1/user_profile")
@RestController
@RequiredArgsConstructor
public class UserProfileController
{
    private final OurUserService ourUserService;
//--------------------------------------------------------------------------

    @GetMapping ("/userinfo")
    public UserInfoDto getUserInfo (Principal principal)
    {
        return ourUserService.getUserInfoDto (principal);
    }
}
