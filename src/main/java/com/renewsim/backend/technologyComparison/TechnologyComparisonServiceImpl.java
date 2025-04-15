package com.renewsim.backend.technologyComparison;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TechnologyComparisonServiceImpl implements TechnologyComparisonService {

    private final TechnologyComparisonRepository repository;

    public TechnologyComparisonServiceImpl(TechnologyComparisonRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TechnologyComparison> getAllTechnologies() {
        return repository.findAll();
    }

    @Override
    public Optional<TechnologyComparison> getTechnologyById(Long id) {
        return repository.findById(id);
    }

    @Override
    public TechnologyComparison addTechnology(TechnologyComparison technology) {
        if (repository.existsByTechnologyName(technology.getTechnologyName())) {
            throw new IllegalArgumentException("Technology with this name already exists.");
        }
        return repository.save(technology);
    }

    @Override
    public void deleteTechnology(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Technology with ID " + id + " does not exist.");
        }
        repository.deleteById(id);
    }

    @Override
    public List<TechnologyComparison> getTechnologiesByEnergyType(String energyType) {
        return repository.findByEnergyType(energyType);
    }
}
