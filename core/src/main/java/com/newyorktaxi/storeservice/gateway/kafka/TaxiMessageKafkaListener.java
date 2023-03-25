package com.newyorktaxi.storeservice.gateway.kafka;
import com.newyorktaxi.avro.model.TaxiMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TaxiMessageKafkaListener implements KafkaConsumer<TaxiMessage> {

    @Override
    @KafkaListener(id = "${kafka-consumer-config.taxi-message-group-id}",
            topics = "${kafka-consumer-config.taxi-message-topic}")
    public void receive(List<TaxiMessage> messages, List<String> keys, List<Integer> partitions, List<Long> offsets) {
        log.info("Received {} messages from Kafka", messages.size());

        for (int i = 0; i < messages.size(); i++) {
            log.info("Received message: {} with key: {} from partition: {} at offset: {}",
                    messages.get(i), keys.get(i), partitions.get(i), offsets.get(i));
        }
    }
}
