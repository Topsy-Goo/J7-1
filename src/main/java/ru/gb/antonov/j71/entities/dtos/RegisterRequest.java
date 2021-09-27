package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import static ru.gb.antonov.j71.Factory.*;

@Data
@NoArgsConstructor
public class RegisterRequest
{
    @NotNull (message="\rЗадайте логин (3…32 латинских сиволов и/или цифр).")
    @Length (min= LOGIN_LEN_MIN, max=LOGIN_LEN_MAX, message="\rДлина логина — 3…36 латинских символов и/или цифр.")
    private String login;

    @NotNull (message="\rЗадайте пароль (3…128 символов).")
    @Length (min=PASS_LEN_MIN, max=PASS_LEN_MAX, message="\rДлина пароля — 3…128 символов.")
    private String password;

    /*  Можно сделать два пароля при регистрации : пароль и подтверждение, -- и проверять,
    чтобы они совпадали.
    */

    @NotNull (message="\rПочта указана неполностью: Имя пользователя.")
    @Length (min=1/*, message=""*/)
    private String emailUser;

    @NotNull (message="\rПочта указана неполностью: Имя сервера.")
    @Length (min=1/*, message=""*/)
    private String emailServer;

    @NotNull (message="\rПочта указана неполностью: Домен.")
    @Length (min=1/*, message=""*/)
    private String emailDomain;
}
