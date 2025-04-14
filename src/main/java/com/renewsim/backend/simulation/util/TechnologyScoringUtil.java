package com.renewsim.backend.simulation.util;

import com.renewsim.backend.simulation.dto.NormalizationStatsDTO;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;

import java.util.List;

public class TechnologyScoringUtil {

    private TechnologyScoringUtil() {      
    }

    public static NormalizationStatsDTO calculateNormalizationStats(List<TechnologyComparisonResponseDTO> techList) {
        return NormalizationStatsDTO.builder()
                .minCo2(techList.stream().mapToDouble(TechnologyComparisonResponseDTO::getCo2Reduction).min().orElse(0))
                .maxCo2(techList.stream().mapToDouble(TechnologyComparisonResponseDTO::getCo2Reduction).max().orElse(1))
                .minEnergy(techList.stream().mapToDouble(TechnologyComparisonResponseDTO::getEnergyProduction).min().orElse(0))
                .maxEnergy(techList.stream().mapToDouble(TechnologyComparisonResponseDTO::getEnergyProduction).max().orElse(1))
                .minCost(techList.stream().mapToDouble(TechnologyComparisonResponseDTO::getInstallationCost).min().orElse(0))
                .maxCost(techList.stream().mapToDouble(TechnologyComparisonResponseDTO::getInstallationCost).max().orElse(1))
                .minEfficiency(techList.stream().mapToDouble(TechnologyComparisonResponseDTO::getEfficiency).min().orElse(0))
                .maxEfficiency(techList.stream().mapToDouble(TechnologyComparisonResponseDTO::getEfficiency).max().orElse(1))
                .build();
    }

    public static double calculateScoreDynamic(TechnologyComparisonResponseDTO tech, NormalizationStatsDTO stats) {
        double co2 = tech.getCo2Reduction();
        double energy = tech.getEnergyProduction();
        double cost = tech.getInstallationCost();
        double eff = tech.getEfficiency();

        double normalizedCo2 = normalize(co2, stats.getMinCo2(), stats.getMaxCo2());
        double normalizedEnergy = normalize(energy, stats.getMinEnergy(), stats.getMaxEnergy());
        double normalizedCost = normalize(cost, stats.getMinCost(), stats.getMaxCost());
        double normalizedEff = normalize(eff, stats.getMinEfficiency(), stats.getMaxEfficiency());

        return (normalizedCo2 * 0.25) +
               (normalizedEnergy * 0.30) +
               (normalizedEff * 0.25) -
               (normalizedCost * 0.20);
    }

    public static double normalize(double value, double min, double max) {
        if (max - min == 0) {
            return 0.0;
        }
        return (value - min) / (max - min);
    }
}
