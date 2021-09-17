package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class RegisterRequest
{
    @NotNull (message="\rЗадайте логин (3…32 латинских сиволов и/или цифр).")
    @Length (min=3, max=32, message="\rДлина логина — 3…32 латинских символов и/или цифр.")
    private String login;

    @NotNull (message="\rЗадайте пароль (3…128 символов).")
    @Length (min=3, max=128, message="\rДлина пароля — 3…128 символов.")
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
