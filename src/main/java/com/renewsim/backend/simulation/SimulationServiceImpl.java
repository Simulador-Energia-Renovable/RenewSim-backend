package com.renewsim.backend.simulation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.renewsim.backend.technologyComparison.TechnologyComparison;
import com.renewsim.backend.technologyComparison.TechnologyComparisonRepository;
import com.renewsim.backend.technologyComparison.TechnologyComparisonResponseDTO;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;

@Service
public class SimulationServiceImpl implements SimulationService {

    private final SimulationRepository simulationRepository;
    private final UserRepository userRepository;
    private final TechnologyComparisonRepository technologyComparisonRepository;

    public SimulationServiceImpl(SimulationRepository simulationRepository, UserRepository userRepository,
            TechnologyComparisonRepository technologyComparisonRepository) {
        this.simulationRepository = simulationRepository;
        this.userRepository = userRepository;
        this.technologyComparisonRepository = technologyComparisonRepository;
    }

    @Override
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

    // 📌 Seleccionamos solo las tecnologías que coincidan con el tipo de energía
    List<TechnologyComparison> selectedTechnologies = technologyComparisonRepository.findByEnergyType(dto.getEnergyType());

    List<TechnologyComparisonResponseDTO> technologyDTOs = selectedTechnologies.stream()
            .map(tech -> new TechnologyComparisonResponseDTO(
                    tech.getTechnologyName(),
                    tech.getEfficiency(),
                    tech.getInstallationCost(),
                    tech.getMaintenanceCost(),
                    tech.getEnvironmentalImpact(),
                    tech.getCo2Reduction(),
                    tech.getEnergyProduction()))
            .collect(Collectors.toList());

    double irradiance = switch (dto.getEnergyType().toLowerCase()) {
        case "solar" -> dto.getClimate().getIrradiance();
        case "wind" -> dto.getClimate().getWind();
        case "hydro" -> dto.getClimate().getHydrology();
        default -> throw new IllegalArgumentException("Tipo de energía no reconocido.");
    };

    double efficiency = switch (dto.getEnergyType().toLowerCase()) {
        case "solar" -> 0.18;
        case "wind" -> 0.40;
        case "hydro" -> 0.50;
        default -> throw new IllegalArgumentException("Tipo de energía no reconocido.");
    };

    double energyGenerated = irradiance * efficiency * dto.getProjectSize() * 365;
    double ahorro = energyGenerated * 0.2;
    double roi = ahorro > 0 ? dto.getBudget() / ahorro : 0;

    // 🌱 Creamos la simulación
    Simulation simulation = new Simulation();
    simulation.setLocation(dto.getLocation());
    simulation.setEnergyType(dto.getEnergyType());
    simulation.setProjectSize(dto.getProjectSize());
    simulation.setBudget(dto.getBudget());
    simulation.setEnergyConsumption(dto.getEnergyConsumption());
    simulation.setEnergyGenerated(energyGenerated);
    simulation.setEstimatedSavings(ahorro);
    simulation.setReturnOnInvestment(roi);
    simulation.setUser(user);

    // Asociamos las tecnologías correctas
    simulation.setTechnologies(selectedTechnologies);

    // Relación inversa (importante 👇)
    selectedTechnologies.forEach(tech -> tech.getSimulations().add(simulation));

    // Guardamos la simulación con las relaciones bidireccionales
    simulationRepository.save(simulation);

    return new SimulationResponseDTO(
            simulation.getId(),
            energyGenerated,
            ahorro,
            roi,
            technologyDTOs
    );
}


    @Override
    @Cacheable(value = "simulations", key = "#dto.hashCode()")
    public SimulationResponseDTO calculateSimulation(SimulationRequestDTO dto) {
        double irradiance = 0;
        double efficiency = 0;

        List<TechnologyComparisonResponseDTO> technologyDTOs = technologyComparisonRepository.findAll().stream()
                .map(tech -> new TechnologyComparisonResponseDTO(
                        tech.getTechnologyName(),
                        tech.getEfficiency(),
                        tech.getInstallationCost(),
                        tech.getMaintenanceCost(),
                        tech.getEnvironmentalImpact(),
                        tech.getCo2Reduction(),
                        tech.getEnergyProduction()))
                .collect(Collectors.toList());

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

        return new SimulationResponseDTO(null, energyGenerated, ahorro, roi, technologyDTOs);
    }

    @Override
    public List<Simulation> getUserSimulations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return simulationRepository.findAllByUser(user);
    }

    @Override
    public Simulation getSimulationById(Long simulationId) {
        return simulationRepository.findById(simulationId)
                .orElseThrow(() -> new IllegalArgumentException("Simulación no encontrada"));
    }

}
