package com.renewsim.backend.technologyComparison.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TechnologyComparisonRequestDTO {

    @NotBlank(message = "Technology name is required")
    private String technologyName;

    @NotNull(message = "Efficiency is required")
    @Positive(message = "Efficiency must be positive")
    private Double efficiency;

    @NotNull(message = "Installation cost is required")
    @Positive(message = "Installation cost must be positive")
    private Double installationCost;

    @NotNull(message = "Maintenance cost is required")
    @Positive(message = "Maintenance cost must be positive")
    private Double maintenanceCost;

    private String environmentalImpact;

    private Double co2Reduction;

    private Double energyProduction;

    @NotNull(message = "El tipo de energía es obligatorio")
    private String energyType;
}


