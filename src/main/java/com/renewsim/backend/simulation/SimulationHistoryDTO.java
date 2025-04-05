package com.renewsim.backend.simulation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimulationHistoryDTO {
    private String location;
    private String energyType;
    private double energyGenerated;
    private double estimatedSavings;
    private double returnOnInvestment;
    private LocalDateTime timestamp;
}

