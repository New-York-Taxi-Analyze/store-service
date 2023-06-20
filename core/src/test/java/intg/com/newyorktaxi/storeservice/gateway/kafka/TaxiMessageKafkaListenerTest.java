package com.newyorktaxi.storeservice.gateway.kafka;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.newyorktaxi.avro.model.TaxiMessage;
import com.newyorktaxi.storeservice.StoreServiceApplication;
import com.newyorktaxi.storeservice.TestData;
import com.newyorktaxi.storeservice.TestUtil;
import com.newyorktaxi.storeservice.mapper.TaxiTripMapper;
import com.newyorktaxi.storeservice.repository.FailureMessageRepository;
import com.newyorktaxi.storeservice.repository.TaxiTripRepository;
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
import static org.junit.jupiter.api.Assertions.assertAll;
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

    @Value("${kafka-consumer-config.taxi-message-topic}")
    String topic;
    @Value("${kafka-consumer-config.taxi-message-topic-dlt}")
    String topicDlt;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate<String, TaxiMessage> kafkaTemplate;

    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;

    @SpyBean
    TaxiMessageKafkaListener taxiMessageKafkaListener;

    @SpyBean
    TaxiTripMapper taxiTripMapper;

    @Autowired
    TaxiTripRepository taxiTripRepository;

    @Autowired
    FailureMessageRepository failureMessageRepository;

    @BeforeEach
    void setUp() {
        endpointRegistry.getListenerContainers()
                .forEach(container ->
                        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic()));

        WireMock.reset();
        WireMock.resetAllRequests();
        WireMock.resetAllScenarios();
        WireMock.resetToDefault();

        TestUtil.registerSchema(1, topic, TaxiMessage.getClassSchema().toString());
        TestUtil.registerSchema(2, topicDlt, TaxiMessage.getClassSchema().toString());
    }

    @AfterEach
    void tearDown() {
        taxiTripRepository.deleteAll();
        failureMessageRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    @DisplayName("Test on message")
    void testOnMessage() {
        final TaxiMessage taxiMessage = TestData.buildTaxiMessage();
        kafkaTemplate.sendDefault(taxiMessage).get();

        await().atMost(5, TimeUnit.SECONDS)
                .pollDelay(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> assertThat(taxiTripRepository.count()).isEqualTo(1));
        verify(taxiMessageKafkaListener).onMessage(taxiMessage);
    }

    @Test
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @DisplayName("Successful message processing")
    void testDeadLetterTopic() {
        final String expectedExceptionMessage = "Listener failed; Test exception";
        final TaxiMessage expectedTaxiMessage = TestData.buildTaxiMessage();
        final ArgumentCaptor<ConsumerRecord<String, TaxiMessage>> recordCaptor = ArgumentCaptor.forClass(ConsumerRecord.class);
        final ArgumentCaptor<String> exceptionCaptor = ArgumentCaptor.forClass(String.class);
        final CountDownLatch latch = new CountDownLatch(1);

        doThrow(new RuntimeException("Test exception")).when(taxiTripMapper).toTaxiTripParams(expectedTaxiMessage);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(taxiMessageKafkaListener).dltHandler(recordCaptor.capture(), exceptionCaptor.capture());

        kafkaTemplate.sendDefault(expectedTaxiMessage).get();

        boolean await = latch.await(5, TimeUnit.SECONDS);

        assertThat(await)
                .as("awaiting for latch")
                .isTrue();

        final ConsumerRecord<String, TaxiMessage> actualTaxiMessage = recordCaptor.getValue();
        final String actualExceptionMessage = exceptionCaptor.getValue();

        assertAll(
                () -> assertThat(actualTaxiMessage.value())
                        .as("expected taxi message")
                        .isEqualTo(expectedTaxiMessage),
                () -> assertThat(actualExceptionMessage)
                        .as("expected exception message")
                        .contains(expectedExceptionMessage)
        );

        verify(taxiMessageKafkaListener).onMessage(expectedTaxiMessage);
        verify(taxiMessageKafkaListener).dltHandler(actualTaxiMessage, "Listener failed; Test exception");
    }

    @Test
    @SneakyThrows
    @DisplayName("Successful message processing with real data")
    void testDeadLetterTopicWithRealData() {
        final TaxiMessage expectedTaxiMessage = TestData.buildTaxiMessage();

        doThrow(new RuntimeException("Test exception"))
                .when(taxiTripMapper)
                .toTaxiTripParams(expectedTaxiMessage);

        kafkaTemplate.sendDefault(TestData.TOPIC_KEY, expectedTaxiMessage).get();

        await().atMost(5, TimeUnit.SECONDS)
                .pollDelay(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> assertThat(failureMessageRepository.count()).isEqualTo(1));
    }
}
