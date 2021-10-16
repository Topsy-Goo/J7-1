package ru.gb.antonov.j71;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.gb.antonov.j71.beans.services.ProductService;
import ru.gb.antonov.j71.entities.dtos.ProductDto;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.gb.antonov.j71.Factory.NO_FILTERS;
import static ru.gb.antonov.j71.Factory.PROD_PAGESIZE_DEF;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest
{
    @Autowired private MockMvc mockMvc; //< Наше ненастоящее вёб-окружение; он получает запросы и передаёт их на подходящий контроллер.
    @MockBean  private ProductService productService; //< бин, который не участвует в тесте, но нужен для работы тестируемого бина (вместо него будет использоваться «подделка», изготовленные Mockito).


    @Test public void testProductControllerPageRequest () throws Exception
    {
        List<ProductDto> pdtoList = new ArrayList<>();
        ProductDto pdto = ProductDto.dummyProductDto (2L, "Товар2", 20.0, 20, "W");
        pdtoList.add (pdto);
        Page<ProductDto> ppd = new PageImpl<>(pdtoList);

//Инструктируем BDDMockito, что возвращать при вызове productService.getPageOfProducts(0,6) (какой метод какого контроллера будет вызван, определяется запросом по аналогии с тем, как это делает сервлет-диспетчер):
        BDDMockito.given (productService.getPageOfProducts (0, PROD_PAGESIZE_DEF, NO_FILTERS)).willReturn (ppd);

//В первых двух строчках выполняется конфигурирование запроса.
        mockMvc.perform (get ("/api/v1/products/page?p=0").contentType (MediaType.APPLICATION_JSON))
//В этот момент нам приходит ответ на только что сформированный запрос. Ответ приходит от нашего контроллера.
               .andDo (print())             //< логирование
               .andExpect (status().isOk())
               .andExpect (jsonPath("$.content").isArray()) //< content — это List<T>-поле в PageImpl/Chunk, которое в JSON-не имеет вид массива
               .andExpect (jsonPath("$.content", hasSize(1)))
               .andExpect (jsonPath("$.content[0].title", is(pdto.getTitle())));
    }
}
