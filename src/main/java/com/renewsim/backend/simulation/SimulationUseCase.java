package com.renewsim.backend.simulation;

import java.util.List;

import org.springframework.stereotype.Component;

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
}
