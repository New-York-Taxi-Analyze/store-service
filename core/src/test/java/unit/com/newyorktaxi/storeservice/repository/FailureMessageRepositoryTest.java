package com.newyorktaxi.storeservice.repository;

import com.newyorktaxi.storeservice.ClearDatabaseExtension;
import com.newyorktaxi.storeservice.entity.FailureMessage;
import com.newyorktaxi.storeservice.entity.StatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.flyway.clean-disabled=false",
        "spring.flyway.locations=classpath:db/migration"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(ClearDatabaseExtension.class)
class FailureMessageRepositoryTest {

    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.2")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    @DynamicPropertySource
    static void postgresqlProperties(final DynamicPropertyRegistry registry) {
        postgreSQLContainer.start();
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    FailureMessageRepository repository;

    @Test
    @DisplayName("Should successfully save failure message")
    void testSave() {
        final FailureMessage expected = getFailureMessage();

        final FailureMessage actual = repository.save(expected);

        assertThat(actual)
                .as("actual does not match expected")
                .isEqualTo(expected);
    }

    private static FailureMessage getFailureMessage() {
        return FailureMessage.builder()
                .topic("topic")
                .errorKey(UUID.randomUUID())
                .errorData("errorData")
                .partition(1)
                .offsetValue(1L)
                .exception("exception")
                .status(StatusEnum.RETRY)
                .build();
    }
}
