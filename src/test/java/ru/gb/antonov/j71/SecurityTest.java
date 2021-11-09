package ru.gb.antonov.j71;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.gb.antonov.j71.beans.services.ProductService;
import ru.gb.antonov.j71.entities.dtos.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc    //< чтобы использовать бин MockMvc
@ActiveProfiles ("test") //< используем тестовый профиль настроек приложения (будет загружена тестовая БД)
public class SecurityTest
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ProductService productService;
    private final ObjectMapper oMapper = new ObjectMapper();

/** Тестирование запроса списка продуктов (страницу №0). Запрос должен выполняться без учёта авторизации юзера. */
    @Test public void securityAccessAllowedTest () throws Exception
    {
        mockMvc.perform (get ("/api/v1/products/page?p=0"))
               .andDo (print())
               .andExpect (status().isOk()) //200
               .andExpect (jsonPath ("$.content").isArray());
    }

/** Проверяем, как реагирует приложение на попытки НЕавторизованного юзера (гостя) выполнить след.действия:<p>
• начать оформление заказа;<br>
• завершить оформление заказа;<br>
• удалить това по интексу;<br>
• создать новый товар;<br>
• изменить существующий товар. */
    @Test public void securityUnauthorizedTest () throws Exception
    {
//Оформление заказа навторизованным пользователем.
        mockMvc.perform (get ("/api/v1/order/details").contentType (MediaType.APPLICATION_JSON))
               .andDo (print())
               .andExpect (status().isUnauthorized());

        String strJson = oMapper.writeValueAsString (new OrderDetalesDto());

        mockMvc.perform (post ("/api/v1/order/confirm")
                         .contentType (MediaType.APPLICATION_JSON)
                         .content (strJson))
               .andDo (print())
               .andExpect (status().isUnauthorized()); //401

//Редактирование товара неавторизованным пользователем.
        mockMvc.perform (get ("/api/v1/products/delete/1").contentType (MediaType.APPLICATION_JSON))
               .andDo (print())
               .andExpect (status().isForbidden()); //403

        strJson = oMapper.writeValueAsString (new ProductDto());

        mockMvc.perform (post ("/api/v1/products")
                         .contentType (MediaType.APPLICATION_JSON)
                         .content (strJson))
               .andDo (print())
               .andExpect (status().isForbidden()); //403

        mockMvc.perform (put ("/api/v1/products")
                         .contentType (MediaType.APPLICATION_JSON)
                         .content (strJson))
               .andDo (print())
               .andExpect (status().isForbidden()); //403
    }

/** Проверяем, как реагирует приложение на попытки авторизованного рядового юзера ({@code ROLE_USER}) выполнить след.действия:<p>
• начать оформление заказа;<br>
• завершить оформление заказа;<br>
• удалить това по интексу;<br>
• создать новый товар;<br>
• изменить существующий товар. */
    @Test @WithMockUser (username="user1", roles="USER") //< префикс ROLE_ не указываем
    public void securityUserPermissionsTest () throws Exception
    {
//Оформление заказа пользователем с правами уровня USER.
        mockMvc.perform (get ("/api/v1/order/details").contentType (MediaType.APPLICATION_JSON))
               .andDo (print())
               .andExpect (status().isOk()); //200

        //Выполняем минимальную инициализацию OrderDetalesDto, т.к. для получения ответа Ok нужно пройти проверку на корректность переданных данных.
        OrderDetalesDto oddto = OrderDetalesDto.dummyOrderDetalesDto (ShippingInfoDto.dummyShippingInfoDto());
        String strJson = oMapper.writeValueAsString (oddto);

        mockMvc.perform (post ("/api/v1/order/confirm")
                         .contentType (MediaType.APPLICATION_JSON)
                         .content (strJson))
               .andDo (print())
               .andExpect (status().isCreated()); //201

//Редактирование товара пользователем с правами уровня USER.
        mockMvc.perform (get ("/api/v1/products/delete/1").contentType (MediaType.APPLICATION_JSON))
               .andDo (print())
               .andExpect (status().isForbidden()); //403

        strJson = oMapper.writeValueAsString (new ProductDto());

        mockMvc.perform (post ("/api/v1/products")
                         .contentType (MediaType.APPLICATION_JSON)
                         .content (strJson))
               .andDo (print())
               .andExpect (status().isForbidden()); //403

        mockMvc.perform (put ("/api/v1/products")
                         .contentType (MediaType.APPLICATION_JSON)
                         .content (strJson))
               .andDo (print())
               .andExpect (status().isForbidden()); //403
    }

/** Проверяем, как реагирует приложение на попытки администратора ({@code ROLE_ADMIN}) выполнить след.действия:<p>
• начать оформление заказа;<br>
• завершить оформление заказа;<br>
• удалить това по интексу;<br>
• создать новый товар;<br>
• изменить существующий товар. */
    @Test @WithMockUser (username="admin", roles="ADMIN")
    public void securityAdminPermissionsTest () throws Exception
    {
//Оформление заказа пользователем с правами уровня ADMIN.
        mockMvc.perform (get ("/api/v1/order/details").contentType (MediaType.APPLICATION_JSON))
               .andDo (print())
               .andExpect (status().isOk()); //200

        //Выполняем минимальную инициализацию OrderDetalesDto, т.к. для получения ответа Ok нужно пройти проверку на корректность переданных данных.
        OrderDetalesDto oddto = OrderDetalesDto.dummyOrderDetalesDto (ShippingInfoDto.dummyShippingInfoDto());
        String strJson = oMapper.writeValueAsString (oddto);

        mockMvc.perform (post ("/api/v1/order/confirm")
                         .contentType (MediaType.APPLICATION_JSON)
                         .content (strJson))
               .andDo (print())
               .andExpect (status().isCreated()); //201

//Редактирование товара пользователем с правами уровня ADMIN.
        mockMvc.perform (get ("/api/v1/products/delete/1").contentType (MediaType.APPLICATION_JSON))
               .andDo (print())
               .andExpect (status().isOk()); //200

        ProductDto pdto = ProductDto.dummyProductDto (null, "Товар0", BigDecimal.valueOf(99.9), 20, "D");
        strJson = oMapper.writeValueAsString (pdto);

        strJson = mockMvc.perform (post ("/api/v1/products")
                                   .contentType (MediaType.APPLICATION_JSON)
                                   .content (strJson))
                         .andDo (print())
                         .andExpect (status().isOk()) //200
                         .andReturn().getResponse().getContentAsString (StandardCharsets.UTF_8); //по умолчанию, кажется, используется ISO_8859_1; только UTF8, ASCII и ISO позволяют пройти тест, и только UTF8 не уродует уже изуродованные символы.

        mockMvc.perform (put ("/api/v1/products")
                         .contentType (MediaType.APPLICATION_JSON)
                         .content (strJson))
               .andDo (print())
               .andExpect (status().isOk()); //200
    }

/** Тестируем авторизацию существующего юзера: {@code логин = admin; password = 100}.
В качестве проверки выполняем запрос на оформление заказа. Предполагается, что у этого юзера
есть корзина с непустыми товарными позициями, или что код приложения поддерживает такое тестирование.  */
    @Test public void securityTokenTest () throws Exception
    {
        String strJson = oMapper.writeValueAsString (AuthRequest.dummyAuthRequest ("admin", "100"));

        MvcResult result = mockMvc.perform (post ("/api/v1/auth/login")
                                            .content (strJson)
                                            .contentType (MediaType.APPLICATION_JSON))
                                  .andDo (print())
                                  .andExpect (status().isOk())
                                  .andReturn();

        String string = result.getResponse().getContentAsString();
        AuthResponse ar = oMapper.readValue (string, AuthResponse.class);

        mockMvc.perform (get ("/api/v1/order/details")
                         .header ("Authorization", "Bearer " + ar.getToken()))
               .andDo (print())
               .andExpect (status().isOk()); //200
    }
}
