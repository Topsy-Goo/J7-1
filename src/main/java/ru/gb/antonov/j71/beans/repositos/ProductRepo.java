package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.antonov.j71.entities.Product;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long>
{
    //List<Product> findAllByCostBetween (double minPrice, double maxPrice);
}
