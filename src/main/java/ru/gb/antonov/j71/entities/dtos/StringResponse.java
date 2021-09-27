package ru.gb.antonov.j71.entities.dtos;

import lombok.Data;

@Data
public class StringResponse
{
    private String value;

    public StringResponse() {}
    public StringResponse (String value) { this.value = value; }
}
