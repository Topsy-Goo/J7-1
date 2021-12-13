package ru.gb.antonov.j71.beans.errorhandlers;

import java.util.logging.Logger;

public class ResourceNotFoundException extends RuntimeException {

    final static String messageDefault = " Ресурс не найден. ";
    final static Logger LOGGER         = Logger.getLogger ("ru.gb.antonov.j71.beans.errorhandlers.ResourceNotFoundException");

    public ResourceNotFoundException (String messageText) {

        super (messageText = messageText == null ? messageDefault : messageText);
        LOGGER.severe ("ОШИБКА: "+ messageText);

    }
}
