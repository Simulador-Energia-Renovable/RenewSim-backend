package com.renewsim.backend.simulation;

import java.util.List;

public interface SimulationService {

    SimulationResponseDTO simulateAndSave(SimulationRequestDTO dto);

    SimulationResponseDTO calculateSimulation(SimulationRequestDTO dto,Long simulationId);

    List<Simulation> getUserSimulations(String username);

    Simulation getSimulationById(Long simulationId);
}

