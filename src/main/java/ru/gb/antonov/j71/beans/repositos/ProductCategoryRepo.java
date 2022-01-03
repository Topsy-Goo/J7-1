package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.gb.antonov.j71.entities.ProductsCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepo extends JpaRepository<ProductsCategory, Integer> {

    Optional<ProductsCategory> findByName (String name);

    @Query (value = "SELECT name FROM categories;", nativeQuery = true)
    List<String> findAllNames ();
}
