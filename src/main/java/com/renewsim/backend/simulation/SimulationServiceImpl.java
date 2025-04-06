package com.renewsim.backend.simulation;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
public class SimulationServiceImpl implements SimulationService {

    private final SimulationRepository simulationRepository;
    private final UserRepository userRepository;
    private final TechnologyComparisonRepository technologyComparisonRepository;
    private final SimulationMapper simulationMapper;

    // ✅ Simulate and save
    @Override
    @Transactional
    public SimulationResponseDTO simulateAndSave(SimulationRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Validations
        if (dto.getProjectSize() <= 0 || dto.getProjectSize() > 500) {
            throw new IllegalArgumentException("El tamaño del proyecto debe ser entre 1 y 500 m².");
        }
        if (dto.getBudget() <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser mayor que cero.");
        }
        if (dto.getEnergyConsumption() < 50 || dto.getEnergyConsumption() > 100000) {
            throw new IllegalArgumentException("El consumo energético debe estar entre 50 y 100000 kWh/mes.");
        }

        List<TechnologyComparison> selectedTechnologies = technologyComparisonRepository
                .findByEnergyType(dto.getEnergyType());

        List<TechnologyComparisonResponseDTO> technologyDTOs = selectedTechnologies.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        double irradiance = getIrradiance(dto);
        double efficiency = getEfficiency(dto.getEnergyType());

        String recommendedTechnology = selectedTechnologies.stream()
                .max(Comparator.comparingDouble(tech -> (tech.getCo2Reduction() * 0.3)
                        + (tech.getEnergyProduction() * 0.4)
                        - (tech.getInstallationCost() * 0.3)))
                .map(TechnologyComparison::getTechnologyName)
                .orElse("No recommendation available");

        double energyGenerated = irradiance * efficiency * dto.getProjectSize() * 365;
        double estimatedSavings = energyGenerated * 0.2;
        double returnOnInvestment = estimatedSavings > 0 ? dto.getBudget() / estimatedSavings : 0;

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
        simulation.setTechnologies(selectedTechnologies);

        Simulation savedSimulation = simulationRepository.save(simulation);

        return new SimulationResponseDTO(
                savedSimulation.getId(),
                savedSimulation.getEnergyGenerated(),
                savedSimulation.getEstimatedSavings(),
                savedSimulation.getReturnOnInvestment(),
                savedSimulation.getTimestamp(),
                technologyDTOs,
                recommendedTechnology
        );
    }

    // ✅ Calculate simulation without saving
    @Override
    @Cacheable(value = "simulations", key = "#dto.hashCode()")
    public SimulationResponseDTO calculateSimulation(SimulationRequestDTO dto) {
        double irradiance = getIrradiance(dto);
        double efficiency = getEfficiency(dto.getEnergyType());

        List<TechnologyComparisonResponseDTO> technologyDTOs = technologyComparisonRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        double energyGenerated = irradiance * efficiency * dto.getProjectSize() * 365;
        double estimatedSavings = energyGenerated * 0.2;
        double returnOnInvestment = estimatedSavings > 0 ? dto.getBudget() / estimatedSavings : 0;

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
                .estimatedSavings(estimatedSavings)
                .returnOnInvestment(returnOnInvestment)
                .timestamp(LocalDateTime.now())
                .technologies(technologyDTOs)
                .recommendedTechnology(recommendedTechnology)
                .build();
    }

    // ✅ Get user simulations
    @Override
    public List<Simulation> getUserSimulations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return simulationRepository.findAllByUser(user);
    }

    // ✅ Get single simulation by ID
    @Override
    public Simulation getSimulationById(Long simulationId) {
        return simulationRepository.findById(simulationId)
                .orElseThrow(() -> new IllegalArgumentException("Simulación no encontrada"));
    }

    // ✅ Get simulation history DTOs
    @Override
    public List<SimulationHistoryDTO> getUserSimulationHistoryDTOs(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return simulationRepository.findAllByUser(user).stream()
                .map(simulationMapper::toHistoryDTO)
                .collect(Collectors.toList());
    }

    // ✅ Delete all simulations of a user
    @Override
    @Transactional
    public void deleteSimulationsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        simulationRepository.deleteByUser(user);
    }

    // Helpers 🧩

    private TechnologyComparisonResponseDTO mapToDTO(TechnologyComparison tech) {
        return new TechnologyComparisonResponseDTO(
                tech.getTechnologyName(),
                tech.getEfficiency(),
                tech.getInstallationCost(),
                tech.getMaintenanceCost(),
                tech.getEnvironmentalImpact(),
                tech.getCo2Reduction(),
                tech.getEnergyProduction()
        );
    }

    private double getIrradiance(SimulationRequestDTO dto) {
        return switch (dto.getEnergyType().toLowerCase()) {
            case "solar" -> dto.getClimate().getIrradiance();
            case "wind" -> dto.getClimate().getWind();
            case "hydro" -> dto.getClimate().getHydrology();
            default -> throw new IllegalArgumentException("Tipo de energía no reconocido.");
        };
    }

    private double getEfficiency(String energyType) {
        return switch (energyType.toLowerCase()) {
            case "solar" -> 0.18;
            case "wind" -> 0.40;
            case "hydro" -> 0.50;
            default -> throw new IllegalArgumentException("Tipo de energía no reconocido.");
        };
    }

    private double calculateScore(TechnologyComparisonResponseDTO tech) {
        double co2 = tech.getCo2Reduction();
        double energyProduction = tech.getEnergyProduction();
        double installationCost = tech.getInstallationCost();

        double normalizedCo2 = co2 / 100;
        double normalizedEnergy = energyProduction / 10000;
        double normalizedCost = installationCost / 10000;

        return (normalizedCo2 * 0.3) +
                (normalizedEnergy * 0.4) -
                (normalizedCost * 0.3);
    }
}
