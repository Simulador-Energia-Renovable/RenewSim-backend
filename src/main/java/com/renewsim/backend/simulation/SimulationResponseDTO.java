package com.renewsim.backend.simulation;

public class SimulationResponseDTO {
    private double energyGenerated;
    private double estimatedSavings;
    private double returnOnInvestment;

    public SimulationResponseDTO(double energyGenerated, double estimatedSavings, double returnOnInvestment) {
        this.energyGenerated = energyGenerated;
        this.estimatedSavings = estimatedSavings;
        this.returnOnInvestment = returnOnInvestment;
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
}
