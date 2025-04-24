package com.renewsim.backend.technologyComparison;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    TechnologyComparison tech = repository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Technology not found"));

    if (!tech.getSimulations().isEmpty()) {
        String simulationsInfo = tech.getSimulations().stream()
            .map(sim -> "ID: " + sim.getId() + " - " + sim.getLocation())
            .collect(Collectors.joining("; "));

        throw new IllegalStateException("Tecnología asociada a simulaciones: " + simulationsInfo);
    }

    repository.deleteById(id);
}

    

    @Override
    public List<TechnologyComparison> getTechnologiesByEnergyType(String energyType) {
        return repository.findByEnergyType(energyType);
    }
}
