package com.renewsim.backend.simulation.logic;

import com.renewsim.backend.simulation.dto.NormalizationStatsDTO;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

import static com.renewsim.backend.simulation.util.TechnologyScoringUtil.*;

@Component
public class TechnologyRecommender {

    public String recommendTechnology(List<TechnologyComparisonResponseDTO> technologies, NormalizationStatsDTO stats) {
        return technologies.stream()
                .max(Comparator.comparingDouble(tech -> calculateScoreDynamic(tech, stats)))
                .map(TechnologyComparisonResponseDTO::getTechnologyName)
                .orElse("No recommendation available");
    }
}

