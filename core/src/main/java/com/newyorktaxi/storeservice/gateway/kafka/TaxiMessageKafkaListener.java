package com.newyorktaxi.storeservice.gateway.kafka;

import com.newyorktaxi.avro.model.TaxiMessage;
import com.newyorktaxi.storeservice.mapper.TaxiTripMapper;
import com.newyorktaxi.storeservice.usecase.FunctionalUseCase;
import com.newyorktaxi.storeservice.usecase.params.TaxiTripParams;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
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
    public void onMessage(TaxiMessage taxiMessage) {
        log.info("Received record: {}", taxiMessage);

        final TaxiTripParams taxiTrip = taxiTripMapper.toTaxiTripParams(taxiMessage);
        saveTaxiTripUseCase.execute(taxiTrip);

        log.info("Successfully saved {} to database", taxiTrip);
    }

    @DltHandler
    public void dltHandler(TaxiMessage taxiMessage, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received topic {} with record from DLT: {}", topic, taxiMessage);
    }
}
