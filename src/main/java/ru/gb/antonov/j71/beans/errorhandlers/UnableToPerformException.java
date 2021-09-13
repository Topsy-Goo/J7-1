package ru.gb.antonov.j71.beans.errorhandlers;

public class UnableToPerformException extends IllegalArgumentException
{
    public UnableToPerformException (String messageText) { super (messageText); }
}
