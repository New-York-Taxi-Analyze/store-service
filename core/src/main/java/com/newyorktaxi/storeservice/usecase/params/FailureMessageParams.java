package com.newyorktaxi.storeservice.usecase.params;

import com.newyorktaxi.storeservice.entity.StatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FailureMessageParams {

    String topic;

    UUID errorKey;

    String errorData;

    Integer partition;

    Long offsetValue;

    String exception;

    StatusEnum status;
}
