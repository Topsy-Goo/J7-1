package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gb.antonov.j71.entities.OrderItem;

@Repository
public interface OrderItemRepo extends CrudRepository<OrderItem, Long>
{
}
