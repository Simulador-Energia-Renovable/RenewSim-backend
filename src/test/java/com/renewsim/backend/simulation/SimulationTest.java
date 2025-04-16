package com.renewsim.backend.simulation;

import com.renewsim.backend.technologyComparison.TechnologyComparison;
import com.renewsim.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Simulation Entity Unit Tests")
class SimulationTest {

    private Simulation simulation;
    private User user;
    private TechnologyComparison tech1, tech2;

    @BeforeEach
    void setUp() {    
        user = new User();
        user.setId(1L);
        user.setUsername("john@example.com");
        user.setPassword("secure123");
    
        tech1 = new TechnologyComparison();    
        tech1.setTechnologyName("Solar");

        tech2 = new TechnologyComparison();   
        tech2.setTechnologyName("Wind");

        simulation = new Simulation();
        simulation.setLocation("Madrid");
        simulation.setEnergyType("Solar");
        simulation.setProjectSize(50.0);
        simulation.setBudget(10000.0);
        simulation.setEnergyGenerated(12000.0);
        simulation.setEstimatedSavings(5000.0);
        simulation.setReturnOnInvestment(2.5);
        simulation.setEnergyConsumption(8000.0);
        simulation.setUser(user);
        simulation.setTechnologies(new ArrayList<>(List.of(tech1, tech2)));
    }

    @Test
    @DisplayName("Should create simulation with valid data")
    void testShouldCreateSimulationCorrectly() {
        assertThat(simulation.getLocation()).isEqualTo("Madrid");
        assertThat(simulation.getEnergyType()).isEqualTo("Solar");
        assertThat(simulation.getProjectSize()).isEqualTo(50.0);
        assertThat(simulation.getBudget()).isEqualTo(10000.0);
        assertThat(simulation.getEnergyGenerated()).isEqualTo(12000.0);
        assertThat(simulation.getEstimatedSavings()).isEqualTo(5000.0);
        assertThat(simulation.getReturnOnInvestment()).isEqualTo(2.5);
        assertThat(simulation.getEnergyConsumption()).isEqualTo(8000.0);
        assertThat(simulation.getUser()).isEqualTo(user);
        assertThat(simulation.getTechnologies()).containsExactlyInAnyOrder(tech1, tech2);
    }

    @Test
    @DisplayName("Should initialize timestamp on create")
    void testShouldSetTimestampWhenPersisted() {
        simulation.onCreate();

        assertThat(simulation.getTimestamp()).isNotNull();
        assertThat(simulation.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should allow adding and removing technologies")
    void testShouldAddAndRemoveTechnologies() {
        simulation.getTechnologies().remove(tech1);
        assertThat(simulation.getTechnologies()).containsOnly(tech2);

        simulation.getTechnologies().add(tech1);
        assertThat(simulation.getTechnologies()).containsExactlyInAnyOrder(tech1, tech2);
    }

    @Test
    @DisplayName("Should accept empty technology list")
    void testShouldHandleEmptyTechnologyList() {
        simulation.setTechnologies(new ArrayList<>());
        assertThat(simulation.getTechnologies()).isEmpty();
    }

    @Test
    @DisplayName("Should handle zero or negative values gracefully")
    void testShouldHandleExtremeValues() {
        simulation.setProjectSize(0);
        simulation.setBudget(-5000);
        simulation.setEnergyGenerated(-100);
        simulation.setReturnOnInvestment(0);
        simulation.setEstimatedSavings(0);

        assertThat(simulation.getProjectSize()).isEqualTo(0);
        assertThat(simulation.getBudget()).isEqualTo(-5000);
        assertThat(simulation.getEnergyGenerated()).isEqualTo(-100);
        assertThat(simulation.getReturnOnInvestment()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle null user gracefully (not validated here)")
    void testShouldAllowNullUserTemporarily() {
        simulation.setUser(null);
        assertThat(simulation.getUser()).isNull(); 
    }

    @Test
    @DisplayName("Should throw NullPointerException when accessing null technology list")
    void testShouldThrowIfTechnologiesIsNull() {
        simulation.setTechnologies(null);

        assertThrows(NullPointerException.class, () -> simulation.getTechnologies().size());
    }
}