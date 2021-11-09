package ru.gb.antonov.j71.beans.errorhandlers;

/** {@code HttpStatus.NOT_FOUND = 404} */
public class UserNotFoundException extends RuntimeException {
/** {@code HttpStatus.NOT_FOUND = 404} */
    public UserNotFoundException (String messageText) { super (messageText); }
}
