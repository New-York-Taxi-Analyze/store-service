package com.newyorktaxi.storeservice.mapper;

import com.newyorktaxi.avro.model.TaxiMessage;
import com.newyorktaxi.storeservice.entity.FailureMessage;
import com.newyorktaxi.storeservice.usecase.params.FailureMessageParams;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface FailureMessageMapper {

    @Mapping(target = "id", ignore = true)
    FailureMessage toFailureMessage(FailureMessageParams params);

    @Mapping(target = "topic", expression = "java(record.topic())")
    @Mapping(target = "errorKey", expression = "java(java.util.UUID.fromString(record.key()))")
    @Mapping(target = "errorData", expression = "java(record.value().toString())")
    @Mapping(target = "partition", expression = "java(record.partition())")
    @Mapping(target = "offsetValue", expression = "java(record.offset())")
    @Mapping(target = "exception", source = "exceptionMessage")
    @Mapping(target = "status", constant = "RETRY")
    FailureMessageParams toFailureMessageParams(ConsumerRecord<String, TaxiMessage> record, String exceptionMessage);
}
