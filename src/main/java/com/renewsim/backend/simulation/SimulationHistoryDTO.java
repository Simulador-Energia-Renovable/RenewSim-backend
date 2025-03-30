package com.renewsim.backend.simulation;

import java.time.LocalDateTime;

public class SimulationHistoryDTO {
    private String location;
    private String energyType;
    private double energyGenerated;
    private double estimatedSavings;
    private double returnOnInvestment;
    private LocalDateTime timestamp;
    
    public SimulationHistoryDTO(String location, String energyType, double energyGenerated, double estimatedSavings, double returnOnInvestment, LocalDateTime timestamp) {
        this.location = location;
        this.energyType = energyType;
        this.energyGenerated = energyGenerated;
        this.estimatedSavings = estimatedSavings;
        this.returnOnInvestment = returnOnInvestment;
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public String getEnergyType() { 
        return energyType;
    }

    public double getEnergyGenerated() {
        return energyGenerated;
    }

    public double getEstimatedSavings() {
        return estimatedSavings;
    }

    public double getReturnOnInvestment() {
        return returnOnInvestment;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
        
}

