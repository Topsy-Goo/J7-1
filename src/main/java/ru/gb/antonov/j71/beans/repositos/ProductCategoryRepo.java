package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.antonov.j71.entities.ProductsCategory;

import java.util.Optional;

@Repository
public interface ProductCategoryRepo extends JpaRepository<ProductsCategory, Integer>
{
    Optional<ProductsCategory> findByName (String name);
}
