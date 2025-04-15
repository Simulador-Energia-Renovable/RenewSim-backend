package com.renewsim.backend.technologyComparison;

import com.renewsim.backend.simulation.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit tests for TechnologyComparison model")
class TechnologyComparisonTest {

    private TechnologyComparison baseTech;

    @BeforeEach
    void setUp() {
        baseTech = new TechnologyComparison(
                "Solar", 85.0, 1000.0, 70.0,
                "Clean", 50.0, 300.0, "Solar"
        );
    }

    @Test
    @DisplayName("Should correctly construct TechnologyComparison using constructor")
    void testShouldConstructWithAllFields() {
        assertThat(baseTech.getTechnologyName()).isEqualTo("Solar");
        assertThat(baseTech.getEfficiency()).isEqualTo(85.0);
        assertThat(baseTech.getInstallationCost()).isEqualTo(1000.0);
        assertThat(baseTech.getMaintenanceCost()).isEqualTo(70.0);
        assertThat(baseTech.getEnvironmentalImpact()).isEqualTo("Clean");
        assertThat(baseTech.getCo2Reduction()).isEqualTo(50.0);
        assertThat(baseTech.getEnergyProduction()).isEqualTo(300.0);
        assertThat(baseTech.getEnergyType()).isEqualTo("Solar");
    }

    @Test
    @DisplayName("Should correctly set and get fields")
    void testShouldUseSettersAndGetters() {
        TechnologyComparison tech = new TechnologyComparison();
        tech.setTechnologyName("Wind");
        tech.setEfficiency(75.0);
        tech.setInstallationCost(1500.0);
        tech.setMaintenanceCost(90.0);
        tech.setEnvironmentalImpact("Moderate");
        tech.setCo2Reduction(60.0);
        tech.setEnergyProduction(400.0);
        tech.setEnergyType("Wind");

        assertThat(tech.getTechnologyName()).isEqualTo("Wind");
        assertThat(tech.getEfficiency()).isEqualTo(75.0);
        assertThat(tech.getInstallationCost()).isEqualTo(1500.0);
        assertThat(tech.getMaintenanceCost()).isEqualTo(90.0);
        assertThat(tech.getEnvironmentalImpact()).isEqualTo("Moderate");
        assertThat(tech.getCo2Reduction()).isEqualTo(60.0);
        assertThat(tech.getEnergyProduction()).isEqualTo(400.0);
        assertThat(tech.getEnergyType()).isEqualTo("Wind");
    }

    @Test
    @DisplayName("Should set and get simulations list")
    void testShouldHandleSimulationsList() {
        Simulation simulation = new Simulation();
        baseTech.setSimulations(List.of(simulation));

        assertThat(baseTech.getSimulations()).containsExactly(simulation);
    }

    @Test
    @DisplayName("toString should include key fields")
    void testShouldReturnToStringWithDetails() {
        String result = baseTech.toString();
        assertThat(result).contains("Solar", "85.0", "1000.0", "Clean");
    }
}

