package ru.gb.antonov.j71.beans.errorhandlers;

/** {@code HttpStatus.FORBIDDEN = 403}<br>
    Умолчальный текст сообщения: " Доступ запрещён. " */
public class AccessDeniedException extends RuntimeException
{
    static final String messageDefault = " Доступ запрещён. ";

/** {@code HttpStatus.FORBIDDEN = 403}<br>
    Умолчальный текст сообщения: " Доступ запрещён. " */
    public AccessDeniedException (String messageText)
    {
        super (messageText == null ? messageDefault : messageText);
    }
}
