package com.renewsim.backend.simulation.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClimateData DTO Unit Tests")
class ClimateDataTest {
    private ClimateData baseData;

    @BeforeEach
    void setUp() {
        baseData = new ClimateData();
        baseData.setIrradiance(4.0);
        baseData.setWind(2.5);
        baseData.setHydrology(7.1);
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testShouldSetAndGetFields() {
        assertThat(baseData.getIrradiance()).isEqualTo(4.0);
        assertThat(baseData.getWind()).isEqualTo(2.5);
        assertThat(baseData.getHydrology()).isEqualTo(7.1);
    }

    @Test
    @DisplayName("Should compare ClimateData objects correctly with equals()")
    void testShouldCompareObjectsWithEquals() {
        ClimateData other = new ClimateData();
        other.setIrradiance(4.0);
        other.setWind(2.5);
        other.setHydrology(7.1);

        assertThat(baseData).isEqualTo(other);
        assertThat(baseData.hashCode()).isEqualTo(other.hashCode());
    }

    @Test
    @DisplayName("Should not be equal if fields differ")
    void testShouldNotBeEqualIfFieldsDiffer() {
        ClimateData different = new ClimateData();
        different.setIrradiance(1.0);
        different.setWind(2.5);
        different.setHydrology(7.1);

        assertThat(baseData).isNotEqualTo(different);
    }
}

