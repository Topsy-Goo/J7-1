package ru.gb.antonov.j71;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.auth.BasicUserPrincipal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.gb.antonov.j71.beans.errorhandlers.ResourceNotFoundException;
import ru.gb.antonov.j71.beans.services.CartService;
import ru.gb.antonov.j71.beans.services.ProductService;
import ru.gb.antonov.j71.entities.Product;
import ru.gb.antonov.j71.entities.ProductsCategory;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@SpringBootTest /* < используется, когда нужны Спринг-контекст и вёб-окружение. Использование @SpringBootTest приводит к подъёму всего web-контекста. Если нам все бины в контексте не нужны, то эту аннотацию нужно использовать с параметром classes=SomeOnly.class (можно указать массив классов). Ни тот, ни иной вариант использования этой аннотации не отменяет инекцию указанных в тест-классе бинов.  */
@ActiveProfiles ("test") //< используем тестовый профиль настроек приложения (будет загружена тестовая БД)
public class CartTest
{
    private static final boolean MOKED_PRODUCT_SERVICE = true; //< При смене значения на false нужно закомментировать объявление бина mockedProductService.
// тестируемый бин:
    @Autowired private CartService cartService;
//Oбе следующие переменные являются одним и тем же бином и полностью взаимозаменяемы в коде этого класса (на момент инъекции бинов, в контексте приложения будет существовать только мок-версия бина, которая и будет инжектиться всюду, где бин требуется):
    @MockBean private ProductService mockedProductService; //< закомментировать при MOKED_PRODUCT_SERVICE == false
    @Autowired private ProductService productService;
// бины, которые подделывать не нужно:
    @Autowired private RedisTemplate<String, Object> redisTemplate;


/** Тестируем многократное добавление одного и того же товара, чтобы убедиться, что количество товарных позиций в корзине от этого не меняется.  */
    @Test public void cartsSameProductAddingTest ()
    {
        long pid = 1L;
        Product product;
        if (!MOKED_PRODUCT_SERVICE)
        {
            product = productService.findById (pid);
        }
        else
        {
            product = Product.dummyProduct (pid, "Товар2", BigDecimal.valueOf(20.0), 20, null, null, null);
        }
        String uuid = UUID.randomUUID().toString();
        //Assertions.assertEquals (1, cartService.getCartLoad (null, uuid));

/*  Инструктируем Mockito возвращать некий объект всякий раз, когда будет вызываться productService.findById (2L). Это будет работать даже тогда, когда productService.findById (2L) будет вызываться где-то в недрах экземпляра CartService.
    Эта «инструкция» создаёт соотв.метод внутри поддельного ProductService. Тип параметра в doReturn() должен соответствовать типу, возвращаемому findById()!
*/
        if (MOKED_PRODUCT_SERVICE)
            Mockito.doReturn (product).when (productService).findById (pid);

        cartService.changeProductQuantity (null, uuid, pid, 1);
        cartService.changeProductQuantity (null, uuid, pid, 1);
        cartService.changeProductQuantity (null, uuid, pid, 1);
        cartService.changeProductQuantity (null, uuid, pid, 1);

        if (MOKED_PRODUCT_SERVICE)
            Mockito.verify (productService, Mockito.times (4)).findById (ArgumentMatchers.eq (pid));
        Assertions.assertEquals(1, cartService.getCartItemsCount (null, uuid));
        Assertions.assertEquals(4, cartService.getCartLoad (null, uuid));
        cartService.deleteCart (uuid);
    }

/** Добавление товара в корзину из непустой товарной позиции. */
    @Test public void cartLoadTest ()
    {
//TODO:после изменений в ProductDto некоторые методы выглядят лишними?
        Product product;
        long pid;
        if (!MOKED_PRODUCT_SERVICE)
        {
            //Код, который использует настоящий бин:
            product = productService.createProduct ("Товар2", BigDecimal.valueOf(20.0), 20, "D");
            pid = product.getId();
            product = productService.updateProduct (pid, null, null, 20, null);//TODO:удалить этот вызов?
        }
        else //Код, который использует только поддельный бин
        {
            pid = 1L;
            product = Product.dummyProduct (pid, "Товар2", BigDecimal.valueOf(20.0), 20, null, null, null);
            Mockito.doReturn (product).when (productService).findById (pid);
        }
        String uuid = UUID.randomUUID().toString();

    //Тестируем добавление товара в корзину, …:
        cartService.changeProductQuantity (null, uuid, pid,   1);
        Assertions.assertEquals (1, cartService.getCartLoad (null, uuid));

        cartService.changeProductQuantity (null, uuid, pid,   0);
        Assertions.assertEquals (1, cartService.getCartLoad (null, uuid));

        cartService.changeProductQuantity (null, uuid, pid,  -1);
        Assertions.assertEquals (0, cartService.getCartLoad (null, uuid));

        cartService.changeProductQuantity (null, uuid, pid,  11);
        Assertions.assertEquals (11, cartService.getCartLoad (null, uuid));

        cartService.changeProductQuantity (null, uuid, pid, -50);
        Assertions.assertEquals (0, cartService.getCartLoad (null, uuid));

        cartService.changeProductQuantity (null, uuid, pid, 100);
        Assertions.assertEquals (20, cartService.getCartLoad (null, uuid));

        cartService.deleteCart (uuid);
    }

/** Несколько дополнительных тестов:<br>
• добавление в корзину товара из пустой товарной позиции;<br>
• тест того, как изменения в свойствах товара отражаются на содержимом корзины. */
    @Test public void cartMoreTests ()
    {
//TODO:после изменений в ProductDto некоторые методы выглядят лишними?

        Product product, p;
        long pid;
        ProductsCategory pCat;
        if (!MOKED_PRODUCT_SERVICE)
        {
            //Код, который использует только настоящий бин:
            product = productService.createProduct ("Продукт Ф", BigDecimal.valueOf(99.99), 0, "D");
            pid = product.getId(); //< эффективли файнал, для лямбды
            product = productService.updateProduct (pid, null, null, 0, null);//TODO:удалить этот вызов?
        }
        else   //Код, который использует только поддельный бин.
        {
            pCat = ProductsCategory.dummyProductsCategory (100500L, "W", null, null, null);
            pid = 1L;
            product = Product.dummyProduct (pid, "Продукт Ф", BigDecimal.valueOf(99.99), 0, pCat, null, null);

            Mockito.doReturn (product).when (productService).findById (pid); //< ЗА ПРЕДЕЛАМИ этого класса такое объявление подменного метода работает в точности так, как было сказано на занятии.
        }

        String uuid = UUID.randomUUID().toString();
//Добавление товара в корзину из пустой товарной позиции в корзину:
        Assertions.assertThrows (ResourceNotFoundException.class,
                                 ()->cartService.changeProductQuantity (null, uuid, pid, 1));
        Assertions.assertEquals (cartService.getCartLoad (null, uuid), 0);

        if (MOKED_PRODUCT_SERVICE)
        {
/*  Совершенно случайно оказалось, что ВНУТРИ этого класса такое объявление метода:
        Mockito.doReturn (product.update (,,,123)).when (productService).updateProduct (pid,,,,123);
 является не просто объявлением, но ещё и является ВЫЗОВОМ МЕТОДА от имени подменённого бина. При этом последующий вызов
        ProductService.updateProduct(,,,,123)
ничего не делает, исключения не бросают и просто возвращает неизменённый объект типа Product. НО если этому методу не сделать мок-версию, то он будет возвращать null.
*/
            p = productService.updateProduct (pid, null, null, 4444, null); //< Возвращает null
            p = productService.updateProduct (pid, null, null, 123, null); //< Возвращает null
            Mockito.doReturn (product.update (null, null, 123, null))
                   .when (productService).updateProduct (pid, null, null, 123, null); //< Устанавливает товару остаток в значение 123.
            p = productService.updateProduct (pid, null, null, 4444, null); //< Возвращает null
            p = productService.updateProduct (pid, null, null, 123, null); //< Возвращает product в неизменённом виде.
        }
//Тестируем то, как изменение товара отражается на товаре в корзине.

        if (MOKED_PRODUCT_SERVICE)
            Mockito.doReturn (product.update (null, null, 1, null))
                   .when (productService).updateProduct (pid, null, null, 1, null); //< Устанавливает товару остаток в значение 1.
        p = productService.updateProduct (pid, null, null, 1, null);
        cartService.changeProductQuantity (null, uuid, pid, 10);
        Assertions.assertEquals (cartService.getCartLoad (null, uuid), 1);
        Assertions.assertEquals (cartService.getCartCost (null, uuid), BigDecimal.valueOf(99.99));

        if (MOKED_PRODUCT_SERVICE)
            Mockito.doReturn (product.update (null, BigDecimal.valueOf(36.6), null, null))
                   .when (productService).updateProduct (pid, null, BigDecimal.valueOf(36.6), null, null); //< Устанавливает товару цену в значение 36.6.
        p = productService.updateProduct (pid, null, BigDecimal.valueOf(36.6), null, null);
        Assertions.assertEquals (cartService.getCartLoad (null, uuid), 1);
        Assertions.assertEquals (cartService.getCartCost (null, uuid), BigDecimal.valueOf(36.6));

        if (MOKED_PRODUCT_SERVICE)
            p = productService.updateProduct (pid, null, null, 123, null); //< Возвращает product в неизменённом
        cartService.deleteCart (uuid);
    }

/** (Этот метод не доделан: всё работает, но непонятно, как это применить к хоть сколько-нибудь интересному тестированию. Но зато в нём удалось попрактиковаться в работе с JSON.) */
    @Test public void cartsMergeTest () throws JsonProcessingException //< для readValue
    {
//создаём корзины admin и guest и наполняем их корзины товарнвми позициями (пустыми и непустыми)
        String jsonImcTest1 = "{\"citems\":[{\"pid\":1,\"quantity\":0},{\"pid\":2,\"quantity\":1},{\"pid\":3,\"quantity\":2},{\"pid\":4,\"quantity\":1}]}";
        String jsonImcTest2 = "{\"citems\":[{\"pid\":3,\"quantity\":0},{\"pid\":4,\"quantity\":1},{\"pid\":5,\"quantity\":2},{\"pid\":6,\"quantity\":1}]}";
        ObjectMapper oMapper = new ObjectMapper();
        CartService.InMemoryCart imcTest1 = oMapper.readValue (jsonImcTest1, CartService.InMemoryCart.class);
        CartService.InMemoryCart imcTest2 = oMapper.readValue (jsonImcTest2, CartService.InMemoryCart.class);
//создаём корзины в кэше
        String admin = "admin";
        String test1 = "test1";
        String test2 = "test2";
        Duration cartlife = Duration.ofDays(1L);
        redisTemplate.opsForValue().set (test1, imcTest1);      redisTemplate.expire (test1, cartlife);
        redisTemplate.opsForValue().set (test2, imcTest2);      redisTemplate.expire (test2, cartlife);
        //(Для admin корзина создаётся автоамтически.)
//сливаем корзины:
        cartService.mergeCarts (new BasicUserPrincipal (admin), test1);
        CartService.InMemoryCart imcAdmin = (CartService.InMemoryCart) redisTemplate.opsForValue ().get (admin);
//что-то проверяем
        //...
//сливаем корзины:
        cartService.mergeCarts (new BasicUserPrincipal (admin), test2);
        imcAdmin = (CartService.InMemoryCart) redisTemplate.opsForValue ().get (admin);
//что-то проверяем
        //...
//очистка
        cartService.deleteCart (test1); //TODO: корзины не удаляются!!
        cartService.deleteCart (test2);
        cartService.deleteCart (admin);
    }
}
