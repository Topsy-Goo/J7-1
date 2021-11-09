package ru.gb.antonov.j71.beans.errorhandlers;

/** {@code HttpStatus.INTERNAL_SERVER_ERROR = 500}<br>
    Умолчальный текст сообщения: " Не удалось выполнить запрошенное действие. "  */
public class UnableToPerformException extends IllegalArgumentException {

    static final String messageDefault = " Не удалось выполнить запрошенное действие. ";

/** {@code HttpStatus.INTERNAL_SERVER_ERROR = 500}<br>
    Умолчальный текст сообщения: " Не удалось выполнить запрошенное действие. "  */
    public UnableToPerformException (String messageText) {

        super (messageText != null ? messageText : messageDefault);
    }
}
