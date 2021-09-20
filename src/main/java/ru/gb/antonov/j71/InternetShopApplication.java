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
	// План на курс:
	//  1. Сделать регистрацию пользователей на отдельной странице
        // Домашнее задание:
        // 1. Сделать на backend'е оформление заказа с сохранением его в БД
        // 2. * Привязывать заказ к текущему пользователю
	//  2. TODO: Сделать корзину (+ добавить редис)
        // Домашнее задание:
        // 1. * Попробовать реализовать корзины + редис (Задача довольно тяжелая)
        // 2. Если с кодом тяжело, то хотя бы текстом опишите логику работы (общую идею)
	//  3. TODO: История просмотра товаров
	//  4. TODO: Комментарии/рейтинги/отзывы товаров
	//  5. TODO: Сделать дерево категорий товаров
	//  6. TODO: Блок наиболее полпулярных товаров
	//  7. TODO: Начисление бонусов, личный кабинет пользователя
	//  8. TODO: Побольше разделения прав пользователей (юзер, админ, суперадмин)
	//  9. TODO: Сделать оформление заказов
	// 10. TODO: Добавить платежную систему
	// 11. TODO: Фильтрация товаров
	// 12. TODO: Почтовая рассылка
	// 13. TODO: Поиск по сайту (возможно даже умный)
	// *. ** Акции
	// *. *** Админка
	// *. Рассмотреть MapStruct

