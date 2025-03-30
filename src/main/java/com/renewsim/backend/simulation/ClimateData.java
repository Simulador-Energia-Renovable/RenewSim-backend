package com.renewsim.backend.simulation;

public class ClimateData {
    private double irradiance;    
    private double wind;
    private double hydrology;

    public double getWind() {
        return wind;
    }

    public void setWind(double wind) {
        this.wind = wind;
    }
  

    public double getHydrology() {
        return hydrology;
    }

    public void setHydrology(double hydrology) {
        this.hydrology = hydrology;
    }

    public double getIrradiance() {
        return irradiance;
    }

    public void setIrradiance(double irradiance) {
        this.irradiance = irradiance;
    }
}

