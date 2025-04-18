package com.renewsim.backend.technologyComparison;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TechnologyDataInitializer {

    private final TechnologyComparisonRepository repository;

    @PostConstruct
    public void init() {
        if (repository.count() > 0) return;

        List<TechnologyComparison> initialTechnologies = List.of(
            new TechnologyComparison("Panel Solar", 0.18, 7780.0, 150.0, "Bajo", 1500.0, 4500.0, "solar"),
            new TechnologyComparison("Turbina Eólica", 0.35, 12000.0, 200.0, "Moderado", 2000.0, 7000.0, "eólica"),
            new TechnologyComparison("Micro Hidroeléctrica", 0.85, 15000.0, 300.0, "Bajo", 1000.0, 6000.0, "hidroeléctrica"),
            new TechnologyComparison("Digestor de Biogás", 0.25, 10000.0, 250.0, "Variable", 1200.0, 5000.0, "biomasa"),
            new TechnologyComparison("Planta Geotérmica", 0.15, 18000.0, 500.0, "Bajo", 1000.0, 3500.0, "geotérmica"),
            new TechnologyComparison("Planta Mareomotriz", 0.30, 25000.0, 400.0, "Bajo", 2000.0, 6500.0, "oceánica")
        );

        repository.saveAll(initialTechnologies);
        System.out.println("✅ Tecnologías iniciales con datos reales cargadas en la base de datos.");
    }
}

