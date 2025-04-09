package com.renewsim.backend.technologyComparison;

import org.springframework.stereotype.Component;

@Component
public class TechnologyComparisonMapper {

    public TechnologyComparisonResponseDTO toResponseDTO(TechnologyComparison entity) {
        return TechnologyComparisonResponseDTO.builder()
                .technologyName(entity.getTechnologyName())
                .efficiency(entity.getEfficiency())
                .installationCost(entity.getInstallationCost())
                .maintenanceCost(entity.getMaintenanceCost())
                .environmentalImpact(entity.getEnvironmentalImpact())
                .co2Reduction(entity.getCo2Reduction())
                .energyProduction(entity.getEnergyProduction())
                .build();
    }

    public TechnologyComparison toEntity(TechnologyComparisonRequestDTO dto) {
        return new TechnologyComparison(
                dto.getTechnologyName(),
                dto.getEfficiency(),
                dto.getInstallationCost(),
                dto.getMaintenanceCost(),
                dto.getEnvironmentalImpact(),
                dto.getCo2Reduction(),
                dto.getEnergyProduction(),
                dto.getEnergyType()
        );
    }
}

