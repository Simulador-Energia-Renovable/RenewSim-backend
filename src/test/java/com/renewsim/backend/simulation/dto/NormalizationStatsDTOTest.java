package com.renewsim.backend.simulation.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NormalizationStatsDTO Unit Tests")
class NormalizationStatsDTOTest {

    private NormalizationStatsDTO stats;

    @BeforeEach
    void setUp() {
        stats = new NormalizationStatsDTO();
        stats.setMinCo2(1.0);
        stats.setMaxCo2(10.0);
        stats.setMinEnergy(100.0);
        stats.setMaxEnergy(1000.0);
        stats.setMinCost(500.0);
        stats.setMaxCost(1500.0);
        stats.setMinEfficiency(0.2);
        stats.setMaxEfficiency(0.9);
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void shouldSetAndGetFields() {
        assertThat(stats.getMinCo2()).isEqualTo(1.0);
        assertThat(stats.getMaxCo2()).isEqualTo(10.0);
        assertThat(stats.getMinEnergy()).isEqualTo(100.0);
        assertThat(stats.getMaxEnergy()).isEqualTo(1000.0);
        assertThat(stats.getMinCost()).isEqualTo(500.0);
        assertThat(stats.getMaxCost()).isEqualTo(1500.0);
        assertThat(stats.getMinEfficiency()).isEqualTo(0.2);
        assertThat(stats.getMaxEfficiency()).isEqualTo(0.9);
    }

    @Test
    @DisplayName("Should build object using builder")
    void shouldBuildWithBuilder() {
        NormalizationStatsDTO built = NormalizationStatsDTO.builder()
                .minCo2(2.0)
                .maxCo2(20.0)
                .minEnergy(200.0)
                .maxEnergy(2000.0)
                .minCost(800.0)
                .maxCost(1800.0)
                .minEfficiency(0.3)
                .maxEfficiency(0.95)
                .build();

        assertThat(built.getMaxCost()).isEqualTo(1800.0);
        assertThat(built.getMinEfficiency()).isEqualTo(0.3);
    }

    @Test
    @DisplayName("Should compare objects with equals and hashCode")
    void shouldCompareWithEquals() {
        NormalizationStatsDTO another = new NormalizationStatsDTO(
                1.0, 10.0, 100.0, 1000.0, 500.0, 1500.0, 0.2, 0.9
        );

        assertThat(stats).isEqualTo(another);
        assertThat(stats.hashCode()).isEqualTo(another.hashCode());
    }

    @Test
    @DisplayName("Should handle all zero values")
    void shouldAllowAllZeroValues() {
        NormalizationStatsDTO zeroStats = new NormalizationStatsDTO(
                0, 0, 0, 0, 0, 0, 0, 0
        );

        assertThat(zeroStats.getMaxEnergy()).isZero();
        assertThat(zeroStats.getMinEfficiency()).isZero();
    }

    @Test
    @DisplayName("Should handle extreme values")
    void shouldHandleExtremeValues() {
        NormalizationStatsDTO extremes = new NormalizationStatsDTO(
                Double.MIN_VALUE,
                Double.MAX_VALUE,
                Double.MIN_VALUE,
                Double.MAX_VALUE,
                Double.MIN_VALUE,
                Double.MAX_VALUE,
                Double.MIN_VALUE,
                Double.MAX_VALUE
        );

        assertThat(extremes.getMaxEfficiency()).isEqualTo(Double.MAX_VALUE);
        assertThat(extremes.getMinEfficiency()).isEqualTo(Double.MIN_VALUE);
    }
}
