package ru.gb.antonov.j71.beans.errorhandlers;

import java.util.logging.Logger;

/** {@code HttpStatus.BAD_REQUEST = 400}<br>
    Умолчальный текст сообщения: " Переданы некорректные параметры. " */
public class BadCreationParameterException extends IllegalArgumentException {

    final static String messageDefault = " Переданы некорректные параметры. ";
    final static Logger LOGGER         = Logger.getLogger ("ru.gb.antonov.j71.beans.errorhandlers.BadCreationParameterException");

/** {@code HttpStatus.BAD_REQUEST = 400}}<br>
    Умолчальный текст сообщения: " Переданы некорректные параметры. " */
    public BadCreationParameterException (String messageText) {

        super (messageText = messageText == null ? messageDefault : messageText);
        LOGGER.severe ("ОШИБКА: "+ messageText);
    }
}
