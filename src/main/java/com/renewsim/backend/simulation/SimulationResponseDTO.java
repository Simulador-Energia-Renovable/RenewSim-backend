package com.renewsim.backend.simulation;
import java.time.LocalDateTime;
import java.util.List;

import com.renewsim.backend.technologyComparison.TechnologyComparisonResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimulationResponseDTO {
    private Long simulationId;
    private double energyGenerated;
    private double estimatedSavings;
    private double returnOnInvestment;
    private LocalDateTime timestamp; 
    private List<TechnologyComparisonResponseDTO> technologies;
    private String recommendedTechnology;
}




