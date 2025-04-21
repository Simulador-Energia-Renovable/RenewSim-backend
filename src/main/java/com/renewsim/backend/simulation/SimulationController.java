package com.renewsim.backend.simulation;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.renewsim.backend.simulation.dto.*;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;

@RestController
@RequestMapping("/api/v1/simulation")
@CrossOrigin(origins = "http://localhost:5173")
public class SimulationController {

    private final SimulationUseCase simulationUseCase;

    public SimulationController(SimulationUseCase simulationUseCase) {
        this.simulationUseCase = simulationUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_write:simulations')")
    public ResponseEntity<SimulationResponseDTO> simulate(@RequestBody SimulationRequestDTO dto) {
        String username = getCurrentUsername();
        SimulationResponseDTO result = simulationUseCase.simulateAndSave(dto, username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('SCOPE_read:simulations')")
    public ResponseEntity<List<SimulationHistoryDTO>> getUserSimulations() {
        String username = getCurrentUsername();
        return ResponseEntity.ok(simulationUseCase.getUserSimulationHistoryDTOs(username));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('SCOPE_read:simulations')")
    public ResponseEntity<List<SimulationHistoryDTO>> getSimulationHistory() {
        String username = getCurrentUsername();
        return ResponseEntity.ok(simulationUseCase.getUserSimulationHistoryDTOs(username));
    }

    @GetMapping("/{simulationId}/technologies")
    @PreAuthorize("hasAuthority('SCOPE_read:simulations')")
    public ResponseEntity<List<TechnologyComparisonResponseDTO>> getTechnologiesForSimulation(
            @PathVariable Long simulationId) {
        return ResponseEntity.ok(simulationUseCase.getTechnologiesForSimulation(simulationId));
    }

    @DeleteMapping("/user")
    @PreAuthorize("hasAuthority('SCOPE_write:simulations')")
    public ResponseEntity<Map<String, String>> deleteUserSimulations() {
        String username = getCurrentUsername();
        simulationUseCase.deleteUserSimulations(username);
        return ResponseEntity.ok(Map.of("message", "User simulations deleted successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_write:simulations')")
    public ResponseEntity<Void> deleteSimulation(@PathVariable Long id) {
        simulationUseCase.deleteSimulationById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/normalization-stats")
    @PreAuthorize("hasAuthority('SCOPE_read:simulations')")
    public ResponseEntity<NormalizationStatsResponseDTO> getNormalizationStats() {
        NormalizationStatsDTO stats = simulationUseCase.getCurrentNormalizationStats();
        NormalizationStatsResponseDTO response = NormalizationStatsResponseDTO.builder()
                .minCo2(stats.getMinCo2())
                .maxCo2(stats.getMaxCo2())
                .minEnergyProduction(stats.getMinEnergy())
                .maxEnergyProduction(stats.getMaxEnergy())
                .minInstallationCost(stats.getMinCost())
                .maxInstallationCost(stats.getMaxCost())
                .minEfficiency(stats.getMinEfficiency())
                .maxEfficiency(stats.getMaxEfficiency())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/normalized-technologies")
    @PreAuthorize("hasAuthority('SCOPE_read:simulations')")
    public ResponseEntity<List<NormalizedTechnologyDTO>> getNormalizedTechnologies() {
        return ResponseEntity.ok(simulationUseCase.getNormalizedTechnologies());
    }

    @GetMapping("/technologies/global")
    @PreAuthorize("hasAuthority('SCOPE_read:simulations')")
    public ResponseEntity<List<TechnologyComparisonResponseDTO>> getAllTechnologies() {
        return ResponseEntity.ok(simulationUseCase.getAllTechnologies());
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
