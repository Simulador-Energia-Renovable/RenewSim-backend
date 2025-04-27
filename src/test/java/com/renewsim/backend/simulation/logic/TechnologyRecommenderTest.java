package com.renewsim.backend.simulation.logic;

import com.renewsim.backend.simulation.dto.NormalizationStatsDTO;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TechnologyRecommender Unit Tests")
class TechnologyRecommenderTest {

    private TechnologyRecommender recommender;
    private NormalizationStatsDTO stats;

    @BeforeEach
    void setUp() {
        recommender = new TechnologyRecommender();

        stats = NormalizationStatsDTO.builder()
                .minCo2(0)
                .maxCo2(10)
                .minEnergy(1000)
                .maxEnergy(5000)
                .minCost(200)
                .maxCost(2000)
                .minEfficiency(0.1)
                .maxEfficiency(1.0)
                .build();
    }

    private TechnologyComparisonResponseDTO createTech(String name, double efficiency, double co2, double energy, double cost) {
        TechnologyComparisonResponseDTO dto = new TechnologyComparisonResponseDTO();
        dto.setTechnologyName(name);
        dto.setEfficiency(efficiency);
        dto.setCo2Reduction(co2);
        dto.setEnergyProduction(energy);
        dto.setInstallationCost(cost);
        return dto;
    }

    @Test
    @DisplayName("Should recommend the technology with highest score")
    void testShouldRecommendBestTechnology() {
        var solar = createTech("Solar", 0.9, 9, 4800, 300);
        var wind = createTech("Wind", 0.8, 8, 4700, 400);
        var hydro = createTech("Hydro", 0.7, 7, 4600, 800);

        String recommendation = recommender.recommendTechnology(List.of(solar, wind, hydro), stats);

        assertThat(recommendation).isEqualTo("Solar"); 
    }

    @Test
    @DisplayName("Should return fallback message if list is empty")
    void testShouldReturnDefaultWhenEmpty() {
        String recommendation = recommender.recommendTechnology(List.of(), stats);
        assertThat(recommendation).isEqualTo("No recommendation available");
    }

    @Test
    @DisplayName("Should return one of the tied technologies if equal score")
    void testShouldReturnAnyWithSameScore() {
        var t1 = createTech("Tech1", 0.5, 5, 3000, 1000);
        var t2 = createTech("Tech2", 0.5, 5, 3000, 1000); 

        String result = recommender.recommendTechnology(List.of(t1, t2), stats);

        assertThat(List.of("Tech1", "Tech2")).contains(result);
    }
}

