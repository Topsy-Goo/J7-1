package ru.gb.antonov.j71.beans.repositos.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.gb.antonov.j71.entities.Product;

import static ru.gb.antonov.j71.Factory.PRODUCT_PRICE_FIELD_NAME;
import static ru.gb.antonov.j71.Factory.PRODUCT_TITLE_FIELD_NAME;

/** Класс содержит методы-обработчики фильтров для товаров.<p>
Требуется интерфейс {@code ProductRepo} унаследовать от {@code JpaSpecificationExecutor<T>}. */
public class ProductSpecification {

/** {@code root} некое его представление или обёртка, т.к. у нашего продукта просто нет метода get().<p>
    {@code criteriaBuilder} содержит методы, эквивалентные SQL-операторам сравнения.<p>
    {@code criteriaQuery} — ХЗ<p>
    Попытаемся выяснить, как слово "price" относится к полю Product.price.<p>
    Использование {@code public static} — по желанию.
*/
    public static Specification<Product> priceGreaterThanOrEqualsTo (double minPrice) {

        return (root, criteriaQuery, criteriaBuilder)->
            criteriaBuilder.greaterThanOrEqualTo (root.get (PRODUCT_PRICE_FIELD_NAME), minPrice);
    }

    public static Specification<Product> priceLessThanOrEqualsTo (double maxPrice) {

        return (root, criteriaQuery, criteriaBuilder)->
            criteriaBuilder.lessThanOrEqualTo (root.get (PRODUCT_PRICE_FIELD_NAME), maxPrice);
    }

/** Здесь конструкция {@code ….like(…"%" + title + "%")} имеет примерно тот же смысл, что и в
SQL — что-то «лайкаем».<p>
Строки переводятся в верхний регистр и после этого сравниваются. */
    public static Specification<Product> titleLike (String title) {
        return (root, criteriaQuery, criteriaBuilder)->
            criteriaBuilder.like (
                    criteriaBuilder.upper (root.get (PRODUCT_TITLE_FIELD_NAME)),
                    ("%" + title + "%").toUpperCase());
    }
    //SELECT DISTINCT COL_NAME FROM myTable WHERE UPPER(COL_NAME) LIKE UPPER('%title%');
}
