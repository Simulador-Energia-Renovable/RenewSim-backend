package com.renewsim.backend.simulation.logic;

import com.renewsim.backend.simulation.dto.SimulationRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class SimulationCalculator {

    public double calculateEnergyGenerated(SimulationRequestDTO dto) {
        double irradiance = getIrradiance(dto);
        double efficiency = getEfficiency(dto.getEnergyType());
        return irradiance * efficiency * dto.getProjectSize() * 365;
    }

    public double calculateEstimatedSavings(double energyGenerated) {
        return energyGenerated * 0.2;
    }

    public double calculateROI(double budget, double estimatedSavings) {
        return estimatedSavings > 0 ? budget / estimatedSavings : 0;
    }

    private double getIrradiance(SimulationRequestDTO dto) {
        return switch (dto.getEnergyType().toLowerCase()) {
            case "solar" -> dto.getClimate().getIrradiance();
            case "wind" -> dto.getClimate().getWind();
            case "hydro" -> dto.getClimate().getHydrology();
            default -> throw new IllegalArgumentException("Tipo de energía no reconocido.");
        };
    }

    private double getEfficiency(String energyType) {
        return switch (energyType.toLowerCase()) {
            case "solar" -> 0.18;
            case "wind" -> 0.40;
            case "hydro" -> 0.50;
            default -> throw new IllegalArgumentException("Tipo de energía no reconocido.");
        };
    }
}

