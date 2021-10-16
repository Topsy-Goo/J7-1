package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gb.antonov.j71.entities.OurUser;
import ru.gb.antonov.j71.entities.ProductReview;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ProductReviewsRepo extends CrudRepository<ProductReview, Long>
{
    Collection<ProductReview> findAllByProductId (Long id);
    Optional<ProductReview> findByProductIdAndOurUser (Long pid, OurUser u);
}
