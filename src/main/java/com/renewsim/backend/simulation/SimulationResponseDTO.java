package com.renewsim.backend.simulation;

import java.util.List;

import com.renewsim.backend.technologyComparison.TechnologyComparisonResponseDTO;

public class SimulationResponseDTO {
    private double energyGenerated;
    private double estimatedSavings;
    private double returnOnInvestment;
    private List<TechnologyComparisonResponseDTO> technologies;
    private Long simulationId;

    public SimulationResponseDTO(Long simulationId, double energyGenerated, double estimatedSavings, double returnOnInvestment,
            List<TechnologyComparisonResponseDTO> technologies) {
        this.energyGenerated = energyGenerated;
        this.estimatedSavings = estimatedSavings;
        this.returnOnInvestment = returnOnInvestment;
        this.technologies = technologies;
    }

    public double getReturnOnInvestment() {
        return returnOnInvestment;
    }

    public double getEnergyGenerated() {
        return energyGenerated;
    }

    public double getEstimatedSavings() {
        return estimatedSavings;
    }

    public List<TechnologyComparisonResponseDTO> getTechnologies() {
        return technologies;
    }

    public Long getSimulationId() {
        return simulationId;
    }
}
