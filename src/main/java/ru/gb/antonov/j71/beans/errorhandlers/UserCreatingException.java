package ru.gb.antonov.j71.beans.errorhandlers;

public class UserCreatingException extends IllegalArgumentException
{
    public UserCreatingException (String messageText)
    {
        super (messageText);
    }
}
