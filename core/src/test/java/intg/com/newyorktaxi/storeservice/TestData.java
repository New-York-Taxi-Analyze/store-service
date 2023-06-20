package com.newyorktaxi.storeservice;

import com.newyorktaxi.avro.model.TaxiMessage;
import com.newyorktaxi.storeservice.entity.FailureMessage;
import com.newyorktaxi.storeservice.entity.StatusEnum;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class TestData {

    public static final String TOPIC_KEY = "99be0f6a-4ea5-4c55-8630-4c7a3db76f1a";

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

    public FailureMessage buildFailureMessage() {
        return FailureMessage.builder()
                .topic("topic")
                .errorKey(UUID.randomUUID())
                .errorData(buildTaxiMessage().toString())
                .partition(1)
                .offsetValue(1L)
                .exception("exception")
                .status(StatusEnum.RETRY)
                .build();
    }
}
