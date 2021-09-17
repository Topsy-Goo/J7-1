package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/*  Структура для передачи данных об авторизации пользователя от клинета на бэк.
*/
@Data
@NoArgsConstructor
public class AuthRequest
{
    private String login;

    private String password;
}
