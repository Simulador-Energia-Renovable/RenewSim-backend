package com.renewsim.backend.simulation;

import java.util.List;

import com.renewsim.backend.simulation.dto.NormalizationStatsDTO;
import com.renewsim.backend.simulation.dto.SimulationHistoryDTO;
import com.renewsim.backend.simulation.dto.SimulationRequestDTO;
import com.renewsim.backend.simulation.dto.SimulationResponseDTO;
import com.renewsim.backend.technologyComparison.TechnologyComparisonResponseDTO;

public interface SimulationService {

    SimulationResponseDTO simulateAndSave(SimulationRequestDTO dto);

    SimulationResponseDTO calculateSimulation(SimulationRequestDTO dto);

    List<Simulation> getUserSimulations(String username);

    Simulation getSimulationById(Long simulationId);

    List<SimulationHistoryDTO> getUserSimulationHistoryDTOs(String username);

    void deleteSimulationsByUser(String username);

    NormalizationStatsDTO getCurrentNormalizationStats();

    List<TechnologyComparisonResponseDTO> getAllTechnologies();


}
