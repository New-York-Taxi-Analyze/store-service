package com.newyorktaxi.storeservice.scheduler;

import com.newyorktaxi.storeservice.StoreServiceApplication;
import com.newyorktaxi.storeservice.TestData;
import com.newyorktaxi.storeservice.entity.FailureMessage;
import com.newyorktaxi.storeservice.entity.StatusEnum;
import com.newyorktaxi.storeservice.repository.FailureMessageRepository;
import com.newyorktaxi.storeservice.repository.TaxiTripRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = StoreServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FieldDefaults(level = AccessLevel.PRIVATE)
@TestPropertySource(properties = "scheduler.retry-failed-message.cron-expression=*/1 * * * * *")
class RetryFailedMessageTest {

    @SpyBean
    RetryFailedMessage retryFailedMessage;

    @Autowired
    FailureMessageRepository failureMessageRepository;

    @Autowired
    TaxiTripRepository taxiTripRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        final List<FailureMessage> failureMessages = List.of(
                TestData.buildFailureMessage(),
                TestData.buildFailureMessage()
        );

        failureMessageRepository.saveAll(failureMessages);
    }

    @AfterEach
    void tearDown() {
        failureMessageRepository.deleteAll();
        taxiTripRepository.deleteAll();
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "shedlock");
    }

    @Test
    void testRetry() {
        await().atMost(5, TimeUnit.SECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> assertThat(failureMessageRepository.findAllByStatus(StatusEnum.SUCCESS)).hasSize(2));

        verify(retryFailedMessage).retry();
    }
}
