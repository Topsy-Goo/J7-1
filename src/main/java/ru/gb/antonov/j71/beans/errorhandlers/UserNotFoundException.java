package ru.gb.antonov.j71.beans.errorhandlers;

public class UserNotFoundException extends RuntimeException
{
    public UserNotFoundException (String messageText)
    {
        super (messageText);
    }
}
