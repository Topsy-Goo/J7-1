package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.gb.antonov.j71.entities.Product;

import java.util.List;
import java.util.Optional;

/** Наследование от {@code JpaSpecificationExecutor<T>} требуется для использования «спецификайий», которые используются в фильтрах (см. {@code ProductSpecification}). Это наследование увеличивает количество перегруженных методов findAll(…), которые может вызывать {@code ProductRepo}.
*/
@Repository
public interface ProductRepo extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>
{
    Optional<List<Product>> findAllByIdBetween (Long from, Long to);
}
