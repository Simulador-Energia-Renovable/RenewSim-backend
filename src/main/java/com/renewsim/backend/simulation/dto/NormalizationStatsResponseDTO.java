package com.renewsim.backend.simulation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NormalizationStatsResponseDTO {
    private double minCo2;
    private double maxCo2;
    private double minEnergyProduction;
    private double maxEnergyProduction;
    private double minInstallationCost;
    private double maxInstallationCost;
    private double minEfficiency;
    private double maxEfficiency;
}
