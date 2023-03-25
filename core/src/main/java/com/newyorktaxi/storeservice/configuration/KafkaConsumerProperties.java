package com.newyorktaxi.storeservice.configuration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-consumer-config")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaConsumerProperties {
    String keyDeserializer;
    String valueDeserializer;
    String autoOffsetReset;
    String specificAvroReaderKey;
    String specificAvroReader;
    Boolean batchListener;
    Boolean autoStartup;
    Integer concurrencyLevel;
    Integer sessionTimeoutMs;
    Integer heartbeatIntervalMs;
    Integer maxPollIntervalMs;
    Long pollTimeoutMs;
    Integer maxPollRecords;
    Integer maxPartitionFetchBytesDefault;
    Integer maxPartitionFetchBytesBoostFactor;
}
