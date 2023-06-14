package com.newyorktaxi.storeservice.repository;

import com.newyorktaxi.storeservice.entity.TaxiTrip;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxiTripRepository extends CrudRepository<TaxiTrip, Integer> {
}
