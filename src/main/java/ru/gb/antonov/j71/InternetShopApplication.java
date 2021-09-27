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
/*	   План на курс:

	    1. Сделать регистрацию пользователей на отдельной странице
	    2. Сделать корзину (+ добавить редис)

	    3. TODO: История просмотра товаров
	    4. TODO: Комментарии/рейтинги/отзывы товаров
	    5. TODO: Сделать дерево категорий товаров
	    6. TODO: Блок наиболее полпулярных товаров
	    7. TODO: Начисление бонусов, личный кабинет пользователя
	    8. TODO: Побольше разделения прав пользователей (юзер, админ, суперадмин)
	    9. TODO: Сделать оформление заказов
	   10. TODO: Добавить платежную систему
	   11. TODO: Фильтрация товаров
	   12. TODO: Почтовая рассылка
	   13. TODO: Поиск по сайту (возможно даже умный)
	   *. ** Акции
	   *. *** Админка
	   *. Рассмотреть MapStruct
*/
