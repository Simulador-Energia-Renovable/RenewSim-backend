package com.renewsim.backend.simulation;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/simulacion")
@CrossOrigin(origins = "http://localhost:5173") 
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SimulationResponseDTO> simulate(@RequestBody SimulationRequestDTO dto) {
        SimulationResponseDTO result = simulationService.simulateAndSave(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/mis-simulaciones")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Simulation>> getUserSimulations() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<Simulation> simulations = simulationService.getUserSimulations(username);
        return ResponseEntity.ok(simulations);
    }
}
