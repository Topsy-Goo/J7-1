package ru.gb.antonov.j71.beans.errorhandlers;

import java.util.logging.Logger;

/** {@code HttpStatus.UNAUTHORIZED = 401}}<br>
    Умолчальный текст сообщения: " Авторизуйтесь! " */
public class UnauthorizedAccessException extends RuntimeException {

    final static String messageDefault = " Авторизуйтесь! ";
    final static Logger LOGGER         = Logger.getLogger ("ru.gb.antonov.j71.beans.errorhandlers.UnauthorizedAccessException");

/** {@code HttpStatus.UNAUTHORIZED = 401}}<br>
    Умолчальный текст сообщения: " Авторизуйтесь! " */
    public UnauthorizedAccessException (String messageText) {

        super (messageText = messageText != null ? messageText : messageDefault);
        LOGGER.severe ("ОШИБКА: "+ messageText);
    }
}
