package ru.gb.antonov.j71.beans.errorhandlers;

import java.util.logging.Logger;

import static ru.gb.antonov.j71.Factory.USE_DEFAULT_STRING;

/** {@code HttpStatus.BAD_REQUEST = 400}<br>
    Умолчальный текст сообщения: " ОШИБКА! В фильтре неправильно указан диапазон цен. " */
public class FilterPriceException extends RuntimeException {

    final static String messageDefault = " ОШИБКА! В фильтре неправильно указан диапазон цен. ";
    final static Logger LOGGER = Logger.getLogger ("ru.gb.antonov.j71.beans.errorhandlers.FilterPriceException");

/** {@code HttpStatus.BAD_REQUEST = 400}<br>
    Умолчальный текст сообщения: " ОШИБКА! В фильтре неправильно указан диапазон цен. " */
    public FilterPriceException (String messageText, Throwable cause) {

        super (messageText = messageText == USE_DEFAULT_STRING ? messageDefault : messageText, cause);
        LOGGER.severe ("ОШИБКА: "+ messageText);
    }

    public FilterPriceException (String messageText) {

        super (messageText = messageText == USE_DEFAULT_STRING ? messageDefault : messageText);
        LOGGER.severe ("ОШИБКА: "+ messageText);
    }
}
