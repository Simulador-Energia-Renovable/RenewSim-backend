package com.renewsim.backend.simulation;

import com.renewsim.backend.simulation.dto.*;
import com.renewsim.backend.simulation.util.TechnologyScoringUtil;
import com.renewsim.backend.technologyComparison.TechnologyComparisonMapper;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationUseCaseTest {

    private TechnologyComparisonResponseDTO tech;
    private NormalizationStatsDTO stats;

    @Mock
    private SimulationService simulationService;

    @Mock
    private SimulationMapper simulationMapper;

    @Mock
    private TechnologyComparisonMapper technologyComparisonMapper;

    @InjectMocks
    private SimulationUseCase simulationUseCase;

    @BeforeEach
    void setupData() {
        tech = TechnologyComparisonResponseDTO.builder()
                .technologyName("Solar")
                .co2Reduction(100.0)
                .energyProduction(3000.0)
                .installationCost(15000.0)
                .efficiency(0.18)
                .build();

        stats = NormalizationStatsDTO.builder()
                .minCo2(0.0).maxCo2(200.0)
                .minEnergy(1000.0).maxEnergy(5000.0)
                .minCost(10000.0).maxCost(20000.0)
                .minEfficiency(0.1).maxEfficiency(0.3)
                .build();
    }

    @Test
    @DisplayName("Should simulate and save")
    void shouldSimulateAndSave() {
        SimulationRequestDTO request = new SimulationRequestDTO();
        SimulationResponseDTO response = new SimulationResponseDTO();

        when(simulationService.simulateAndSave(request)).thenReturn(response);

        SimulationResponseDTO result = simulationUseCase.simulateAndSave(request, "user");

        assertThat(result).isEqualTo(response);
        verify(simulationService).simulateAndSave(request);
    }

    @Test
    @DisplayName("Should return user simulations")
    void shouldReturnUserSimulations() {
        List<Simulation> simulations = List.of(new Simulation());
        when(simulationService.getUserSimulations("user")).thenReturn(simulations);

        List<Simulation> result = simulationUseCase.getUserSimulations("user");

        assertThat(result).isEqualTo(simulations);
    }

    @Test
    @DisplayName("Should return simulation history DTOs")
    void shouldReturnSimulationHistoryDTOs() {
        Simulation sim = new Simulation();
        SimulationHistoryDTO dto = new SimulationHistoryDTO();

        when(simulationService.getUserSimulations("user")).thenReturn(List.of(sim));
        when(simulationMapper.toHistoryDTO(sim)).thenReturn(dto);

        List<SimulationHistoryDTO> result = simulationUseCase.getUserSimulationHistoryDTOs("user");

        assertThat(result).containsExactly(dto);
    }

    @Test
    @DisplayName("Should delete all user simulations")
    void shouldDeleteUserSimulations() {
        simulationUseCase.deleteUserSimulations("user");
        verify(simulationService).deleteSimulationsByUser("user");
    }

    @Test
    @DisplayName("Should delete simulation by ID")
    void shouldDeleteSimulationById() {
        simulationUseCase.deleteSimulationById(123L);
        verify(simulationService).deleteSimulationById(123L);
    }

    @Test
    @DisplayName("Should return normalization stats")
    void shouldReturnNormalizationStats() {
        NormalizationStatsDTO stats = new NormalizationStatsDTO();
        when(simulationService.getCurrentNormalizationStats()).thenReturn(stats);

        NormalizationStatsDTO result = simulationUseCase.getCurrentNormalizationStats();

        assertThat(result).isEqualTo(stats);
    }

    @Test
    @DisplayName("Should return all technologies")
    void shouldReturnAllTechnologies() {
        List<TechnologyComparisonResponseDTO> techList = List.of(new TechnologyComparisonResponseDTO());
        when(simulationService.getAllTechnologies()).thenReturn(techList);

        List<TechnologyComparisonResponseDTO> result = simulationUseCase.getAllTechnologies();

        assertThat(result).isEqualTo(techList);
    }

    @Test
    @DisplayName("Should return normalized technologies")
    void shouldReturnNormalizedTechnologies() {
        when(simulationService.getAllTechnologies()).thenReturn(List.of(tech));

        try (MockedStatic<TechnologyScoringUtil> scoringUtilMock = mockStatic(TechnologyScoringUtil.class)) {
            scoringUtilMock.when(() -> TechnologyScoringUtil.calculateNormalizationStats(any()))
                    .thenReturn(stats);
            scoringUtilMock.when(() -> TechnologyScoringUtil.normalize(anyDouble(), anyDouble(), anyDouble()))
                    .thenReturn(0.5);
            scoringUtilMock.when(() -> TechnologyScoringUtil.calculateScoreDynamic(eq(tech), eq(stats)))
                    .thenReturn(0.8);

            List<NormalizedTechnologyDTO> result = simulationUseCase.getNormalizedTechnologies();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTechnologyName()).isEqualTo("Solar");
            assertThat(result.get(0).getNormalizedCo2Reduction()).isEqualTo(0.5);
            assertThat(result.get(0).getScore()).isEqualTo(0.8);
        }
    }

    @Test
    @DisplayName("Should return empty list when user has no simulations")
    void shouldReturnEmptyHistoryIfNoSimulations() {
        when(simulationService.getUserSimulations("user")).thenReturn(List.of());

        List<SimulationHistoryDTO> result = simulationUseCase.getUserSimulationHistoryDTOs("user");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty normalized tech list when no techs available")
    void shouldReturnEmptyNormalizedTechList() {
        when(simulationService.getAllTechnologies()).thenReturn(List.of());

        try (MockedStatic<TechnologyScoringUtil> mocked = mockStatic(TechnologyScoringUtil.class)) {
            mocked.when(() -> TechnologyScoringUtil.calculateNormalizationStats(any()))
                    .thenReturn(stats);
            List<NormalizedTechnologyDTO> result = simulationUseCase.getNormalizedTechnologies();
            assertThat(result).isEmpty();
        }
    }

    @Test
    @DisplayName("Should return technologies for a given simulation")
    void shouldReturnTechnologiesForSimulation() {
        Simulation simulation = new Simulation();
        simulation.setTechnologies(List.of(new com.renewsim.backend.technologyComparison.TechnologyComparison()));

        when(simulationService.getSimulationById(1L)).thenReturn(simulation);
        when(technologyComparisonMapper.toResponseDTO(any())).thenReturn(tech);

        List<TechnologyComparisonResponseDTO> result = simulationUseCase.getTechnologiesForSimulation(1L);

        assertThat(result).containsExactly(tech);
    }

}
