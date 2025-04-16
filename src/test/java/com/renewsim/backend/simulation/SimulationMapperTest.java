package com.renewsim.backend.simulation;

import com.renewsim.backend.simulation.dto.SimulationHistoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SimulationMapper Unit Tests")
class SimulationMapperTest {

    private SimulationMapper simulationMapper;
    private Simulation simulation;

    @BeforeEach
    void setUp() {
        simulationMapper = new SimulationMapper();

        simulation = new Simulation();
        simulation.setId(1L);
        simulation.setLocation("Asturias");
        simulation.setEnergyType("Solar");
        simulation.setEnergyGenerated(12000.0);
        simulation.setEstimatedSavings(5000.0);
        simulation.setReturnOnInvestment(2.5);
        simulation.setTimestamp(LocalDateTime.of(2024, 4, 15, 12, 0));
    }

    @Test
    @DisplayName("Should map Simulation to SimulationHistoryDTO correctly")
    void testShouldMapSimulationToDTO() {
        SimulationHistoryDTO dto = simulationMapper.toHistoryDTO(simulation);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getLocation()).isEqualTo("Asturias");
        assertThat(dto.getEnergyType()).isEqualTo("Solar");
        assertThat(dto.getEnergyGenerated()).isEqualTo(12000.0);
        assertThat(dto.getEstimatedSavings()).isEqualTo(5000.0);
        assertThat(dto.getReturnOnInvestment()).isEqualTo(2.5);
        assertThat(dto.getTimestamp()).isEqualTo(LocalDateTime.of(2024, 4, 15, 12, 0));
    }
}

