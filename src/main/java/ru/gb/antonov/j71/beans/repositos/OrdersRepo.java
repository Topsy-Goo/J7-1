package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gb.antonov.j71.entities.Order;

@Repository
public interface OrdersRepo extends CrudRepository<Order, Long>
{
}
