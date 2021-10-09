package ru.gb.antonov.j71;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import ru.gb.antonov.j71.beans.services.OrderService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SpyTest
{
/**  Бин-шпион — это класс, который ведёт себя как обычный класс, но (1)за ним можно следить, (2)ему можем подменять поведение (как у мок-бинов). Применяется для связанных бинов в случаях, когда на поведение одного бина второй должен реагировать нужным нам способом.
*/
    @Spy private List<Integer> spiedList = new ArrayList<> ();
    @SpyBean private OrderService orderService;

    @Test public void spyTest()
    {
        spiedList.add(1);
        spiedList.add(2);
        spiedList.add(3);

        Mockito.verify (spiedList).add(1);
        Mockito.verify (spiedList).add(2);
        Mockito.verify (spiedList).add(3);
        assertEquals (3, spiedList.size());

        Mockito.doReturn(100).when (spiedList).size();
        assertEquals(100, spiedList.size());
    }
}
