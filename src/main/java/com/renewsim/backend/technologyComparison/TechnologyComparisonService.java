package com.renewsim.backend.technologyComparison;

import java.util.List;
import java.util.Optional;

public interface TechnologyComparisonService {

    List<TechnologyComparison> getAllTechnologies();

    Optional<TechnologyComparison> getTechnologyById(Long id);

    TechnologyComparison addTechnology(TechnologyComparison technology);

    void deleteTechnology(Long id);

    List<TechnologyComparison> getTechnologiesByEnergyType(String energyType);

    TechnologyComparison updateTechnology(Long id, TechnologyComparison updatedData);

}
