package com.renewsim.backend.simulation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;

@Service
public class SimulationService {

    private final SimulationRepository simulationRepository;
    private final UserRepository userRepository;

    public SimulationService(SimulationRepository simulationRepository, UserRepository userRepository) {
        this.simulationRepository = simulationRepository;
        this.userRepository = userRepository;
    }

    public SimulationResponseDTO simulateAndSave(SimulationRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        double eficiencia = 0.75;
        double irradiancia = switch (dto.getEnergyType()) {
            case "solar" -> dto.getClima().getIrradiancia();
            case "wind" -> dto.getClima().getViento();
            case "hydro" -> dto.getClima().getHidrologia();
            default -> 0;
        };

        double energiaGenerada = irradiancia * eficiencia * dto.getProjectSize() * 365;
        double ahorro = energiaGenerada * 0.2;
        double roi = ahorro > 0 ? dto.getBudget() / ahorro : 0;

        Simulation simulation = new Simulation();
        simulation.setLocation(dto.getLocation());
        simulation.setEnergyType(dto.getEnergyType());
        simulation.setProjectSize(dto.getProjectSize());
        simulation.setBudget(dto.getBudget());
        simulation.setEnergiaGenerada(energiaGenerada);
        simulation.setAhorroEstimado(ahorro);
        simulation.setRetornoInversion(roi);
        simulation.setUser(user);

        simulationRepository.save(simulation);

        return new SimulationResponseDTO(energiaGenerada, ahorro, roi);
    }

    public List<Simulation> getUserSimulations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return simulationRepository.findAllByUser(user);
    }
}

