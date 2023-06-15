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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaxiMessageKafkaListener implements AcknowledgingMessageListener<String, Object> {

    FunctionalUseCase<TaxiTripParams, Void> saveTaxiTripUseCase;
    TaxiTripMapper taxiTripMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.taxi-message-group-id}",
            topics = "${kafka-consumer-config.taxi-message-topic}")
    public void onMessage(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment) {
        log.info("Received record: {}", record);
        if (record.value() instanceof TaxiMessage taxiMessage) {
            log.info("Processing {} messages from Kafka", taxiMessage);

            final TaxiTripParams taxiTrip = taxiTripMapper.toTaxiTripParams(taxiMessage);
            saveTaxiTripUseCase.execute(taxiTrip);

            log.info("Successfully saved {} to database", taxiTrip);
            acknowledgment.acknowledge();
        } else {
            log.error("Received unknown message type: {}", record.value());
        }
    }
}
