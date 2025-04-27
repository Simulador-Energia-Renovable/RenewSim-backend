package com.renewsim.backend.technologyComparison;

import com.renewsim.backend.simulation.Simulation;
import com.renewsim.backend.simulation.SimulationService;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonRequestDTO;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/technologies")
public class TechnologyComparisonController {

    private final TechnologyComparisonUseCase useCase;
    private final TechnologyComparisonService service;
    private final SimulationService simulationService;
    private final TechnologyComparisonMapper mapper;

    public TechnologyComparisonController(TechnologyComparisonUseCase useCase, TechnologyComparisonService service,
            SimulationService simulationService, TechnologyComparisonMapper mapper) {
        this.useCase = useCase;
        this.service = service;
        this.simulationService = simulationService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<TechnologyComparisonResponseDTO>> getAllTechnologies() {
        List<TechnologyComparisonResponseDTO> responseList = service.getAllTechnologies()
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechnologyComparisonResponseDTO> getTechnologyById(@PathVariable Long id) {
        return service.getTechnologyById(id)
                .map(mapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{energyType}")
    public ResponseEntity<List<TechnologyComparisonResponseDTO>> getTechnologiesByType(
            @PathVariable String energyType) {
        return ResponseEntity.ok(useCase.filterByType(energyType));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TechnologyComparisonResponseDTO> addTechnology(
            @Valid @RequestBody TechnologyComparisonRequestDTO requestDTO) {
        try {
            return ResponseEntity.ok(useCase.createTechnology(requestDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTechnology(@PathVariable Long id) {
        try {
            useCase.deleteTechnology(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TechnologyComparisonResponseDTO> updateTechnology(
            @PathVariable Long id,
            @Valid @RequestBody TechnologyComparisonRequestDTO requestDTO) {
        try {
            TechnologyComparisonResponseDTO updated = useCase.updateTechnology(id, requestDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
