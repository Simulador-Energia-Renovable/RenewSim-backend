package com.renewsim.backend.simulation.logic;

import com.renewsim.backend.simulation.dto.SimulationRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class SimulationValidator {

    public void validate(SimulationRequestDTO dto) {
        if (dto.getProjectSize() <= 0 || dto.getProjectSize() > 500) {
            throw new IllegalArgumentException("El tamaño del proyecto debe ser entre 1 y 500 m².");
        }
        if (dto.getBudget() <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser mayor que cero.");
        }
        if (dto.getEnergyConsumption() < 50 || dto.getEnergyConsumption() > 100000) {
            throw new IllegalArgumentException("El consumo energético debe estar entre 50 y 100000 kWh/mes.");
        }
    }
}
