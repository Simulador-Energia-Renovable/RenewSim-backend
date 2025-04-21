package com.renewsim.backend.simulation.util;
import com.renewsim.backend.simulation.dto.NormalizationStatsDTO;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TechnologyScoringUtilTest {

    @Test
    @DisplayName("Should calculate normalization stats correctly for given tech list")
    void shouldCalculateNormalizationStats() {
        List<TechnologyComparisonResponseDTO> techList = List.of(
                TechnologyComparisonResponseDTO.builder()
                        .co2Reduction((double) 100)
                        .energyProduction((double) 2000)
                        .installationCost((double) 15000)
                        .efficiency(0.2)
                        .build(),
                TechnologyComparisonResponseDTO.builder()
                        .co2Reduction((double) 200)
                        .energyProduction((double) 3000)
                        .installationCost((double) 10000)
                        .efficiency(0.3)
                        .build()
        );

        NormalizationStatsDTO stats = TechnologyScoringUtil.calculateNormalizationStats(techList);

        assertThat(stats.getMinCo2()).isEqualTo(100);
        assertThat(stats.getMaxCo2()).isEqualTo(200);
        assertThat(stats.getMinEnergy()).isEqualTo(2000);
        assertThat(stats.getMaxEnergy()).isEqualTo(3000);
        assertThat(stats.getMinCost()).isEqualTo(10000);
        assertThat(stats.getMaxCost()).isEqualTo(15000);
        assertThat(stats.getMinEfficiency()).isEqualTo(0.2);
        assertThat(stats.getMaxEfficiency()).isEqualTo(0.3);
    }

    @Test
    @DisplayName("Should normalize values correctly within range")
    void shouldNormalizeValueCorrectly() {
        double result = TechnologyScoringUtil.normalize(150, 100, 200);
        assertThat(result).isEqualTo(0.5);
    }

    @Test
    @DisplayName("Should return 0.0 when min equals max in normalization")
    void shouldReturnZeroWhenMinEqualsMax() {
        double result = TechnologyScoringUtil.normalize(100, 100, 100);
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should calculate score correctly for a given technology")
    void shouldCalculateScoreCorrectly() {
        TechnologyComparisonResponseDTO tech = TechnologyComparisonResponseDTO.builder()
                .co2Reduction((double) 150)
                .energyProduction((double) 2500)
                .installationCost((double) 12000)
                .efficiency(0.25)
                .build();

        NormalizationStatsDTO stats = NormalizationStatsDTO.builder()
                .minCo2(100).maxCo2(200)
                .minEnergy(2000).maxEnergy(3000)
                .minCost(10000).maxCost(15000)
                .minEfficiency(0.2).maxEfficiency(0.3)
                .build();

        double score = TechnologyScoringUtil.calculateScoreDynamic(tech, stats);
        assertThat(score).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should handle empty list in normalization stats gracefully")
    void shouldHandleEmptyListForNormalizationStats() {
        NormalizationStatsDTO stats = TechnologyScoringUtil.calculateNormalizationStats(List.of());
        assertThat(stats).isNotNull();
        assertThat(stats.getMaxCo2()).isEqualTo(1.0);
        assertThat(stats.getMinCo2()).isEqualTo(0.0);
    }
}

