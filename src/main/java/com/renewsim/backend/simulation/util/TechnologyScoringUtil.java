package com.renewsim.backend.simulation.util;

import com.renewsim.backend.simulation.dto.NormalizationStatsDTO;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;

import java.util.List;

/**
 * Clase utilitaria para calcular estadísticas de normalización  y puntuar tecnologías basadas en criterios energéticos.
 */
public class TechnologyScoringUtil {

    // Pesos configurables (pueden extraerse a properties o constantes)
    private static final double WEIGHT_CO2 = 0.25;
    private static final double WEIGHT_ENERGY = 0.30;
    private static final double WEIGHT_EFFICIENCY = 0.25;
    private static final double WEIGHT_COST = 0.20;

    private TechnologyScoringUtil() {
    }

    /**
     * Calcula los valores min y max de cada métrica a partir de una lista de tecnologías.
     */
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

    /**
     * Calcula una puntuación ponderada para una tecnología.
     */
    public static double calculateScoreDynamic(TechnologyComparisonResponseDTO tech, NormalizationStatsDTO stats) {
        double normalizedCo2 = normalize(tech.getCo2Reduction(), stats.getMinCo2(), stats.getMaxCo2());
        double normalizedEnergy = normalize(tech.getEnergyProduction(), stats.getMinEnergy(), stats.getMaxEnergy());
        double normalizedCost = normalize(tech.getInstallationCost(), stats.getMinCost(), stats.getMaxCost());
        double normalizedEff = normalize(tech.getEfficiency(), stats.getMinEfficiency(), stats.getMaxEfficiency());

        return (normalizedCo2 * WEIGHT_CO2) +
               (normalizedEnergy * WEIGHT_ENERGY) +
               (normalizedEff * WEIGHT_EFFICIENCY) -
               (normalizedCost * WEIGHT_COST);
    }

    /**
     * Normaliza un valor dentro de un rango definido.
     */
    public static double normalize(double value, double min, double max) {
        if (max - min == 0) {
            return 0.0;
        }
        return (value - min) / (max - min);
    }
}

