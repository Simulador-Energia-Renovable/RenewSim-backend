package com.renewsim.backend.technologyComparison;

import com.renewsim.backend.simulation.Simulation;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "technology_comparisons")
public class TechnologyComparison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "technology_name", nullable = false)
    private String technologyName;

    @Column(nullable = false)
    private Double efficiency;

    @Column(name = "installation_cost", nullable = false)
    private Double installationCost;

    @Column(name = "maintenance_cost", nullable = false)
    private Double maintenanceCost;

    @Column(name = "environmental_impact")
    private String environmentalImpact;

    @Column(name = "co2_reduction")
    private Double co2Reduction;

    @Column(name = "energy_production")
    private Double energyProduction;

    @Column(name = "energy_type", nullable = false)
    private String energyType;

    @ManyToMany(mappedBy = "technologies")
    private List<Simulation> simulations = new ArrayList<>();

    public TechnologyComparison() {
    }

    public TechnologyComparison(String technologyName, Double efficiency,
            Double installationCost, Double maintenanceCost,
            String environmentalImpact, Double co2Reduction,
            Double energyProduction, String energyType) {
        this.technologyName = technologyName;
        this.efficiency = efficiency;
        this.installationCost = installationCost;
        this.maintenanceCost = maintenanceCost;
        this.environmentalImpact = environmentalImpact;
        this.co2Reduction = co2Reduction;
        this.energyProduction = energyProduction;
        this.energyType = energyType;
    }
    public Long getId() {
        return id;
    }

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

    public List<Simulation> getSimulations() {
        return simulations;
    }

    public void setSimulations(List<Simulation> simulations) {
        this.simulations = simulations;
    }

    public String getEnergyType() {
        return energyType;
    }

    public void setEnergyType(String energyType) {
        this.energyType = energyType;
    }

    @Override
    public String toString() {
        return "TechnologyComparison{" +
                "id=" + id +
                ", technologyName='" + technologyName + '\'' +
                ", efficiency=" + efficiency +
                ", installationCost=" + installationCost +
                ", maintenanceCost=" + maintenanceCost +
                ", environmentalImpact='" + environmentalImpact + '\'' +
                ", co2Reduction=" + co2Reduction +
                ", energyProduction=" + energyProduction +
                ", energyType='" + energyType + '\'' +
                '}';
    }
}
