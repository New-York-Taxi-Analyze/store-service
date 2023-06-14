package com.newyorktaxi.storeservice.gateway.kafka;

import com.newyorktaxi.avro.model.TaxiMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaxiMessageKafkaListener implements AcknowledgingMessageListener<String, Object> {

    @Override
    @KafkaListener(id = "${kafka-consumer-config.taxi-message-group-id}",
            topics = "${kafka-consumer-config.taxi-message-topic}")
    public void onMessage(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment) {
        if(record.value() instanceof TaxiMessage taxiMessage) {
            log.info("Received {} messages from Kafka", taxiMessage);
        }

        acknowledgment.acknowledge();
    }
}
