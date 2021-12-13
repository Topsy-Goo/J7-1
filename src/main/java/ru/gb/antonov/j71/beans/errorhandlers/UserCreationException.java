package ru.gb.antonov.j71.beans.errorhandlers;

import java.util.logging.Logger;

import static ru.gb.antonov.j71.Factory.USE_DEFAULT_STRING;

/** {@code HttpStatus.BAD_REQUEST = 400}<br>
    Умолчальный текст сообщения: " Не удалось создать пользователя. " */
public class UserCreationException extends IllegalArgumentException {

    final static String messageDefault = " Не удалось создать пользователя. ";
    final static Logger LOGGER = Logger.getLogger ("ru.gb.antonov.j71.beans.errorhandlers.UserCreationException");

/** {@code HttpStatus.BAD_REQUEST = 400}<br>
    Умолчальный текст сообщения: " Не удалось создать пользователя. " */
    public UserCreationException (String messageText) {

        super (messageText = messageText == USE_DEFAULT_STRING ? messageDefault : messageText);
        LOGGER.severe ("ОШИБКА: "+ messageText);
    }
}
