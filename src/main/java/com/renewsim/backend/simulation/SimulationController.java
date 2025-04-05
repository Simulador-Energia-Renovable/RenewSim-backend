package com.renewsim.backend.simulation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renewsim.backend.technologyComparison.TechnologyComparisonResponseDTO;

@RestController
@RequestMapping("/api/v1/simulation")
@CrossOrigin(origins = "http://localhost:5173")
public class SimulationController {

    private final SimulationUseCase simulationUseCase;

    public SimulationController(SimulationUseCase simulationUseCase) {
        this.simulationUseCase = simulationUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_write: simualtion')")
    public ResponseEntity<SimulationResponseDTO> simulate(@RequestBody SimulationRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SimulationResponseDTO result = simulationUseCase.simulateAndSave(dto, username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user")
@PreAuthorize("hasAuthority('SCOPE_read: simulation')")
public ResponseEntity<List<SimulationHistoryDTO>> getUserSimulations() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    List<SimulationHistoryDTO> simulationHistoryList = simulationUseCase.getUserSimulationHistoryDTOs(username);
    return ResponseEntity.ok(simulationHistoryList);
}


    @GetMapping("/history")
    @PreAuthorize("hasAuthority('SCOPE_read:simualtion')")
    public ResponseEntity<List<SimulationHistoryDTO>> getSimulationHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(simulationUseCase.getUserSimulationHistoryDTOs(username));
    }

    @GetMapping("/{simulationId}/technologies")
    @PreAuthorize("hasAuthority('SCOPE_read: simualtion')")
    public ResponseEntity<List<TechnologyComparisonResponseDTO>> getTechnologiesForSimulation(
            @PathVariable Long simulationId) {

        List<TechnologyComparisonResponseDTO> technologies = simulationUseCase
                .getTechnologiesForSimulation(simulationId);
        return ResponseEntity.ok(technologies);
    }

}
