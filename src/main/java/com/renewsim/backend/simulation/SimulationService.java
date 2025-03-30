package com.renewsim.backend.simulation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.cache.annotation.Cacheable;

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

        // Validaciones básicas
        if (dto.getProjectSize() <= 0 || dto.getProjectSize() > 500) {
            throw new IllegalArgumentException("El tamaño del proyecto debe ser entre 1 y 500 m².");
        }

        if (dto.getBudget() <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser mayor que cero.");
        }

        if (dto.getEnergyConsumption() < 50 || dto.getEnergyConsumption() > 100000) {
            throw new IllegalArgumentException("El consumo energético debe estar entre 50 y 100000 kWh/mes.");
        }

        SimulationResponseDTO resultado = calculateSimulation(dto);

        // Guardar simulación
        Simulation simulation = new Simulation();
        simulation.setLocation(dto.getLocation());
        simulation.setEnergyType(dto.getEnergyType());
        simulation.setProjectSize(dto.getProjectSize());
        simulation.setBudget(dto.getBudget());
        simulation.setEnergyConsumption(dto.getEnergyConsumption());
        simulation.setEnergyGenerated(resultado.getEnergyGenerated());
        simulation.setEstimatedSavings(resultado.getEstimatedSavings());
        simulation.setReturnOnInvestment(resultado.getReturnOnInvestment());
        simulation.setUser(user);

        simulationRepository.save(simulation);

        return resultado;
    }

    @Cacheable(value = "simulations", key = "#dto.hashCode()")
    public SimulationResponseDTO calculateSimulation(SimulationRequestDTO dto) {
        double irradiance = 0;
        double efficiency = 0;

        switch (dto.getEnergyType().toLowerCase()) {
            case "solar" -> {
                irradiance = dto.getClimate().getIrradiance();
                if (irradiance <= 0 || irradiance > 2000)
                    throw new IllegalArgumentException("Irradiancia solar inválida.");
                efficiency = 0.18;
            }
            case "wind" -> {
                irradiance = dto.getClimate().getWind();
                if (irradiance <= 0 || irradiance > 20)
                    throw new IllegalArgumentException("Velocidad del viento inválida.");
                efficiency = 0.40;
            }
            case "hydro" -> {
                irradiance = dto.getClimate().getHydrology();
                if (irradiance <= 0 || irradiance > 100)
                    throw new IllegalArgumentException("Índice hidrológico inválido.");
                efficiency = 0.50;
            }
            default -> throw new IllegalArgumentException("Tipo de energía no reconocido.");
        }

        double energyGenerated = irradiance * efficiency * dto.getProjectSize() * 365;
        double ahorro = energyGenerated * 0.2;
        double roi = ahorro > 0 ? dto.getBudget() / ahorro : 0;

        return new SimulationResponseDTO(energyGenerated, ahorro, roi);
    }

    public List<Simulation> getUserSimulations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return simulationRepository.findAllByUser(user);
    }
}