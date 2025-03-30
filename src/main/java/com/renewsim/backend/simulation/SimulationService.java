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
        double efficiency = 0;

        // 🛡️ Validaciones básicas
        if (dto.getProjectSize() <= 0 || dto.getProjectSize() > 500) {
            throw new IllegalArgumentException("El tamaño del proyecto debe ser entre 1 y 500 m².");
        }

        if (dto.getBudget() <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser mayor que cero.");
        }

        // 🌞 Selección de fuente
        switch (dto.getEnergyType().toLowerCase()) {
            case "solar" -> {
                irradiance = dto.getClimate().getIrradiance(); // kWh/m²/día
                if (irradiance <= 0 || irradiance > 2000) // 2000 kWh/m²/año es un límite razonable
                    throw new IllegalArgumentException("Irradiancia solar inválida.");

                efficiency = 0.18;
                System.out.println("☀️ Irradiancia recibida: " + irradiance);
            }
            case "wind" -> {
                irradiance = dto.getClimate().getWind(); // m/s
                if (irradiance <= 0 || irradiance > 20)
                    throw new IllegalArgumentException("Velocidad del viento inválida.");
                efficiency = 0.40;
            }
            case "hydro" -> {
                irradiance = dto.getClimate().getHydrology(); // índice arbitrario
                if (irradiance <= 0 || irradiance > 100)
                    throw new IllegalArgumentException("Índice hidrológico inválido.");
                efficiency = 0.50;
            }
            default -> throw new IllegalArgumentException("Tipo de energía no reconocido.");
        }

        // ⚙️ Cálculo de energía generada anual
        double energyGenerated = irradiance * efficiency * dto.getProjectSize() * 365;

        // 💶 Ahorro estimado
        double ahorro = energyGenerated * 0.2; // suponiendo 0.2€/kWh

        // 📈 Retorno sobre inversión
        double roi = ahorro > 0 ? dto.getBudget() / ahorro : 0;

        // 👁️‍🗨️ Mostrar en consola
        System.out.println("💰 Presupuesto: " + dto.getBudget());
        System.out.println("🔋 Energía generada: " + energyGenerated);
        System.out.println("💶 Ahorro estimado: " + ahorro);
        System.out.println("📈 ROI (años): " + roi);

        // 💾 Guardar simulación
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