package com.renewsim.backend.simulation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NormalizedTechnologyDTO {
    private String technologyName;
    private double normalizedCo2Reduction;
    private double normalizedEnergyProduction;
    private double normalizedInstallationCost;
    private double normalizedEfficiency;
    private double score;
}
