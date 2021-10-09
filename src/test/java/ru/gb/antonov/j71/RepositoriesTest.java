package ru.gb.antonov.j71;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.gb.antonov.j71.beans.repositos.OurUserRepo;
import ru.gb.antonov.j71.beans.repositos.ProductRepo;

@DataJpaTest    //< для тестирования репозиториев, плюс можно использовать TestEntityManager (аналог Hibernate'овской сессии).
@ActiveProfiles ("test")    //< указываем профиль, в котором хотим тестировать. Пока известно, что название профиля должно маячить в названии файла конфигурации; например, для профиля test файл конфигурации должен называться application-test.yaml. В нашем test-профиле будет загружена тестовая БД — resources/db/testdata/V1__initialize.sql, — т.к. именно она указана в application-test.yaml). Теоретически, нам ничто не мешает работать с обычной, НЕтестовой базой.
public class RepositoriesTest
{
//Можем указать несколько репозиториев:
    @Autowired OurUserRepo ourUserRepo;
    @Autowired ProductRepo productRepo;

    @Autowired TestEntityManager testEntityManager;

//TODO: файл V1__initialize.sql ещё пустой.

// Имеет смысл тестировать только какие-то нестандартные, сложные запросы, что-то посложнее обычных CRUD-операций.
    @Test public void testProductRepo () {}
    @Test public void testOurUserRepo () {}

}
