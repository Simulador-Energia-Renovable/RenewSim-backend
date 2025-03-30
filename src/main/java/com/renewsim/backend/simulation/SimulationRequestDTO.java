package com.renewsim.backend.simulation;

public class SimulationRequestDTO {
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    private String energyType;

    public String getEnergyType() {
        return energyType;
    }

    public void setEnergyType(String energyType) {
        this.energyType = energyType;
    }
    private double projectSize;

    public double getProjectSize() {
        return projectSize;
    }

    public void setProjectSize(double projectSize) {
        this.projectSize = projectSize;
    }
    private double budget;

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
    private ClimateData clima;

    public ClimateData getClima() {
        return clima;
    }

    public void setClima(ClimateData clima) {
        this.clima = clima;
    }
}
