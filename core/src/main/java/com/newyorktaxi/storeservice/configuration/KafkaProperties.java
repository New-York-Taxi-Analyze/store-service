package com.newyorktaxi.storeservice.configuration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaProperties {
    String bootstrapServers;
    String schemaRegistryUrlKey;
    String schemaRegistryUrl;
    Integer numOfPartitions;
    Short replicationFactor;
}
