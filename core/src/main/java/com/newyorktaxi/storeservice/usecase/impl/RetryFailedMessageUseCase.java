package com.newyorktaxi.storeservice.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newyorktaxi.avro.model.TaxiMessage;
import com.newyorktaxi.storeservice.entity.FailureMessage;
import com.newyorktaxi.storeservice.entity.StatusEnum;
import com.newyorktaxi.storeservice.mapper.TaxiTripMapper;
import com.newyorktaxi.storeservice.repository.FailureMessageRepository;
import com.newyorktaxi.storeservice.usecase.FunctionalUseCase;
import com.newyorktaxi.storeservice.usecase.params.TaxiTripParams;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RetryFailedMessageUseCase implements FunctionalUseCase<Void, Void> {

    FunctionalUseCase<TaxiTripParams, Void> saveTaxiTripUseCase;
    FailureMessageRepository repository;
    TaxiTripMapper taxiTripMapper;
    ObjectMapper objectMapper;

    @Override
    public Void execute(Void params) {
        final List<FailureMessage> allByStatus = repository.findAllByStatus(StatusEnum.RETRY);

        if (!allByStatus.isEmpty()) {
            log.info("Retrying {} messages", allByStatus.size());
        }

        allByStatus.forEach(failureMessage -> {
            try {
                final ConsumerRecord<String, TaxiMessage> consumerRecord = buildConsumerRecord(failureMessage);
                final TaxiTripParams taxiTripParams = taxiTripMapper.toTaxiTripParams(consumerRecord.value());

                saveTaxiTripUseCase.execute(taxiTripParams);

                failureMessage.setStatus(StatusEnum.SUCCESS);
                repository.save(failureMessage);

                log.info("Message successfully retried: {}", failureMessage);
            } catch (JsonProcessingException e) {
                log.error("Error while parsing message: {}", e.getMessage());
            }
        });

        return null;
    }

    private ConsumerRecord<String, TaxiMessage> buildConsumerRecord(FailureMessage failureMessage)
            throws JsonProcessingException {
        return new ConsumerRecord<>(
                failureMessage.getTopic(),
                failureMessage.getPartition(),
                failureMessage.getOffsetValue(),
                failureMessage.getErrorKey().toString(),
                objectMapper.readValue(failureMessage.getErrorData(), TaxiMessage.class));
    }
}
