package com.renewsim.backend.simulation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.renewsim.backend.simulation.dto.NormalizationStatsDTO;
import com.renewsim.backend.simulation.dto.NormalizedTechnologyDTO;
import com.renewsim.backend.simulation.dto.SimulationHistoryDTO;
import com.renewsim.backend.simulation.dto.SimulationRequestDTO;
import com.renewsim.backend.simulation.dto.SimulationResponseDTO;
import com.renewsim.backend.simulation.util.TechnologyScoringUtil;
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
            TechnologyComparisonMapper technologyComparisonMapper) {
        this.simulationService = simulationService;
        this.simulationMapper = simulationMapper;
        this.technologyComparisonMapper = technologyComparisonMapper;
    }

    // Simulate and save new simulation
    public SimulationResponseDTO simulateAndSave(SimulationRequestDTO dto, String username) {
        return simulationService.simulateAndSave(dto);
    }

    // Get user simulations (entities)
    public List<Simulation> getUserSimulations(String username) {
        return simulationService.getUserSimulations(username);
    }

    // Get user simulation history (DTO) — Using mapper
    public List<SimulationHistoryDTO> getUserSimulationHistoryDTOs(String username) {
        return simulationService.getUserSimulations(username).stream()
                .map(simulationMapper::toHistoryDTO)
                .collect(Collectors.toList());
    }

    // Get technologies associated with a specific simulation — Using mapper
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

    public List<NormalizedTechnologyDTO> getNormalizedTechnologies() {
        List<TechnologyComparisonResponseDTO> techList = simulationService.getAllTechnologies();

        NormalizationStatsDTO stats = TechnologyScoringUtil.calculateNormalizationStats(techList);

        return techList.stream().map(tech -> {
            double normalizedCo2 = TechnologyScoringUtil.normalize(tech.getCo2Reduction(), stats.getMinCo2(),
                    stats.getMaxCo2());
            double normalizedEnergy = TechnologyScoringUtil.normalize(tech.getEnergyProduction(), stats.getMinEnergy(),
                    stats.getMaxEnergy());
            double normalizedCost = TechnologyScoringUtil.normalize(tech.getInstallationCost(), stats.getMinCost(),
                    stats.getMaxCost());
            double normalizedEff = TechnologyScoringUtil.normalize(tech.getEfficiency(), stats.getMinEfficiency(),
                    stats.getMaxEfficiency());
            double score = TechnologyScoringUtil.calculateScoreDynamic(tech, stats);

            return NormalizedTechnologyDTO.builder()
                    .technologyName(tech.getTechnologyName())
                    .normalizedCo2Reduction(normalizedCo2)
                    .normalizedEnergyProduction(normalizedEnergy)
                    .normalizedInstallationCost(normalizedCost)
                    .normalizedEfficiency(normalizedEff)
                    .score(score)
                    .build();
        }).collect(Collectors.toList());
    }

    public List<TechnologyComparisonResponseDTO> getAllTechnologies() {
        return simulationService.getAllTechnologies();
    }

}
