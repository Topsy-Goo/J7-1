package ru.gb.antonov.j71.beans.errorhandlers;

import java.util.logging.Logger;

/** {@code HttpStatus.NOT_FOUND = 404}<br>
    Умолчальный текст сообщения: " Пользователь не найден. " */
public class UserNotFoundException extends RuntimeException {

    final static String messageDefault = " Пользователь не найден. ";
    final static Logger LOGGER = Logger.getLogger ("ru.gb.antonov.j71.beans.errorhandlers.UserNotFoundException");

/** {@code HttpStatus.NOT_FOUND = 404}<br>
    Умолчальный текст сообщения: " Пользователь не найден. " */
    public UserNotFoundException (String messageText) {

        super (messageText = messageText == null ? messageDefault : messageText);
        LOGGER.severe ("ОШИБКА: "+ messageText);
    }

    public UserNotFoundException (String messageText, Throwable cause) {

        this (cause.getLocalizedMessage() + messageText);
        this.initCause (cause);
    }
}
