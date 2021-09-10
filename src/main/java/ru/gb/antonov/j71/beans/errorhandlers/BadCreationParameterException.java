package ru.gb.antonov.j71.beans.errorhandlers;

public class BadCreationParameterException extends IllegalArgumentException
{
    public BadCreationParameterException (String messageText)
    {
        super (messageText);
    }
}
