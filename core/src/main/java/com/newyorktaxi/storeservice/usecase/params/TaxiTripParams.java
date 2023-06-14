package com.newyorktaxi.storeservice.usecase.params;

import com.newyorktaxi.storeservice.model.PaymentType;
import com.newyorktaxi.storeservice.model.RateCodeType;
import com.newyorktaxi.storeservice.model.StoreAndFwdFlagType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TaxiTripParams {

    Integer vendorId;
    Instant tpepPickupDatetime;
    Instant tpepDropoffDatetime;
    Integer passengerCount;
    BigDecimal tripDistance;
    RateCodeType rateCodeId;
    StoreAndFwdFlagType storeAndFwdFlag;
    Integer puLocationId;
    Integer doLocationId;
    PaymentType paymentType;
    BigDecimal fareAmount;
    BigDecimal extra;
    BigDecimal mtaTax;
    BigDecimal tipAmount;
    BigDecimal tollsAmount;
    BigDecimal improvementSurcharge;
    BigDecimal totalAmount;
}
