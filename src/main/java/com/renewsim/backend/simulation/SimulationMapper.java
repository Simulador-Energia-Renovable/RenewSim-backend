package com.renewsim.backend.simulation;

public class SimulationMapper {
    public static SimulationHistoryDTO toHistoryDTO(Simulation simulation) {
        return new SimulationHistoryDTO(
            simulation.getLocation(),
            simulation.getEnergyType(),
            simulation.getEnergyGenerated(),
            simulation.getEstimatedSavings(),
            simulation.getReturnOnInvestment(),
            simulation.getTimestamp()
        );
    }
}

