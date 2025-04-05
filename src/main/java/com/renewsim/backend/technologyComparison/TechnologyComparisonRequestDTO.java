package com.renewsim.backend.technologyComparison;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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

    // Getters and Setters

    public String getTechnologyName() {
        return technologyName;
    }

    public void setTechnologyName(String technologyName) {
        this.technologyName = technologyName;
    }

    public Double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Double efficiency) {
        this.efficiency = efficiency;
    }

    public Double getInstallationCost() {
        return installationCost;
    }

    public void setInstallationCost(Double installationCost) {
        this.installationCost = installationCost;
    }

    public Double getMaintenanceCost() {
        return maintenanceCost;
    }

    public void setMaintenanceCost(Double maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }

    public String getEnvironmentalImpact() {
        return environmentalImpact;
    }

    public void setEnvironmentalImpact(String environmentalImpact) {
        this.environmentalImpact = environmentalImpact;
    }

    public Double getCo2Reduction() {
        return co2Reduction;
    }

    public void setCo2Reduction(Double co2Reduction) {
        this.co2Reduction = co2Reduction;
    }

    public Double getEnergyProduction() {
        return energyProduction;
    }

    public void setEnergyProduction(Double energyProduction) {
        this.energyProduction = energyProduction;
    }

    public String getEnergyType() {
        return energyType;
    }

    public void setEnergyType(String energyType) {
        this.energyType = energyType;
    }
}

