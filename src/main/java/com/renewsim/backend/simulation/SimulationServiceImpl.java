package com.renewsim.backend.simulation;

import static com.renewsim.backend.simulation.util.TechnologyScoringUtil.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.renewsim.backend.exception.ResourceNotFoundException;
import com.renewsim.backend.simulation.dto.*;
import com.renewsim.backend.simulation.logic.SimulationCalculator;
import com.renewsim.backend.simulation.logic.SimulationValidator;
import com.renewsim.backend.simulation.logic.TechnologyRecommender;
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
        private final TechnologyRecommender technologyRecommender;

        @Override
        @Transactional
        public SimulationResponseDTO simulateAndSave(SimulationRequestDTO dto) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                if (dto.getProjectSize() <= 0) {
                        double estimatedSize = simulationCalculator.estimateProjectSize(
                                        dto.getEnergyConsumption(),
                                        dto.getEnergyType(),
                                        dto.getClimate());
                        dto.setProjectSize(estimatedSize);
                }

                simulationValidator.validate(dto);

                List<TechnologyComparison> selectedTechnologies = technologyComparisonRepository
                                .findByEnergyType(dto.getEnergyType());

                List<TechnologyComparisonResponseDTO> technologyDTOs = selectedTechnologies.stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());

                NormalizationStatsDTO stats = calculateNormalizationStats(technologyDTOs);
                String recommendedTechnology = technologyRecommender.recommendTechnology(technologyDTOs, stats);

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
                                .recommendedTechnology(recommendedTechnology)
                                .build();

                Simulation savedSimulation = simulationRepository.save(simulation);

                return new SimulationResponseDTO(
                                savedSimulation.getId(),
                                savedSimulation.getLocation(),
                                savedSimulation.getEnergyType(),
                                savedSimulation.getEnergyGenerated(),
                                savedSimulation.getEstimatedSavings(),
                                savedSimulation.getReturnOnInvestment(),
                                savedSimulation.getProjectSize(),
                                savedSimulation.getBudget(),
                                savedSimulation.getTimestamp(),
                                technologyDTOs,
                                recommendedTechnology);
        }

        @Override
        @Cacheable(value = "simulations", key = "#dto.hashCode()")
        public SimulationResponseDTO calculateSimulation(SimulationRequestDTO dto) {
                List<TechnologyComparisonResponseDTO> technologyDTOs = technologyComparisonRepository.findAll().stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());

                NormalizationStatsDTO stats = calculateNormalizationStats(technologyDTOs);
                String recommendedTechnology = technologyRecommender.recommendTechnology(technologyDTOs, stats);

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

        @Override
        public List<SimulationHistoryDTO> getUserSimulationHistoryDTOs(String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                return simulationRepository.findAllByUser(user).stream()
                                .map(simulationMapper::toHistoryDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public void deleteSimulationsByUser(String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                simulationRepository.deleteByUser(user);
        }

        @Override
        public void deleteSimulationById(Long id) {
                if (!simulationRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Simulación no encontrada con ID: " + id);
                }
                simulationRepository.deleteById(id);
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
}
