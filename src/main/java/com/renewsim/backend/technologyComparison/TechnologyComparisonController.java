package com.renewsim.backend.technologyComparison;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.renewsim.backend.simulation.Simulation;
import com.renewsim.backend.simulation.SimulationService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/technologies")
public class TechnologyComparisonController {

    private final TechnologyComparisonService service;
    private final SimulationService simulationService;
    private final TechnologyComparisonMapper mapper;

    @Autowired
    public TechnologyComparisonController(TechnologyComparisonService service, SimulationService simulationService,
            TechnologyComparisonMapper mapper) {
        this.service = service;
        this.simulationService = simulationService;
        this.mapper = mapper;
    }

    // Obtener todas las tecnologías
    @GetMapping
    public ResponseEntity<List<TechnologyComparisonResponseDTO>> getAllTechnologies() {
        List<TechnologyComparisonResponseDTO> responseList = service.getAllTechnologies()
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    // Obtener una tecnología por ID
    @GetMapping("/{id}")
    public ResponseEntity<TechnologyComparisonResponseDTO> getTechnologyById(@PathVariable Long id) {
        return service.getTechnologyById(id)
                .map(mapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Agregar una nueva tecnología
    @PostMapping
    public ResponseEntity<TechnologyComparisonResponseDTO> addTechnology(
            @Valid @RequestBody TechnologyComparisonRequestDTO requestDTO) {
        try {
            TechnologyComparison technology = mapper.toEntity(requestDTO);
            TechnologyComparison savedTechnology = service.addTechnology(technology);
            return ResponseEntity.ok(mapper.toResponseDTO(savedTechnology));
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

    // Obtener tecnologías asociadas a una simulación
    @GetMapping("/simulation/{simulationId}")
    @PreAuthorize("hasAuthority('SCOPE_read:simulations')")
    public ResponseEntity<List<TechnologyComparisonResponseDTO>> getTechnologiesBySimulation(
            @PathVariable Long simulationId) {
        Simulation simulation = simulationService.getSimulationById(simulationId);
        List<TechnologyComparisonResponseDTO> dtos = simulation.getTechnologies().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
