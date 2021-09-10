package ru.gb.antonov.j71.beans.errorhandlers;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/*   Сообщение, которое отправлем клиенту (в виде JSON'а), если его запрос некорректен.   */
@Data
@NoArgsConstructor
public class ErrorMessage
{
    private List<String> messages;
    private Date         date;

//---------------- конструкторы -----------------------------------

    public ErrorMessage (List<String> strings)
    {
        messages = strings;
        date = new Date();
    }

    public ErrorMessage (String text)
    {
        this (List.of(text));
    }

    public ErrorMessage (String ... messages)
    {
        this (Arrays.asList(messages));
    }
//---------------- геттеры и сесстеры -----------------------------

    public List<String> getMessages() {   return Collections.unmodifiableList (messages);   }
}
