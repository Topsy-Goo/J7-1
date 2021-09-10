package ru.gb.antonov.j71;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication (scanBasePackages = "ru.gb.antonov.j71.beans")
public class InternetShopApplication
{

    public static void main (String[] args)
    {
        SpringApplication.run (InternetShopApplication.class, args);
    }

}
