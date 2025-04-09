package com.renewsim.backend.simulation.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NormalizationStatsDTO {
    private double minCo2;
    private double maxCo2;
    private double minEnergy;
    private double maxEnergy;
    private double minCost;
    private double maxCost;
    private double minEfficiency;
    private double maxEfficiency;
}

