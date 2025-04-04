package com.renewsim.backend.technologyComparison;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<TechnologyComparisonResponseDTO>> getAllTechnologies() {
        List<TechnologyComparisonResponseDTO> responseList = service.getAllTechnologies()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    // Obtener una tecnología por ID
    @GetMapping("/{id}")
    public ResponseEntity<TechnologyComparisonResponseDTO> getTechnologyById(@PathVariable Long id) {
        return service.getTechnologyById(id)
                .map(this::mapToResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Agregar una nueva tecnología
    @PostMapping
    public ResponseEntity<TechnologyComparisonResponseDTO> addTechnology(
            @Valid @RequestBody TechnologyComparisonRequestDTO requestDTO) {
        try {
            TechnologyComparison technology = mapToEntity(requestDTO);
            TechnologyComparison savedTechnology = service.addTechnology(technology);
            return ResponseEntity.ok(mapToResponseDTO(savedTechnology));
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

    // Mapper de Entity a ResponseDTO
    private TechnologyComparisonResponseDTO mapToResponseDTO(TechnologyComparison entity) {
        return new TechnologyComparisonResponseDTO(
                entity.getId(),
                entity.getTechnologyName(),
                entity.getEfficiency(),
                entity.getInstallationCost(),
                entity.getMaintenanceCost(),
                entity.getEnvironmentalImpact(),
                entity.getCo2Reduction(),
                entity.getEnergyProduction()
        );
    }

    // Mapper de RequestDTO a Entity
    private TechnologyComparison mapToEntity(TechnologyComparisonRequestDTO dto) {
        return new TechnologyComparison(
                dto.getTechnologyName(),
                dto.getEfficiency(),
                dto.getInstallationCost(),
                dto.getMaintenanceCost(),
                dto.getEnvironmentalImpact(),
                dto.getCo2Reduction(),
                dto.getEnergyProduction()
        );
    }
}
