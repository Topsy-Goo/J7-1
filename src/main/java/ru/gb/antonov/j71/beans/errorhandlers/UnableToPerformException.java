package ru.gb.antonov.j71.beans.errorhandlers;

import java.util.logging.Logger;

/** {@code HttpStatus.INTERNAL_SERVER_ERROR = 500}<br>
    Умолчальный текст сообщения: " Не удалось выполнить запрошенное действие. "  */
public class UnableToPerformException extends IllegalArgumentException {

    final static String messageDefault = " Не удалось выполнить запрошенное действие. ";
    final static Logger LOGGER         = Logger.getLogger ("ru.gb.antonov.j71.beans.errorhandlers.UnableToPerformException");

/** {@code HttpStatus.INTERNAL_SERVER_ERROR = 500}<br>
    Умолчальный текст сообщения: " Не удалось выполнить запрошенное действие. "  */
    public UnableToPerformException (String messageText) {

        super (messageText = messageText != null ? messageText : messageDefault);
        LOGGER.severe ("ОШИБКА: "+ messageText);
    }
}
