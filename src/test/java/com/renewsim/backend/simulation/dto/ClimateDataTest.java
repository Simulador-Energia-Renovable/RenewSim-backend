package com.renewsim.backend.simulation.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClimateData DTO Unit Tests")
class ClimateDataTest {

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testShouldSetAndGetFields() {
        ClimateData data = new ClimateData();
        data.setIrradiance(5.5);
        data.setWind(3.2);
        data.setHydrology(8.7);

        assertThat(data.getIrradiance()).isEqualTo(5.5);
        assertThat(data.getWind()).isEqualTo(3.2);
        assertThat(data.getHydrology()).isEqualTo(8.7);
    }

    @Test
    @DisplayName("Should compare ClimateData objects correctly with equals()")
    void testShouldCompareObjectsWithEquals() {
        ClimateData data1 = new ClimateData();
        data1.setIrradiance(4.0);
        data1.setWind(2.5);
        data1.setHydrology(7.1);

        ClimateData data2 = new ClimateData();
        data2.setIrradiance(4.0);
        data2.setWind(2.5);
        data2.setHydrology(7.1);

        assertThat(data1).isEqualTo(data2);
        assertThat(data1.hashCode()).isEqualTo(data2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal if fields differ")
    void testShouldNotBeEqualIfFieldsDiffer() {
        ClimateData data1 = new ClimateData();
        data1.setIrradiance(1.0);

        ClimateData data2 = new ClimateData();
        data2.setIrradiance(2.0);

        assertThat(data1).isNotEqualTo(data2);
    }
}

