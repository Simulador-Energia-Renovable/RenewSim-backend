package com.renewsim.backend.simulation.logic;

import com.renewsim.backend.simulation.dto.ClimateData;
import com.renewsim.backend.simulation.dto.SimulationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SimulationCalculator Unit Tests")
class SimulationCalculatorTest {

    private SimulationCalculator calculator;
    private ClimateData climate;

    @BeforeEach
    void setUp() {
        calculator = new SimulationCalculator();

        climate = new ClimateData();
        climate.setIrradiance(5.0);
        climate.setWind(6.0);
        climate.setHydrology(4.0);
    }

    private SimulationRequestDTO createDTO(String type, double size) {
        SimulationRequestDTO dto = new SimulationRequestDTO();
        dto.setEnergyType(type);
        dto.setProjectSize(size);
        dto.setClimate(climate);
        return dto;
    }

    @Test
    @DisplayName("Should calculate energy generated for solar")
    void testShouldCalculateEnergyForSolar() {
        SimulationRequestDTO dto = createDTO("solar", 10);
        double energy = calculator.calculateEnergyGenerated(dto);
        assertThat(energy).isEqualTo(5.0 * 0.18 * 10 * 365);
    }

    @Test
    @DisplayName("Should calculate energy generated for wind")
    void testShouldCalculateEnergyForWind() {
        SimulationRequestDTO dto = createDTO("wind", 20);
        double energy = calculator.calculateEnergyGenerated(dto);
        assertThat(energy).isEqualTo(6.0 * 0.40 * 20 * 365);
    }

    @Test
    @DisplayName("Should calculate energy generated for hydro")
    void testShouldCalculateEnergyForHydro() {
        SimulationRequestDTO dto = createDTO("hydro", 15);
        double energy = calculator.calculateEnergyGenerated(dto);
        assertThat(energy).isEqualTo(4.0 * 0.50 * 15 * 365);
    }

    @Test
    @DisplayName("Should calculate estimated savings correctly")
    void testShouldCalculateEstimatedSavings() {
        double savings = calculator.calculateEstimatedSavings(10000);
        assertThat(savings).isEqualTo(1200.0);
    }

    @Test
    @DisplayName("Should calculate ROI correctly")
    void testShouldCalculateROI() {
        double roi = calculator.calculateROI(10000, 2500);
        assertThat(roi).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Should return ROI 0 if estimated savings is zero")
    void testShouldHandleZeroEstimatedSavingsInROI() {
        double roi = calculator.calculateROI(5000, 0);
        assertThat(roi).isZero();
    }

    @Test
    @DisplayName("Should throw exception for unknown energy type in getIrradiance")
    void testShouldThrowForUnknownEnergyTypeInIrradiance() {
        SimulationRequestDTO dto = createDTO("nuclear", 10);
        assertThatThrownBy(() -> calculator.calculateEnergyGenerated(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de energía no reconocido");
    }

    @Test
    @DisplayName("Should throw exception for unknown energy type in getEfficiency")
    void testShouldThrowForUnknownEnergyTypeInEfficiency() {
        SimulationRequestDTO dto = createDTO("nuclear", 10);

        assertThatThrownBy(() -> calculator.calculateEnergyGenerated(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should handle edge case: zero project size")
    void testShouldReturnZeroEnergyIfProjectSizeIsZero() {
        SimulationRequestDTO dto = createDTO("solar", 0);
        double result = calculator.calculateEnergyGenerated(dto);
        assertThat(result).isZero();
    }

    @Test
    @DisplayName("Should handle edge case: negative climate values")
    void testShouldCalculateEnergyWithNegativeClimateData() {
        climate.setIrradiance(-3.0);
        SimulationRequestDTO dto = createDTO("solar", 5);
        double energy = calculator.calculateEnergyGenerated(dto);
        assertThat(energy).isLessThan(0);
    }

    @Test
    @DisplayName("Should throw exception for unknown energy type in getEfficiency only")
    void testShouldThrowForUnknownEnergyTypeInGetEfficiency() {

        assertThatThrownBy(() -> calculator.calculateROI(1000, calculator.calculateEstimatedSavings(
                calculator.calculateEnergyGenerated(createDTO("desconocido", 10)))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de energía no reconocido");
    }

    @Test
    @DisplayName("Should estimate project size correctly for solar")
    void testShouldEstimateProjectSizeForSolar() {
        double monthlyConsumption = 540.0;
        double expectedSize = 540.0 / (5.0 * 0.18 * 30);

        double result = calculator.estimateProjectSize(monthlyConsumption, "solar", climate);

        assertThat(result).isEqualTo(Math.round(expectedSize * 100.0) / 100.0);
    }

    @Test
    @DisplayName("Should throw exception for unknown energy type in estimateProjectSize")
    void testShouldThrowForUnknownEnergyTypeInEstimateProjectSize() {
        assertThatThrownBy(() -> calculator.estimateProjectSize(500, "nuclear", climate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de energía no reconocido");
    }

    
}
