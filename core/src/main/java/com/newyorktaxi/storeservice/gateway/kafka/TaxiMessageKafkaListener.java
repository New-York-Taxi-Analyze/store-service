package com.newyorktaxi.storeservice.gateway.kafka;

import com.newyorktaxi.avro.model.TaxiMessage;
import com.newyorktaxi.storeservice.mapper.TaxiTripMapper;
import com.newyorktaxi.storeservice.usecase.FunctionalUseCase;
import com.newyorktaxi.storeservice.usecase.params.TaxiTripParams;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaxiMessageKafkaListener {

    FunctionalUseCase<TaxiTripParams, Void> saveTaxiTripUseCase;
    TaxiTripMapper taxiTripMapper;

    @RetryableTopic(attempts = "${kafka-consumer-config.taxi-message-retry-attempts}",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            backoff = @Backoff(delayExpression = "${kafka-consumer-config.taxi-message-retry-delay}",
                    multiplierExpression = "${kafka-consumer-config.taxi-message-retry-multiplier}"))
    @KafkaListener(id = "${kafka-consumer-config.taxi-message-group-id}",
            topics = "${kafka-consumer-config.taxi-message-topic}")
    public void onMessage(ConsumerRecord<String, TaxiMessage> record) {
        log.info("Received record: {}", record);

        final TaxiTripParams taxiTrip = taxiTripMapper.toTaxiTripParams(record.value());
        saveTaxiTripUseCase.execute(taxiTrip);

        log.info("Successfully saved {} to database", taxiTrip);
    }

    @DltHandler
    public void dltHandler(ConsumerRecord<String, TaxiMessage> record) {
        log.info("Received record from DLT: {}", record);
    }
}
