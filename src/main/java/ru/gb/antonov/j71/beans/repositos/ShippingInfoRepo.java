package ru.gb.antonov.j71.beans.repositos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingInfoRepo extends CrudRepository <ShippingInfoRepo, Long> { }
