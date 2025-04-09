package com.renewsim.backend.simulation;

import org.springframework.stereotype.Component;

import com.renewsim.backend.simulation.dto.SimulationHistoryDTO;

@Component
public class SimulationMapper {   

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
    }
    


