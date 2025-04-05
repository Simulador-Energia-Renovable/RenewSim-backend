package com.renewsim.backend.simulation;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;

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
    @Transactional
    public SimulationResponseDTO simulateAndSave(SimulationRequestDTO dto) {
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Validaciones de entrada
        if (dto.getProjectSize() <= 0 || dto.getProjectSize() > 500) {
            throw new IllegalArgumentException("El tamaño del proyecto debe ser entre 1 y 500 m².");
        }
        if (dto.getBudget() <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser mayor que cero.");
        }
        if (dto.getEnergyConsumption() < 50 || dto.getEnergyConsumption() > 100000) {
            throw new IllegalArgumentException("El consumo energético debe estar entre 50 y 100000 kWh/mes.");
        }

        // Buscar tecnologías por tipo de energía
        List<TechnologyComparison> selectedTechnologies = technologyComparisonRepository
                .findByEnergyType(dto.getEnergyType());

        // Mensaje de debug para confirmar tecnologías encontradas
        System.out.println("Tecnologías encontradas para el tipo de energía '" + dto.getEnergyType() + "': "
                + selectedTechnologies.size());

        // Mapear tecnologías a DTOs de respuesta
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

        // Configurar parámetros según el tipo de energía
        double irradiance = switch (dto.getEnergyType().toLowerCase()) {
            case "solar" -> dto.getClimate().getIrradiance();
            case "wind" -> dto.getClimate().getWind();
            case "hydro" -> dto.getClimate().getHydrology();
            default -> throw new IllegalArgumentException("Tipo de energía no reconocido.");
        };
        // Calculamos la mejor tecnología basada en impacto-beneficio
        String recommendedTechnology = selectedTechnologies.stream()
                .max(Comparator.comparingDouble(tech -> (tech.getCo2Reduction() * 0.3) +
                        (tech.getEnergyProduction() * 0.4) -
                        (tech.getInstallationCost() * 0.3)))
                .map(TechnologyComparison::getTechnologyName)
                .orElse("No recommendation available");

        double efficiency = switch (dto.getEnergyType().toLowerCase()) {
            case "solar" -> 0.18;
            case "wind" -> 0.40;
            case "hydro" -> 0.50;
            default -> throw new IllegalArgumentException("Tipo de energía no reconocido.");
        };

        // Cálculos de simulación
        double energyGenerated = irradiance * efficiency * dto.getProjectSize() * 365;
        double estimatedSavings = energyGenerated * 0.2;
        double returnOnInvestment = estimatedSavings > 0 ? dto.getBudget() / estimatedSavings : 0;

        // Crear entidad de simulación
        Simulation simulation = new Simulation();
        simulation.setLocation(dto.getLocation());
        simulation.setEnergyType(dto.getEnergyType());
        simulation.setProjectSize(dto.getProjectSize());
        simulation.setBudget(dto.getBudget());
        simulation.setEnergyConsumption(dto.getEnergyConsumption());
        simulation.setEnergyGenerated(energyGenerated);
        simulation.setEstimatedSavings(estimatedSavings);
        simulation.setReturnOnInvestment(returnOnInvestment);
        simulation.setUser(user);

        // Asignar tecnologías a la simulación
        simulation.setTechnologies(selectedTechnologies);

        // Guardar simulación en base de datos
        Simulation savedSimulation = simulationRepository.save(simulation);

        // Devolver respuesta
        return new SimulationResponseDTO(
                savedSimulation.getId(),
                savedSimulation.getEnergyGenerated(),
                savedSimulation.getEstimatedSavings(),
                savedSimulation.getReturnOnInvestment(),
                savedSimulation.getTimestamp(),
                technologyDTOs,
                recommendedTechnology);
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

        String recommendedTechnology = technologyDTOs.isEmpty()
                ? "No hay tecnologías disponibles para recomendar."
                : technologyDTOs.stream()
                        .sorted((t1, t2) -> Double.compare(calculateScore(t2), calculateScore(t1)))
                        .map(TechnologyComparisonResponseDTO::getTechnologyName)
                        .findFirst()
                        .orElse("No se pudo determinar una recomendación.");

        return SimulationResponseDTO.builder()
                .simulationId(null)
                .energyGenerated(energyGenerated)
                .estimatedSavings(ahorro)
                .returnOnInvestment(roi)
                .timestamp(LocalDateTime.now())
                .technologies(technologyDTOs)
                .recommendedTechnology(recommendedTechnology)
                .build();

    }

    private double calculateScore(TechnologyComparisonResponseDTO tech) {
        double co2 = tech.getCo2Reduction();
        double energyProduction = tech.getEnergyProduction();
        double installationCost = tech.getInstallationCost();

        // Supongamos rangos promedio para normalizar
        double normalizedCo2 = co2 / 100; // si el máximo aproximado es 100
        double normalizedEnergy = energyProduction / 10000; // supongamos máximo 10,000 kWh
        double normalizedCost = installationCost / 10000; // supongamos máximo coste de instalación 10,000 €

        return (normalizedCo2 * 0.3) +
                (normalizedEnergy * 0.4) -
                (normalizedCost * 0.3);
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
