package com.renewsim.backend.simulation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode
public class SimulationRequestDTO {
    private String location;
    private String energyType;
    private double projectSize;
    private double budget;
    private ClimateData climate;
    private double energyConsumption;
    
}
