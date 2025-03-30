package com.renewsim.backend.simulation;

public class SimulationRequestDTO {
    private String location;
    private String energyType;
    private double projectSize;
    private double budget;
    private ClimateData climate;
    private double energyConsumption;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getEnergyType() {
        return energyType;
    }

    public void setEnergyType(String energyType) {
        this.energyType = energyType;
    }
  

    public double getProjectSize() {
        return projectSize;
    }

    public void setProjectSize(double projectSize) {
        this.projectSize = projectSize;
    }


    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }


    public ClimateData getClimate() {
        return climate;
    }

    public void setClimate(ClimateData climate) {
        this.climate = climate;
    }
    public double getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }
}
