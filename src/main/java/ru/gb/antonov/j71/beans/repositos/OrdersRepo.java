package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gb.antonov.j71.entities.Order;
import ru.gb.antonov.j71.entities.OurUser;

import java.util.List;

@Repository
public interface OrdersRepo extends JpaRepository<Order, Long>
{
    //@Query ("select o from Order o where o.ouruser.id = :ouruser.id")
    //@Query ("select o from Order o where o.ouruser.id = :id")
    List<Order> findAllByOuruser (OurUser ouruser);
}
