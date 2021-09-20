package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.antonov.j71.Factory;
import ru.gb.antonov.j71.beans.repositos.OrderStatesRepo;
import ru.gb.antonov.j71.entities.OrderState;

import javax.annotation.PostConstruct;

import static ru.gb.antonov.j71.Factory.*;

@Service
@RequiredArgsConstructor
public class OrderStatesService
{
    private final OrderStatesRepo orderStatesRepo;
/*    private Iterable<OrderState> states;

    @PostConstruct
    private void init()
    {
        states = orderStatesRepo.findAll();
    }

    private OrderState findByName (String state)
    {
        //OrderState orderState = null;
        state = Factory.validateString (state, 1, ORDERSTATE_STATE_LEN);
        if (state == null)
            throw new ???;

        for (OrderState os : states)
        {
            if (os.getState().equals (state))
                return os;
        }
        return null;
    }*/

    public OrderState getOrderStateByShortName (String shortName)
    {
        return orderStatesRepo.findByShortName (shortName);
    }

/*    public OrderState getOrderStatePending ()
    {
        return findByName (ORDERSTATE_PENDING);
    }

    public OrderState getOrderStateServing ()
    {
        return findByName (ORDERSTATE_SERVING);
    }

    public OrderState getOrderStatePayed ()
    {
        return findByName (ORDERSTATE_PAYED);
    }

    public OrderState getOrderStateCanceled ()
    {
        return findByName (ORDERSTATE_CANCELED);
    }*/
}
