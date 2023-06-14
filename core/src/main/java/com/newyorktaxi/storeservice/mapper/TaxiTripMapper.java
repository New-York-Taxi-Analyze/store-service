package com.newyorktaxi.storeservice.mapper;

import com.newyorktaxi.avro.model.TaxiMessage;
import com.newyorktaxi.storeservice.entity.PaymentType;
import com.newyorktaxi.storeservice.entity.RateCodeType;
import com.newyorktaxi.storeservice.entity.TaxiTrip;
import com.newyorktaxi.storeservice.usecase.params.TaxiTripParams;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TaxiTripMapper {

    TaxiTripParams toTaxiTripParams(TaxiMessage taxiMessage);

    @Mapping(target = "id", ignore = true)
    TaxiTrip toTaxiTrip(TaxiTripParams taxiTripParams);

    default RateCodeType mapRateCodeType(Integer rateCodeId) {
        return RateCodeType.fromValue(rateCodeId);
    }

    default PaymentType mapPaymentType(Integer paymentType) {
        return PaymentType.fromValue(paymentType);
    }

    default com.newyorktaxi.storeservice.model.PaymentType mapPaymentType(PaymentType paymentType) {
        return com.newyorktaxi.storeservice.model.PaymentType.fromValue(paymentType.getValue());
    }

    default  com.newyorktaxi.storeservice.model.RateCodeType mapRateCodeType(RateCodeType rateCodeType) {
        return com.newyorktaxi.storeservice.model.RateCodeType.fromValue(rateCodeType.getValue());
    }
}
