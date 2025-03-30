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

        double irradiance = 0;
        double efficiency  = 0;

        switch (dto.getEnergyType().toLowerCase()) {
            case "solar" -> {
                irradiance = dto.getClimate().getIrradiance(); // kWh/m²/día
                efficiency  = 0.18; // Eficiencia típica de panel solar
                System.out.println("☀️ Irradiancia recibida: " + irradiance);
            }
            case "wind" -> {
                irradiance = dto.getClimate().getWind(); // velocidad promedio del viento
                efficiency  = 0.40; // Eficiencia estimada de turbina
            }
            case "hydro" -> {
                irradiance = dto.getClimate().getHydrology(); // índice arbitrario
                efficiency  = 0.50;
            }
            default -> {
                irradiance = 0;
                efficiency  = 0;
            }
        }

        // Cálculo de energía generada anual
        double energyGenerated = irradiance * efficiency  * dto.getProjectSize() * 365;

        // Ahorro y retorno de inversión
        double ahorro = energyGenerated * 0.2;
        double roi = ahorro > 0 ? dto.getBudget() / ahorro : 0;

        // Guardar simulación
        Simulation simulation = new Simulation();
        simulation.setLocation(dto.getLocation());
        simulation.setEnergyType(dto.getEnergyType());
        simulation.setProjectSize(dto.getProjectSize());
        simulation.setBudget(dto.getBudget());
        simulation.setEnergyGenerated(energyGenerated);
        simulation.setEstimatedSavings(ahorro);
        simulation.setReturnOnInvestment(roi);
        simulation.setUser(user);

        simulationRepository.save(simulation);

        return new SimulationResponseDTO(energyGenerated, ahorro, roi);
    }

    public List<Simulation> getUserSimulations(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
                
        return simulationRepository.findAllByUser(user);
    }
}
