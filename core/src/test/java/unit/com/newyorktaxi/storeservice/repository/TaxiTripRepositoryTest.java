package com.newyorktaxi.storeservice.repository;

import com.newyorktaxi.storeservice.ClearDatabaseExtension;
import com.newyorktaxi.storeservice.entity.TaxiTrip;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.flyway.clean-disabled=false",
        "spring.flyway.locations=classpath:db/migration"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(ClearDatabaseExtension.class)
class TaxiTripRepositoryTest {

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
    TestEntityManager entityManager;

    @Autowired
    TaxiTripRepository repository;

    @Test
    @DisplayName("Should successfully find taxi trip by id")
    void testFindById() {
        final TaxiTrip expected = getTaxiTrip();
        entityManager.persist(expected);

        final TaxiTrip actual = repository.findById(expected.getId()).orElseThrow();

        assertThat(actual)
                .as("actual does not match expected")
                .isEqualTo(expected);
    }

    private static TaxiTrip getTaxiTrip() {
        return TaxiTrip.builder()
                .vendorId(1)
                .passengerCount(1)
                .tripDistance(BigDecimal.ONE)
                .fareAmount(BigDecimal.TEN)
                .build();
    }
}
