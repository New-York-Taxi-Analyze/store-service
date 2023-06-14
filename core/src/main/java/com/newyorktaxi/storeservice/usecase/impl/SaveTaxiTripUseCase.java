package com.newyorktaxi.storeservice.usecase.impl;

import com.newyorktaxi.storeservice.entity.TaxiTrip;
import com.newyorktaxi.storeservice.mapper.TaxiTripMapper;
import com.newyorktaxi.storeservice.repository.TaxiTripRepository;
import com.newyorktaxi.storeservice.usecase.FunctionalUseCase;
import com.newyorktaxi.storeservice.usecase.params.TaxiTripParams;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaveTaxiTripUseCase implements FunctionalUseCase<TaxiTripParams, Void> {

    TaxiTripRepository taxiTripRepository;
    TaxiTripMapper taxiTripMapper;

    @Override
    public Void execute(TaxiTripParams params) {
        final TaxiTrip taxiTrip = taxiTripMapper.toTaxiTrip(params);
        taxiTripRepository.save(taxiTrip);
        return null;
    }
}
