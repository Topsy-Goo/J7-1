package ru.gb.antonov.j71.beans.errorhandlers;

/** {@code HttpStatus.BAD_REQUEST = 400} */
public class BadCreationParameterException extends IllegalArgumentException
{
/** {@code HttpStatus.BAD_REQUEST = 400} */
    public BadCreationParameterException (String messageText) { super (messageText); }
}
