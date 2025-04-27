package com.renewsim.backend.simulation;

import com.renewsim.backend.simulation.dto.SimulationHistoryDTO;
import com.renewsim.backend.simulation.dto.SimulationResponseDTO;
import com.renewsim.backend.technologyComparison.TechnologyComparisonMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SimulationMapper {

    private final TechnologyComparisonMapper technologyComparisonMapper;

    public SimulationMapper(TechnologyComparisonMapper technologyComparisonMapper) {
        this.technologyComparisonMapper = technologyComparisonMapper;
    }

    public SimulationHistoryDTO toHistoryDTO(Simulation simulation) {
        return SimulationHistoryDTO.builder()
                .id(simulation.getId())
                .location(simulation.getLocation())
                .energyType(simulation.getEnergyType())
                .energyGenerated(simulation.getEnergyGenerated())
                .estimatedSavings(simulation.getEstimatedSavings())
                .returnOnInvestment(simulation.getReturnOnInvestment())
                .timestamp(simulation.getTimestamp())
                .build();
    }

    public SimulationResponseDTO toDTO(Simulation simulation) {
        return SimulationResponseDTO.builder()
                .location(simulation.getLocation()) 
                .energyType(simulation.getEnergyType())
                .energyGenerated(simulation.getEnergyGenerated())
                .estimatedSavings(simulation.getEstimatedSavings())
                .returnOnInvestment(simulation.getReturnOnInvestment())
                .projectSize(simulation.getProjectSize())
                .budget(simulation.getBudget())
                .timestamp(simulation.getTimestamp())
                .technologies(
                        simulation.getTechnologies().stream()
                                .map(technologyComparisonMapper::toResponseDTO)
                                .collect(Collectors.toList()))
                .recommendedTechnology(simulation.getRecommendedTechnology())
                .build();
    }

}
