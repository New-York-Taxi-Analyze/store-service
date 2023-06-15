package com.newyorktaxi.storeservice;

import com.newyorktaxi.avro.model.TaxiMessage;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestData {

    public TaxiMessage buildTaxiMessage() {
        return TaxiMessage.newBuilder()
                .setVendorId(1)
                .setTpepPickupDatetime("2023-11-04T12:34:56Z")
                .setTpepDropoffDatetime("2023-11-05T12:34:56Z")
                .setPassengerCount(3)
                .setTripDistance(4.0f)
                .setRateCodeId(1)
                .setStoreAndFwdFlag("Y")
                .setPuLocationId(5)
                .setDoLocationId(6)
                .setPaymentType(1)
                .setFareAmount(7.0)
                .setExtra(8.0)
                .setMtaTax(9.0)
                .setTipAmount(10.0)
                .setTollsAmount(11.0)
                .setImprovementSurcharge(12.0)
                .setTotalAmount(13.0)
                .build();
    }
}
