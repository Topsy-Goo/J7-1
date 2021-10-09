package ru.gb.antonov.j71.beans.errorhandlers;

/** {@code HttpStatus.UNAUTHORIZED = 401} */
public class UnauthorizedAccessException extends RuntimeException
{
    public UnauthorizedAccessException (String text) { super (text); }
}
