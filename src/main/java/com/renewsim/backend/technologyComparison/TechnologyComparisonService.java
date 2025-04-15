package com.renewsim.backend.technologyComparison;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TechnologyComparisonService {

    private final TechnologyComparisonRepository repository;

    public TechnologyComparisonService(TechnologyComparisonRepository repository) {
        this.repository = repository;
    }
  
    public List<TechnologyComparison> getAllTechnologies() {
        return repository.findAll();
    }

    public List<TechnologyComparison> getTechnologiesByEnergyType(String energyType) {
        return repository.findByEnergyType(energyType);
    }

    public Optional<TechnologyComparison> getTechnologyById(Long id) {
        return repository.findById(id);
    }

    public TechnologyComparison addTechnology(TechnologyComparison technology) {
        if (repository.existsByTechnologyName(technology.getTechnologyName())) {
            throw new IllegalArgumentException("Technology with this name already exists.");
        }
        return repository.save(technology);
    }

    public void deleteTechnology(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Technology with ID " + id + " does not exist.");
        }
        repository.deleteById(id);
    }
}

