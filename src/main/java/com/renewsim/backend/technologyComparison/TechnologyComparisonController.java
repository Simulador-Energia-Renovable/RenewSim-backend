package com.renewsim.backend.technologyComparison;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.renewsim.backend.simulation.Simulation;
import com.renewsim.backend.simulation.SimulationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/technologies")
public class TechnologyComparisonController {

    private final TechnologyComparisonService service;
    private final SimulationService simulationService;


    @Autowired
    public TechnologyComparisonController(TechnologyComparisonService service, SimulationService simulationService) {
        this.service = service;
        this.simulationService = simulationService;
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
                entity.getTechnologyName(),
                entity.getEfficiency(),
                entity.getInstallationCost(),
                entity.getMaintenanceCost(),
                entity.getEnvironmentalImpact(),
                entity.getCo2Reduction(),
                entity.getEnergyProduction());
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
                dto.getEnergyProduction());
               
    }

    @GetMapping("/simulation/{simulationId}")
    @PreAuthorize("hasAuthority('SCOPE_read:simulations')")
    public ResponseEntity<List<TechnologyComparisonResponseDTO>> getTechnologiesBySimulation(
            @PathVariable Long simulationId) {
        Simulation simulation = simulationService.getSimulationById(simulationId);
        List<TechnologyComparisonResponseDTO> dtos = simulation.getTechnologies().stream()
                .map(tech -> new TechnologyComparisonResponseDTO(
                        tech.getTechnologyName(),
                        tech.getEfficiency(),
                        tech.getInstallationCost(),
                        tech.getMaintenanceCost(),
                        tech.getEnvironmentalImpact(),
                        tech.getCo2Reduction(),
                        tech.getEnergyProduction()))
                .toList();

        return ResponseEntity.ok(dtos);
    }

}
