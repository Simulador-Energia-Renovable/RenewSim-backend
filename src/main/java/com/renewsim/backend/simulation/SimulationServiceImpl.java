package com.renewsim.backend.simulation;

import static com.renewsim.backend.simulation.util.TechnologyScoringUtil.*;

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

import com.renewsim.backend.simulation.dto.NormalizationStatsDTO;
import com.renewsim.backend.simulation.dto.SimulationHistoryDTO;
import com.renewsim.backend.simulation.dto.SimulationRequestDTO;
import com.renewsim.backend.simulation.dto.SimulationResponseDTO;
import com.renewsim.backend.simulation.logic.SimulationCalculator;
import com.renewsim.backend.simulation.logic.SimulationValidator;
import com.renewsim.backend.technologyComparison.TechnologyComparison;
import com.renewsim.backend.technologyComparison.TechnologyComparisonRepository;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class SimulationServiceImpl implements SimulationService {

    private final SimulationRepository simulationRepository;
    private final UserRepository userRepository;
    private final TechnologyComparisonRepository technologyComparisonRepository;
    private final SimulationMapper simulationMapper;
    private final SimulationValidator simulationValidator;
    private final SimulationCalculator simulationCalculator;

    @Override
    @Transactional
    public SimulationResponseDTO simulateAndSave(SimulationRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Validación de entrada
        simulationValidator.validate(dto);

        // Tecnologías por tipo
        List<TechnologyComparison> selectedTechnologies = technologyComparisonRepository
                .findByEnergyType(dto.getEnergyType());

        List<TechnologyComparisonResponseDTO> technologyDTOs = selectedTechnologies.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        var stats = calculateNormalizationStats(technologyDTOs);

        // Lógica de recomendación (más adelante con TechnologyRecommender)
        String recommendedTechnology = technologyDTOs.stream()
                .max(Comparator.comparingDouble(tech -> calculateScoreDynamic(tech, stats)))
                .map(TechnologyComparisonResponseDTO::getTechnologyName)
                .orElse("No recommendation available");

        // Cálculos
        double energyGenerated = simulationCalculator.calculateEnergyGenerated(dto);
        double estimatedSavings = simulationCalculator.calculateEstimatedSavings(energyGenerated);
        double returnOnInvestment = simulationCalculator.calculateROI(dto.getBudget(), estimatedSavings);

        Simulation simulation = Simulation.builder()
                .location(dto.getLocation())
                .energyType(dto.getEnergyType())
                .projectSize(dto.getProjectSize())
                .budget(dto.getBudget())
                .energyConsumption(dto.getEnergyConsumption())
                .energyGenerated(energyGenerated)
                .estimatedSavings(estimatedSavings)
                .returnOnInvestment(returnOnInvestment)
                .user(user)
                .technologies(selectedTechnologies)
                .build();

        Simulation savedSimulation = simulationRepository.save(simulation);

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

        // Obtener tecnologías disponibles
        List<TechnologyComparisonResponseDTO> technologyDTOs = technologyComparisonRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // Calcular estadísticas de normalización
        var stats = calculateNormalizationStats(technologyDTOs);

        // Recomendación simple basada en score
        String recommendedTechnology = technologyDTOs.isEmpty()
                ? "No hay tecnologías disponibles para recomendar."
                : technologyDTOs.stream()
                        .max(Comparator.comparingDouble(tech -> calculateScoreDynamic(tech, stats)))
                        .map(TechnologyComparisonResponseDTO::getTechnologyName)
                        .orElse("No se pudo determinar una recomendación.");

        // Cálculos usando SimulationCalculator
        double energyGenerated = simulationCalculator.calculateEnergyGenerated(dto);
        double estimatedSavings = simulationCalculator.calculateEstimatedSavings(energyGenerated);
        double returnOnInvestment = simulationCalculator.calculateROI(dto.getBudget(), estimatedSavings);

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

    // Get user simulations
    @Override
    public List<Simulation> getUserSimulations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return simulationRepository.findAllByUser(user);
    }

    // Get single simulation by ID
    @Override
    public Simulation getSimulationById(Long simulationId) {
        return simulationRepository.findById(simulationId)
                .orElseThrow(() -> new IllegalArgumentException("Simulación no encontrada"));
    }

    // Get simulation history DTOs
    @Override
    public List<SimulationHistoryDTO> getUserSimulationHistoryDTOs(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return simulationRepository.findAllByUser(user).stream()
                .map(simulationMapper::toHistoryDTO)
                .collect(Collectors.toList());
    }

    // Delete all simulations of a user
    @Override
    @Transactional
    public void deleteSimulationsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        simulationRepository.deleteByUser(user);
    }

    @Override
    public NormalizationStatsDTO getCurrentNormalizationStats() {
        List<TechnologyComparisonResponseDTO> techList = technologyComparisonRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return calculateNormalizationStats(techList);
    }

    @Override
    public List<TechnologyComparisonResponseDTO> getAllTechnologies() {
        return technologyComparisonRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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
                tech.getEnergyProduction(),
                tech.getEnergyType());
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
}
