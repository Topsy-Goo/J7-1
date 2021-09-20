package ru.gb.antonov.j71.beans.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.antonov.j71.beans.repositos.OrderStatesRepo;
import ru.gb.antonov.j71.entities.OrderState;

import static ru.gb.antonov.j71.Factory.*;

@Service
@RequiredArgsConstructor
public class OrderStatesService
{
    private final OrderStatesRepo orderStatesRepo;

    public OrderState getStateNone ()    { return orderStatesRepo.findByShortName (ORDERSTATE_NONE); }
    public OrderState getStatePending () { return orderStatesRepo.findByShortName (ORDERSTATE_PENDING); }
    public OrderState getOrderStateServing ()  { return orderStatesRepo.findByShortName (ORDERSTATE_SERVING); }
    public OrderState getOrderStatePayed ()    { return orderStatesRepo.findByShortName (ORDERSTATE_PAYED); }
    public OrderState getOrderStateCanceled () { return orderStatesRepo.findByShortName (ORDERSTATE_CANCELED); }
}
