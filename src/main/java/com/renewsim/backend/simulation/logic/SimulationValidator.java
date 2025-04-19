package com.renewsim.backend.simulation.logic;

import com.renewsim.backend.simulation.dto.SimulationRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class SimulationValidator {

    public void validate(SimulationRequestDTO dto) {
        if (dto.getProjectSize() <= 0 || dto.getProjectSize() > 500) {
            throw new IllegalArgumentException("El tamaño del proyecto debe estar entre 1 y 500 kW.");
        }
    
        if (dto.getBudget() <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser mayor que cero.");
        }
    
        if (dto.getEnergyConsumption() < 50 || dto.getEnergyConsumption() > 100000) {
            throw new IllegalArgumentException("El consumo energético debe estar entre 50 y 100 000 kWh/mes.");
        }
    
        double estimatedCostPerKW = 1000.0;
        double maxAllowedSize = dto.getBudget() / estimatedCostPerKW;
    
        if (dto.getProjectSize() > maxAllowedSize) {
            throw new IllegalArgumentException(
                "Con un presupuesto de " + dto.getBudget() + " €, el tamaño máximo recomendado es " +
                Math.floor(maxAllowedSize) + " kW. Ajusta tu proyecto o incrementa el presupuesto."
            );
        }
    }
    
}
