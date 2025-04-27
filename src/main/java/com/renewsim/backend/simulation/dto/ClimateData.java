package com.renewsim.backend.simulation.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ClimateData {
    private double irradiance;
    private double wind;
    private double hydrology;
}

