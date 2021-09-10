package ru.gb.antonov.j71.beans.errorhandlers;

public class ResourceNotFoundException extends RuntimeException
{
    public ResourceNotFoundException (String messageText)
    {
        super (messageText);
    }
}
