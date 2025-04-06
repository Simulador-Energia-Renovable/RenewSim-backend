package com.renewsim.backend.simulation;

import java.util.List;

public interface SimulationService {

    SimulationResponseDTO simulateAndSave(SimulationRequestDTO dto);

    SimulationResponseDTO calculateSimulation(SimulationRequestDTO dto);

    List<Simulation> getUserSimulations(String username);

    Simulation getSimulationById(Long simulationId);

    List<SimulationHistoryDTO> getUserSimulationHistoryDTOs(String username);

    void deleteSimulationsByUser(String username);

    NormalizationStatsDTO getCurrentNormalizationStats();

}
