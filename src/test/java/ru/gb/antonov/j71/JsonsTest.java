package ru.gb.antonov.j71;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.gb.antonov.j71.entities.dtos.ProductCategoryDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest //< тестирует JSON-(де)сериализацию. Делает возможным использование бина JacksonTester<SomeEntityType>. Такое тестирование обычно сводится к преобразованию объекта в JSON и обратно (см. класс JsonTests.java в доп.материалах к уроку J7-7).
public class JsonsTest
{
    @Autowired private JacksonTester<ProductCategoryDto> jackson;

    @Test public void jsonSerializationTest() throws Exception
    {
        ProductCategoryDto pCategoryDto = new ProductCategoryDto();
        pCategoryDto.setId (2L);
        pCategoryDto.setName ("B");
        //  { "id": 2, "name": "B" };
        assertThat (jackson.write (pCategoryDto))
                //Символ $ — обозначает полученный JSON-объект
                .hasJsonPathNumberValue("$.id")
                .extractingJsonPathStringValue("$.name").isEqualTo("B");
    }

    @Test public void jsonDeserializationTest() throws Exception
    {
        String content = "{\"id\": 2,\"name\":\"B\"}";
        ProductCategoryDto pCategoryDto = new ProductCategoryDto();
        pCategoryDto.setId (2L);
        pCategoryDto.setName ("B");

        assertThat (jackson.parse (content)).isEqualTo (pCategoryDto);
        assertThat (jackson.parseObject (content).getName()).isEqualTo("B");
    }
}
