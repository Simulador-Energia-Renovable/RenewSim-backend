package com.renewsim.backend.technologyComparison;

public class TechnologyComparisonResponseDTO {

    private Long id;
    private String technologyName;
    private Double efficiency;
    private Double installationCost;
    private Double maintenanceCost;
    private String environmentalImpact;
    private Double co2Reduction;
    private Double energyProduction;

    // Constructor
    public TechnologyComparisonResponseDTO(Long id, String technologyName, Double efficiency,
            Double installationCost, Double maintenanceCost,
            String environmentalImpact, Double co2Reduction,
            Double energyProduction) {
        this.id = id;
        this.technologyName = technologyName;
        this.efficiency = efficiency;
        this.installationCost = installationCost;
        this.maintenanceCost = maintenanceCost;
        this.environmentalImpact = environmentalImpact;
        this.co2Reduction = co2Reduction;
        this.energyProduction = energyProduction;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getTechnologyName() {
        return technologyName;
    }

    public Double getEfficiency() {
        return efficiency;
    }

    public Double getInstallationCost() {
        return installationCost;
    }

    public Double getMaintenanceCost() {
        return maintenanceCost;
    }

    public String getEnvironmentalImpact() {
        return environmentalImpact;
    }

    public Double getCo2Reduction() {
        return co2Reduction;
    }

    public Double getEnergyProduction() {
        return energyProduction;
    }
}
