package com.renewsim.backend.simulation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.renewsim.backend.technologyComparison.TechnologyComparisonMapper;
import com.renewsim.backend.technologyComparison.TechnologyComparisonResponseDTO;

@Component
public class SimulationUseCase {

    private final SimulationService simulationService;
    private final SimulationMapper simulationMapper;
    private final TechnologyComparisonMapper technologyComparisonMapper;

    public SimulationUseCase(
            SimulationService simulationService,
            SimulationMapper simulationMapper,
            TechnologyComparisonMapper technologyComparisonMapper
    ) {
        this.simulationService = simulationService;
        this.simulationMapper = simulationMapper;
        this.technologyComparisonMapper = technologyComparisonMapper;
    }

    //Simulate and save new simulation
    public SimulationResponseDTO simulateAndSave(SimulationRequestDTO dto, String username) {
        return simulationService.simulateAndSave(dto);
    }

    // Get user simulations (entities)
    public List<Simulation> getUserSimulations(String username) {
        return simulationService.getUserSimulations(username);
    }

    //Get user simulation history (DTO) — Using mapper
    public List<SimulationHistoryDTO> getUserSimulationHistoryDTOs(String username) {
        return simulationService.getUserSimulations(username).stream()
                .map(simulationMapper::toHistoryDTO)
                .collect(Collectors.toList());
    }

    //Get technologies associated with a specific simulation — Using mapper
    public List<TechnologyComparisonResponseDTO> getTechnologiesForSimulation(Long simulationId) {
        Simulation simulation = simulationService.getSimulationById(simulationId);

        return simulation.getTechnologies().stream()
                .map(technologyComparisonMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Delete all simulations of the user
    public void deleteUserSimulations(String username) {
        simulationService.deleteSimulationsByUser(username);
    }

    public NormalizationStatsDTO getCurrentNormalizationStats() {
        return simulationService.getCurrentNormalizationStats();
    }
    
}

