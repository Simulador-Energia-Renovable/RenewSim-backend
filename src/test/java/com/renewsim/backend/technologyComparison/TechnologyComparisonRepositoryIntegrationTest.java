package com.renewsim.backend.technologyComparison;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Integration tests for TechnologyComparisonRepository")
public class TechnologyComparisonRepositoryIntegrationTest {

    @Autowired
    private TechnologyComparisonRepository repository;

    private TechnologyComparison solarTech;
    private TechnologyComparison windTech;

    @BeforeEach
    void setUp() {
        solarTech = new TechnologyComparison(
                "Solar",
                85.0,
                1200.0,
                70.0,
                "Clean",
                50.0,
                320.0,
                "Solar"
        );

        windTech = new TechnologyComparison(
                "Wind",
                75.0,
                2000.0,
                90.0,
                "Moderate",
                60.0,
                400.0,
                "Wind"
        );
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should return true when technology with given name exists")
    void testShouldReturnTrueWhenTechnologyExists() {
        repository.save(solarTech);

        boolean exists = repository.existsByTechnologyName("Solar");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when technology with given name does not exist")
    void testShouldReturnFalseWhenTechnologyDoesNotExist() {
        boolean exists = repository.existsByTechnologyName("Unknown");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return list of technologies filtered by energy type")
    void testShouldFindByEnergyType() {
        repository.saveAll(List.of(solarTech, windTech));

        List<TechnologyComparison> solarResults = repository.findByEnergyType("Solar");

        assertThat(solarResults)
                .isNotEmpty()
                .hasSize(1)
                .extracting("technologyName")
                .contains("Solar")
                .doesNotContain("Wind");
    }
}
