package com.newyorktaxi.storeservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "TaxiTrip")
public class TaxiTrip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator="native")
    Integer id;
    Integer vendorId;
    Instant tpepPickupDatetime;
    Instant tpepDropoffDatetime;
    Integer passengerCount;
    BigDecimal tripDistance;
    @Enumerated
    RateCodeType rateCodeId;
    @Enumerated(EnumType.STRING)
    StoreAndFwdFlagType storeAndFwdFlag;
    Integer puLocationId;
    Integer doLocationId;
    @Enumerated
    PaymentType paymentType;
    BigDecimal fareAmount;
    BigDecimal extra;
    BigDecimal mtaTax;
    BigDecimal tipAmount;
    BigDecimal tollsAmount;
    BigDecimal improvementSurcharge;
    BigDecimal totalAmount;
}
