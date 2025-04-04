package com.renewsim.backend.technologyComparison;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technologies")
public class TechnologyComparisonController {

    private final TechnologyComparisonService service;

    @Autowired
    public TechnologyComparisonController(TechnologyComparisonService service) {
        this.service = service;
    }

    // Obtener todas las tecnologías
    @GetMapping
    public ResponseEntity<List<TechnologyComparison>> getAllTechnologies() {
        return ResponseEntity.ok(service.getAllTechnologies());
    }

    // Obtener una tecnología por ID
    @GetMapping("/{id}")
    public ResponseEntity<TechnologyComparison> getTechnologyById(@PathVariable Long id) {
        return service.getTechnologyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Agregar una nueva tecnología
    @PostMapping
    public ResponseEntity<TechnologyComparison> addTechnology(@RequestBody TechnologyComparison technology) {
        try {
            TechnologyComparison savedTechnology = service.addTechnology(technology);
            return ResponseEntity.ok(savedTechnology);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Eliminar una tecnología por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTechnology(@PathVariable Long id) {
        try {
            service.deleteTechnology(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

