package com.renewsim.backend.simulation;

import com.renewsim.backend.simulation.dto.SimulationHistoryDTO;
import com.renewsim.backend.simulation.dto.SimulationResponseDTO;
import com.renewsim.backend.technologyComparison.TechnologyComparison;
import com.renewsim.backend.technologyComparison.TechnologyComparisonMapper;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimulationMapper Unit Tests")
class SimulationMapperTest {

    private Simulation simulation;

     @Mock
    private TechnologyComparisonMapper technologyComparisonMapper;

     @InjectMocks
    private SimulationMapper simulationMapper;

 

    @BeforeEach
    void setUp() {
        simulation = new Simulation();
        simulation.setId(1L);
        simulation.setLocation("Asturias");
        simulation.setEnergyType("Solar");
        simulation.setEnergyGenerated(12000.0);
        simulation.setEstimatedSavings(5000.0);
        simulation.setProjectSize(5.0);
        simulation.setBudget(10000.0);
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

    @Test
    @DisplayName("Should map null fields without throwing exception")
    void testShouldMapEvenWithNullFields() {
        Simulation sim = new Simulation();

        SimulationHistoryDTO dto = simulationMapper.toHistoryDTO(sim);

        assertThat(dto.getLocation()).isNull();
        assertThat(dto.getTimestamp()).isNull();
    }

    @Test
    @DisplayName("Should map null fields without throwing exception")
    void shouldMapEvenWithNullFields() {
        Simulation emptySim = new Simulation();

        SimulationHistoryDTO dto = simulationMapper.toHistoryDTO(emptySim);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getLocation()).isNull();
        assertThat(dto.getEnergyType()).isNull();
        assertThat(dto.getEnergyGenerated()).isEqualTo(0.0);
        assertThat(dto.getEstimatedSavings()).isEqualTo(0.0);
        assertThat(dto.getReturnOnInvestment()).isEqualTo(0.0);
        assertThat(dto.getTimestamp()).isNull();
    }

    @Test
    @DisplayName("Should map Simulation to SimulationResponseDTO including technologies")
    void testShouldMapSimulationToResponseDTO() {
        
        TechnologyComparison tech = new TechnologyComparison();
        simulation.setTechnologies(List.of(tech));
        simulation.setRecommendedTechnology("Solar Panel Pro");

        TechnologyComparisonResponseDTO dtoMock = new TechnologyComparisonResponseDTO();
        when(technologyComparisonMapper.toResponseDTO(tech)).thenReturn(dtoMock);

        // When
        SimulationResponseDTO responseDTO = simulationMapper.toDTO(simulation);

        // Then
        assertThat(responseDTO.getLocation()).isEqualTo("Asturias");
        assertThat(responseDTO.getProjectSize()).isEqualTo(5.0);
        assertThat(responseDTO.getTechnologies()).hasSize(1);
        assertThat(responseDTO.getRecommendedTechnology()).isEqualTo("Solar Panel Pro");

        verify(technologyComparisonMapper).toResponseDTO(tech);
    }

}
