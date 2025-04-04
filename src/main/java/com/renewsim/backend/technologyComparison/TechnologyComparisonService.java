package com.renewsim.backend.technologyComparison;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TechnologyComparisonService {

    private final TechnologyComparisonRepository repository;

    @Autowired
    public TechnologyComparisonService(TechnologyComparisonRepository repository) {
        this.repository = repository;
    }

    // Obtener todas las tecnologías
    public List<TechnologyComparison> getAllTechnologies() {
        return repository.findAll();
    }

    // Obtener una tecnología por ID
    public Optional<TechnologyComparison> getTechnologyById(Long id) {
        return repository.findById(id);
    }

    // Agregar nueva tecnología
    public TechnologyComparison addTechnology(TechnologyComparison technology) {
        if (repository.existsByTechnologyName(technology.getTechnologyName())) {
            throw new IllegalArgumentException("Technology with this name already exists.");
        }
        return repository.save(technology);
    }

    // Eliminar una tecnología por ID
    public void deleteTechnology(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Technology with ID " + id + " does not exist.");
        }
        repository.deleteById(id);
    }
}

