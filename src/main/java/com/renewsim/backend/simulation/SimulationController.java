package com.renewsim.backend.simulation;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.renewsim.backend.technologyComparison.TechnologyComparisonResponseDTO;

@RestController
@RequestMapping("/api/v1/simulation")
@CrossOrigin(origins = "http://localhost:5173")
public class SimulationController {

    private final SimulationUseCase simulationUseCase;

    public SimulationController(SimulationUseCase simulationUseCase) {
        this.simulationUseCase = simulationUseCase;
    }

    //Simulate new project
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_write:simulations')")
    public ResponseEntity<SimulationResponseDTO> simulate(@RequestBody SimulationRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SimulationResponseDTO result = simulationUseCase.simulateAndSave(dto, username);
        return ResponseEntity.ok(result);
    }

    //Get user simulations history
    @GetMapping("/user")
    @PreAuthorize("hasAuthority('SCOPE_read:simulations')")
    public ResponseEntity<List<SimulationHistoryDTO>> getUserSimulations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SimulationHistoryDTO> simulationHistoryList = simulationUseCase.getUserSimulationHistoryDTOs(username);
        return ResponseEntity.ok(simulationHistoryList);
    }

    //Get global simulation history (if needed, same as user-specific for now)
    @GetMapping("/history")
    @PreAuthorize("hasAuthority('SCOPE_read:simulations')")
    public ResponseEntity<List<SimulationHistoryDTO>> getSimulationHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(simulationUseCase.getUserSimulationHistoryDTOs(username));
    }

    // Get technologies for specific simulation
    @GetMapping("/{simulationId}/technologies")
    @PreAuthorize("hasAuthority('SCOPE_read:simulations')")
    public ResponseEntity<List<TechnologyComparisonResponseDTO>> getTechnologiesForSimulation(
            @PathVariable Long simulationId) {
        List<TechnologyComparisonResponseDTO> technologies =
                simulationUseCase.getTechnologiesForSimulation(simulationId);
        return ResponseEntity.ok(technologies);
    }

    //Delete all simulations for current user
    @DeleteMapping("/user")
    @PreAuthorize("hasAuthority('SCOPE_write:simulations')")
    public ResponseEntity<Map<String, String>> deleteUserSimulations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        simulationUseCase.deleteUserSimulations(username);
        return ResponseEntity.ok(Map.of("message", "User simulations deleted successfully"));
    }
}

