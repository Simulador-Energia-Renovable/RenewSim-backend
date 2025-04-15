package com.renewsim.backend.technologyComparison;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Integration test for TechnologyComparisonServiceImpl with real database")
public class TechnologyComparisonServiceIntegrationTest {

    @Autowired
    private TechnologyComparisonRepository repository;

    private TechnologyComparisonServiceImpl service;

    private TechnologyComparison solar;

    @BeforeEach
    void setUp() {
        service = new TechnologyComparisonServiceImpl(repository);

        solar = new TechnologyComparison(
                "Solar",
                85.0,
                1200.0,
                70.0,
                "Clean",
                50.0,
                320.0,
                "Solar");
    }

    @Test
    @DisplayName("Should persist and retrieve technology")
    void testShouldPersistAndRetrieve() {
        service.addTechnology(solar);
        List<TechnologyComparison> all = service.getAllTechnologies();

        assertThat(all).hasSize(1).extracting("technologyName").contains("Solar");
    }

    @Test
    @DisplayName("Should find by ID from real DB")
    void testShouldFindById() {
        TechnologyComparison saved = service.addTechnology(solar);

        Optional<TechnologyComparison> found = service.getTechnologyById(saved.getId());

        assertThat(found).isPresent().containsSame(saved);
    }

    @Test
    @DisplayName("Should delete technology and confirm it's gone")
    void testShouldDelete() {
        TechnologyComparison saved = service.addTechnology(solar);
        service.deleteTechnology(saved.getId());

        assertThat(service.getTechnologyById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should filter by energy type with real DB")
    void testShouldFilterByType() {
        TechnologyComparison wind = new TechnologyComparison(
                "Wind",
                75.0,
                2000.0,
                90.0,
                "Moderate",
                60.0,
                400.0,
                "Wind");

        service.addTechnology(solar);
        service.addTechnology(wind);

        List<TechnologyComparison> results = service.getTechnologiesByEnergyType("Solar");

        assertThat(results).hasSize(1).extracting("technologyName").contains("Solar");
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }
}
