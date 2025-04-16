package com.renewsim.backend.simulation;

import com.renewsim.backend.technologyComparison.TechnologyComparison;
import com.renewsim.backend.technologyComparison.TechnologyComparisonRepository;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Simulation Entity Integration Tests")
class SimulationIntegrationTest {

    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TechnologyComparisonRepository technologyComparisonRepository;

    @Test
    @DisplayName("Should persist simulation with user and technologies")
    void shouldPersistSimulationCorrectly() {

        User user = new User();
        user.setUsername("jane@example.com");
        user.setPassword("pass123");
        userRepository.save(user);

        TechnologyComparison tech1 = new TechnologyComparison();
        tech1.setTechnologyName("Solar");
        tech1.setEfficiency(0.85);
        tech1.setInstallationCost(4000.0);
        tech1.setMaintenanceCost(200.0);
        tech1.setEnvironmentalImpact("Low");
        tech1.setCo2Reduction(1.2);
        tech1.setEnergyProduction(15000.0);
        tech1.setEnergyType("Solar");

        technologyComparisonRepository.save(tech1);

        Simulation sim = new Simulation();
        sim.setLocation("Gijón");
        sim.setEnergyType("Solar");
        sim.setProjectSize(60.0);
        sim.setBudget(12000.0);
        sim.setEnergyGenerated(15000.0);
        sim.setEstimatedSavings(7000.0);
        sim.setReturnOnInvestment(2.8);
        sim.setEnergyConsumption(9000.0);
        sim.setUser(user);
        sim.setTechnologies(List.of(tech1));

        sim.onCreate();

        Simulation saved = simulationRepository.save(sim);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTimestamp()).isNotNull();
        assertThat(saved.getUser().getUsername()).isEqualTo("jane@example.com");
        assertThat(saved.getTechnologies()).hasSize(1);
        assertThat(saved.getTechnologies().get(0).getTechnologyName()).isEqualTo("Solar");
    }

    @Test
    @DisplayName("Should fail when required fields are missing")
    void shouldFailWhenRequiredFieldsAreMissing() {
        Simulation sim = new Simulation(); 

        assertThrows(Exception.class, () -> simulationRepository.saveAndFlush(sim));
    }
}

