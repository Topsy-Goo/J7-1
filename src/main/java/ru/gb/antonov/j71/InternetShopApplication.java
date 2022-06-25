package ru.gb.antonov.j71;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@SpringBootApplication (scanBasePackages = "ru.gb.antonov.j71.beans")
public class InternetShopApplication {

    @Autowired private Environment env;

    @PostConstruct private void init() { Factory.init (env); }

    public static void main (String[] args) {
        SpringApplication.run (InternetShopApplication.class, args);
    }
}
/*	   План на курс:
	    1. Сделать регистрацию пользователей на отдельной странице
	    2. Сделать корзину (+ добавить редис)
	    3. TODO: История просмотра товаров
	    4. Отзывы к товарам
	    5. TODO: Сделать дерево категорий товаров
	    6. TODO: Блок наиболее полпулярных товаров
	    7. Личный кабинет пользователя
	    8. Побольше разделения прав пользователей (юзер, админ, суперадмин)
	    9. Сделать оформление заказов
	   10. TODO: Добавить платежную систему (добавлена в 7-10, но лучше бы её там не было, — при тестировании приходится подключаться к грёбаному ПэйПал)
	   11. Фильтрация товаров
	   12. TODO: Почтовая рассылка
	   13. TODO: Поиск по сайту (возможно даже умный)
	   14. TODO: Добавить картинки к товарам
	   *. *** Админка
	   *. Рассмотреть MapStruct
	   *. Добавить на фронте валидацию токенов
       *. Добавить Docker compose file
*/
