package ru.gb.antonov.j71.beans.errorhandlers;

/** {@code HttpStatus.UNAUTHORIZED = 401}}<br>
    Умолчальный текст сообщения: " Авторизуйтесь! " */
public class UnauthorizedAccessException extends RuntimeException
{
    static final String messageDefault = " Авторизуйтесь! ";

/** {@code HttpStatus.UNAUTHORIZED = 401}}<br>
    Умолчальный текст сообщения: " Авторизуйтесь! " */
    public UnauthorizedAccessException (String messageText)
    {
        super (messageText != null ? messageText : messageDefault);
    }
}
