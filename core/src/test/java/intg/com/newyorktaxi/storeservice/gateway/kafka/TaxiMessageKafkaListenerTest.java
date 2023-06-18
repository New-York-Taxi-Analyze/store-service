package com.newyorktaxi.storeservice.gateway.kafka;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.newyorktaxi.avro.model.TaxiMessage;
import com.newyorktaxi.storeservice.StoreServiceApplication;
import com.newyorktaxi.storeservice.TestData;
import com.newyorktaxi.storeservice.TestUtil;
import com.newyorktaxi.storeservice.repository.TaxiTripRepository;
import com.newyorktaxi.storeservice.usecase.impl.SaveTaxiTripUseCase;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {StoreServiceApplication.class, TaxiMessageKafkaListener.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@EmbeddedKafka(topics = "taxi-messages", partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.properties.schema.registry.url=http://localhost:${wiremock.server.port}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=io.confluent.kafka.serializers.KafkaAvroSerializer",
        "spring.kafka.producer.properties.schema.registry.url=http://localhost:${wiremock.server.port}",
        "kafka-consumer-config.taxi-message-retry-attempts=1"

})
@FieldDefaults(level = AccessLevel.PRIVATE)
class TaxiMessageKafkaListenerTest {

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate<String, TaxiMessage> kafkaTemplate;

    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;

    @SpyBean
    TaxiMessageKafkaListener taxiMessageKafkaListener;

    @SpyBean
    SaveTaxiTripUseCase saveTaxiTripUseCase;

    @Autowired
    TaxiTripRepository taxiTripRepository;

    @BeforeEach
    void setUp(@Value("${kafka-consumer-config.taxi-message-topic}") String topic) {
        endpointRegistry.getListenerContainers()
                .forEach(container ->
                        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic()));

        WireMock.reset();
        WireMock.resetAllRequests();
        WireMock.resetAllScenarios();
        WireMock.resetToDefault();

        TestUtil.registerSchema(1, topic, TaxiMessage.getClassSchema().toString());
        TestUtil.registerSchema(2, topic + "-dlt", TaxiMessage.getClassSchema().toString());
    }

    @AfterEach
    void tearDown() {
        taxiTripRepository.deleteAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @DisplayName("test on message")
    void testOnMessage() {
        final TaxiMessage taxiMessage = TestData.buildTaxiMessage();
        kafkaTemplate.sendDefault(taxiMessage).get();

        await().atMost(5, TimeUnit.SECONDS)
                .pollDelay(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> assertThat(taxiTripRepository.count()).isEqualTo(1));
        verify(taxiMessageKafkaListener).onMessage(any(ConsumerRecord.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @DisplayName("test dead letter topic")
    void testDeadLetterTopic() {
        final TaxiMessage taxiMessage = TestData.buildTaxiMessage();
        final ArgumentCaptor<ConsumerRecord<String, TaxiMessage>> recordCaptor = ArgumentCaptor.forClass(ConsumerRecord.class);
        final CountDownLatch latch = new CountDownLatch(1);

        doThrow(new RuntimeException("Test exception")).when(saveTaxiTripUseCase).execute(any());
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(taxiMessageKafkaListener).dltHandler(recordCaptor.capture());

        kafkaTemplate.sendDefault(taxiMessage).get();

        boolean await = latch.await(5, TimeUnit.SECONDS);

        assertThat(await).isTrue();

        verify(taxiMessageKafkaListener).onMessage(any(ConsumerRecord.class));
        verify(taxiMessageKafkaListener).dltHandler(any(ConsumerRecord.class));

        final ConsumerRecord<String, TaxiMessage> capturedRecord = recordCaptor.getValue();
        assertThat(capturedRecord.value()).isEqualTo(taxiMessage);
    }
}
