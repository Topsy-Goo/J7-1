package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import static ru.gb.antonov.j71.Factory.*;

@Data
@NoArgsConstructor
public class AuthRequest
{
    @NotNull (message="\rНе указан логин!")
    @Length (min=LOGIN_LEN_MIN, max=LOGIN_LEN_MAX, message="\rДлина логина: 3…32 символов!")
    private String login;

    @NotNull (message="\rНе указан пароль!")
    @Length (min=PASS_LEN_MIN, max=PASS_LEN_MAX, message="\rДлина пароля: 3…128 символов!")
    private String password;
}
