package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/*  Структура для передачи данных об авторизации пользователя от бэка к клинету.
*/
@Data
@NoArgsConstructor
public class AuthResponse
{
    private String token;

    public AuthResponse (String token) { this.token = token;   }
}
