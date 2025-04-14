package com.renewsim.backend.technologyComparison.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TechnologyComparisonResponseDTO {

    private String technologyName;
    private Double efficiency;
    private Double installationCost;
    private Double maintenanceCost;
    private String environmentalImpact;
    private Double co2Reduction;
    private Double energyProduction;
    private String energyType; 
}

