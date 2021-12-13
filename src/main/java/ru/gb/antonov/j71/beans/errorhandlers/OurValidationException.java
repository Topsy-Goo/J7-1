package ru.gb.antonov.j71.beans.errorhandlers;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

//Сласс создан для использования возможностей hibernate.validator'а.
//Об использовании см. GlobalExceptionHandler, ErrorMessage, ProductController.createProduct() и Product.
public class OurValidationException extends RuntimeException {

    private final List<String> messages;
    final static  Logger       LOGGER = Logger.getLogger ("ru.gb.antonov.j71.beans.errorhandlers.OurValidationException");

    public OurValidationException (List<String> strings) {
        messages = strings;
        LOGGER.severe ("ОШИБКА: "+ strings.toString());
    }

    public List<String> getMessages() {   return Collections.unmodifiableList (messages);   }
}
