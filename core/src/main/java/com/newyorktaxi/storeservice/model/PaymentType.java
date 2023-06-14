package com.newyorktaxi.storeservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public enum PaymentType {
    CASH(1),
    CREDIT_CARD(2),
    NO_CHARGE(3),
    DISPUTE(4),
    UNKNOWN(5),
    VOIDED_TRIP(6);

    int value;

    public static PaymentType fromValue(int value) {
        for (PaymentType paymentType : PaymentType.values()) {
            if (paymentType.value == value) {
                return paymentType;
            }
        }
        return null;
    }
}
