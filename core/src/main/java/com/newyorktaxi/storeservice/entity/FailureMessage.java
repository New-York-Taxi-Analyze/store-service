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

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "failure_message")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FailureMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "topic", nullable = false)
    String topic;

    @Column(name = "error_key", nullable = false)
    UUID errorKey;

    @Column(name = "error_data", nullable = false)
    String errorData;

    @Column(name = "partition", nullable = false)
    Integer partition;

    @Column(name = "offset_value", nullable = false)
    Long offsetValue;

    @Column(name = "exception", nullable = false)
    String exception;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    StatusEnum status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FailureMessage that = (FailureMessage) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
