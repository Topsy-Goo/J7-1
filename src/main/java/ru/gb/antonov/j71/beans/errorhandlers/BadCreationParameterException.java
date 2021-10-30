package ru.gb.antonov.j71.beans.errorhandlers;

/** {@code HttpStatus.BAD_REQUEST = 400}<br>
    Умолчальный текст сообщения: " Переданы некорректные параметры. " */
public class BadCreationParameterException extends IllegalArgumentException
{
    static final String messageDefault = " Переданы некорректные параметры. ";

/** {@code HttpStatus.BAD_REQUEST = 400}}<br>
    Умолчальный текст сообщения: " Переданы некорректные параметры. " */
    public BadCreationParameterException (String messageText)
    {
        super (messageText == null ? messageDefault : messageText);
    }
}
