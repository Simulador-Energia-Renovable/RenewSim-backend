package com.renewsim.backend.technologyComparison.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests for TechnologyComparisonResponseDTO")
class TechnologyComparisonResponseDTOTest {

    @Test
    @DisplayName("Should correctly build DTO using builder pattern")
    void shouldBuildDtoCorrectly() {
        TechnologyComparisonResponseDTO dto = TechnologyComparisonResponseDTO.builder()
                .technologyName("Solar Panel")
                .efficiency(88.5)
                .installationCost(2000.0)
                .maintenanceCost(150.0)
                .environmentalImpact("Low carbon footprint")
                .co2Reduction(45.2)
                .energyProduction(320.0)
                .energyType("Solar")
                .build();

        assertEquals("Solar Panel", dto.getTechnologyName());
        assertEquals(88.5, dto.getEfficiency());
        assertEquals(2000.0, dto.getInstallationCost());
        assertEquals(150.0, dto.getMaintenanceCost());
        assertEquals("Low carbon footprint", dto.getEnvironmentalImpact());
        assertEquals(45.2, dto.getCo2Reduction());
        assertEquals(320.0, dto.getEnergyProduction());
        assertEquals("Solar", dto.getEnergyType());
    }

    @Test
    @DisplayName("Should allow setting and getting fields manually")
    void shouldSetAndGetFields() {
        TechnologyComparisonResponseDTO dto = new TechnologyComparisonResponseDTO();
        dto.setTechnologyName("Wind Turbine");
        dto.setEfficiency(75.0);
        dto.setInstallationCost(5000.0);
        dto.setMaintenanceCost(300.0);
        dto.setEnvironmentalImpact("Moderate noise");
        dto.setCo2Reduction(60.0);
        dto.setEnergyProduction(450.0);
        dto.setEnergyType("Wind");

        assertEquals("Wind Turbine", dto.getTechnologyName());
        assertEquals(75.0, dto.getEfficiency());
        assertEquals(5000.0, dto.getInstallationCost());
        assertEquals(300.0, dto.getMaintenanceCost());
        assertEquals("Moderate noise", dto.getEnvironmentalImpact());
        assertEquals(60.0, dto.getCo2Reduction());
        assertEquals(450.0, dto.getEnergyProduction());
        assertEquals("Wind", dto.getEnergyType());
    }
}
