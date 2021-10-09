package ru.gb.antonov.j71;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.auth.BasicUserPrincipal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.gb.antonov.j71.beans.errorhandlers.ResourceNotFoundException;
import ru.gb.antonov.j71.beans.services.CartService;
import ru.gb.antonov.j71.beans.services.ProductService;
import ru.gb.antonov.j71.entities.Product;

import java.time.Duration;
import java.util.UUID;

@SpringBootTest /* < используется, когда нужны Спринг-контекст и вёб-окружение. Использование @SpringBootTest приводит к подъёму всего web-контекста. Если нам все бины в контексте не нужны, то эту аннотацию нужно использовать с параметром classes=SomeOnly.class (можно указать массив классов). Ни тот, ни иной вариант использования этой аннотации не отменяет инекцию указанных в тест-классе бинов.  */
@ActiveProfiles ("test") //< используем тестовый профиль настроек приложения (будет загружена тестовая БД)
public class CartTest
{
//тестируемый бин
    @Autowired private CartService cartService;
//бины, которые подделывать не нужно:
    @Autowired private ProductService productService;
    @Autowired private RedisTemplate<String, Object> redisTemplate;
//-----------------------------------------------------------------------------
/** Добавление товара в корзину из непустой товарной позиции. */
    @Test public void cartLoadTest ()
    {
        Product product = productService.createProduct ("Товар2", 20.0, "D");
        long pid = product.getId();

        product = productService.updateProduct (pid, null, null, null, 20);
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
        String uuid = UUID.randomUUID().toString();
        Product product = productService.createProduct ("Продукт Ф", 99.99, "D");
        final long pid = product.getId(); //< эффективли файнал, для лямбды

//Добавление товара в корзину из пустой товарной позиции в корзину:
        product = productService.updateProduct (pid, null, null, null, 0);
        Assertions.assertThrows (ResourceNotFoundException.class,
                                 ()->cartService.changeProductQuantity (null, uuid, pid, 1));
        Assertions.assertEquals (cartService.getCartLoad (null, uuid), 0);

//Тестируем то, как изменение товара отражается на товаре в корзине.
        product = productService.updateProduct (product.getId(), null, null, null, 1);
        cartService.changeProductQuantity (null, uuid, pid, 10);
        Assertions.assertEquals (cartService.getCartLoad (null, uuid), 1);
        Assertions.assertEquals (cartService.getCartCost (null, uuid), 99.99);

        product = productService.updateProduct (pid, null, 36.6, null, null);
        Assertions.assertEquals (cartService.getCartLoad (null, uuid), 1);
        Assertions.assertEquals (cartService.getCartCost (null, uuid), 36.6);

        cartService.deleteCart (uuid);
    }

/*    @Test public void cartsMergeTest () throws JsonProcessingException //< для readValue
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
    }*/
}
/*  Инструктируем Mockito возвращать некий объект всякий раз, когда будет вызываться productService.findById (2L). Это будет работать даже тогда, когда productService.findById (2L) будет вызываться где-то в недрах экземпляра CartService.
    Наверное, эта «инструкция» просто создаёт соотв.метод внутри поддельного ProductService. В пользу этого предположения говорит и то, что попытка вызвать из такого бина метод «без предупреждения» приведёт к исключению.
    Тип параметра в doReturn() должен соответствовать типу, возвращаемому findById()!

    Mockito.doReturn (product).when (mockedProductService).findById (2L);
    ...
    Mockito.verify (mockedProductService, Mockito.times (0)).findById (ArgumentMatchers.eq (2L));
*/