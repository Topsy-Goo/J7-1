package ru.gb.antonov.j71.beans.errorhandlers;

public class ProductUpdatingException extends IllegalArgumentException
{
    public ProductUpdatingException (String messageText)
    {
        super (messageText);
    }
}
