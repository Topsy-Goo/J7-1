package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gb.antonov.j71.entities.OrderItem;

import java.util.List;

@Repository
public interface OrderItemRepo extends CrudRepository<OrderItem, Long>
{
    @Query (value = "SELECT * FROM orderitems WHERE product_id = :pid AND order_id in " +
                    "(SELECT id FROM orders WHERE ouruser_id = :uid AND orderstate_id = :order_state);",
            nativeQuery = true)
    List<OrderItem> userOrderItemsByProductId (@Param("uid") Long uid,
                                               @Param("pid") Long pid,
                                               @Param("order_state") Integer stateId);
}
