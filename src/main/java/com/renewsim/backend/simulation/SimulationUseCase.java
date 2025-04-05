package com.renewsim.backend.simulation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.renewsim.backend.technologyComparison.TechnologyComparisonResponseDTO;

@Component
public class SimulationUseCase {

    private final SimulationService simulationService;

    public SimulationUseCase(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    public SimulationResponseDTO simulateAndSave(SimulationRequestDTO dto, String username) {
        return simulationService.simulateAndSave(dto);
    }

    public List<Simulation> getUserSimulations(String username) {
        return simulationService.getUserSimulations(username);
    }    

    public List<TechnologyComparisonResponseDTO> getTechnologiesForSimulation(Long simulationId) {
        Simulation simulation = simulationService.getSimulationById(simulationId);
    
        return simulation.getTechnologies().stream()
                .map(tech -> new TechnologyComparisonResponseDTO(
                        tech.getTechnologyName(),
                        tech.getEfficiency(),
                        tech.getInstallationCost(),
                        tech.getMaintenanceCost(),
                        tech.getEnvironmentalImpact(),
                        tech.getCo2Reduction(),
                        tech.getEnergyProduction()))
                .collect(Collectors.toList());
    }

    public List<SimulationHistoryDTO> getUserSimulationHistoryDTOs(String username) {
        return simulationService.getUserSimulationHistoryDTOs(username);
    }
    
    
    
}
