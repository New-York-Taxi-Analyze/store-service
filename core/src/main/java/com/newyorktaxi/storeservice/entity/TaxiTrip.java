package com.newyorktaxi.storeservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "TaxiTrip")
@Table(name = "taxi_trip")
public class TaxiTrip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator="native")
    @Column(name = "id")
    Integer id;

    @Column(name = "vendor_id")
    Integer vendorId;

    @Column(name = "tpep_pickup_datetime")
    Instant tpepPickupDatetime;

    @Column(name = "tpep_dropoff_datetime")
    Instant tpepDropoffDatetime;

    @Column(name = "passenger_count")
    Integer passengerCount;

    @Column(name = "trip_distance")
    BigDecimal tripDistance;

    @Enumerated
    @Column(name = "rate_code_id")
    RateCodeType rateCodeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "store_and_fwd_flag")
    StoreAndFwdFlagType storeAndFwdFlag;

    @Column(name = "pu_location_id")
    Integer puLocationId;

    @Column(name = "do_location_id")
    Integer doLocationId;

    @Enumerated
    @Column(name = "payment_type")
    PaymentType paymentType;

    @Column(name = "fare_amount")
    BigDecimal fareAmount;

    @Column(name = "extra")
    BigDecimal extra;

    @Column(name = "mta_tax")
    BigDecimal mtaTax;

    @Column(name = "tip_amount")
    BigDecimal tipAmount;

    @Column(name = "tolls_amount")
    BigDecimal tollsAmount;

    @Column(name = "improvement_surcharge")
    BigDecimal improvementSurcharge;

    @Column(name = "total_amount")
    BigDecimal totalAmount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TaxiTrip taxiTrip = (TaxiTrip) o;
        return getId() != null && Objects.equals(getId(), taxiTrip.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
