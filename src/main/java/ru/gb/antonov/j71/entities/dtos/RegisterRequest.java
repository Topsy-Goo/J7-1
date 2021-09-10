package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/*  Структура для передачи данных о регистрации нового пользователя от клиента на бэк.
*/
@Data
@NoArgsConstructor
public class RegisterRequest
{
    @NotNull (message="задайте логин (3…32 латинских сиволов и/или цифр)\r")
    @Length (min=3, max=32, message="длина логина — [3…32] латинских символов и/или цифр\r")
    private String login;

    @NotNull (message="задайте пароль (3…128 символов)\r")
    @Length (min=3, max=128, message="длина пароля — [3…128] символов\r")
    private String password;

    /*  Можно сделать два пароля при регистрации : пароль и подтверждение, -- и прверять,
    чтобы они совпадали.
    */

    @NotNull (message="укажите адрес электронной почты (5…64 символов); формат — xxx@xxx.xx\r")
    @Length (min=5, max=64, message="длина адреса — 5…64 символов; формат — xxx@xxx.xx\r")
    private String email;
}
