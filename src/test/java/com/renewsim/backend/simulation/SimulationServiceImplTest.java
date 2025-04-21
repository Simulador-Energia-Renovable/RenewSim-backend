package com.renewsim.backend.simulation;

import com.renewsim.backend.simulation.dto.*;
import com.renewsim.backend.simulation.logic.SimulationCalculator;
import com.renewsim.backend.simulation.logic.SimulationValidator;
import com.renewsim.backend.simulation.logic.TechnologyRecommender;

import com.renewsim.backend.technologyComparison.TechnologyComparisonRepository;

import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationServiceImplTest {

    @Mock
    private SimulationRepository simulationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TechnologyComparisonRepository technologyComparisonRepository;

    @Mock
    private SimulationMapper simulationMapper;

    @Mock
    private SimulationValidator simulationValidator;

    @Mock
    private SimulationCalculator simulationCalculator;

    @Mock
    private TechnologyRecommender technologyRecommender;

    @InjectMocks
    private SimulationServiceImpl simulationService;

    private User user;
    private SimulationRequestDTO request;
    private Simulation simulation;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("user");

        request = new SimulationRequestDTO();
        request.setEnergyType("solar");
        request.setProjectSize(10.0);
        request.setBudget(10000);
        request.setClimate(new ClimateData());
        request.setEnergyConsumption(500);
        request.setLocation("Madrid");

        simulation = Simulation.builder()
                .id(1L)
                .energyGenerated(3000)
                .estimatedSavings(600)
                .returnOnInvestment(3.5)
                .projectSize(10)
                .location("Madrid")
                .energyType("solar")
                .user(user)
                .technologies(List.of())
                .build();
    }

    private void mockSecurityContext(String username) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should simulate and save when project size > 0")
    void testShouldSimulateAndSave_withProjectSize() {
        mockSecurityContext("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(technologyComparisonRepository.findByEnergyType("solar")).thenReturn(List.of());
        when(simulationCalculator.calculateEnergyGenerated(request)).thenReturn(3000.0);
        when(simulationCalculator.calculateEstimatedSavings(3000)).thenReturn(600.0);
        when(simulationCalculator.calculateROI(10000, 600)).thenReturn(3.5);
        when(technologyRecommender.recommendTechnology(any(), any())).thenReturn("Solar");
        when(simulationRepository.save(any())).thenReturn(simulation);

        SimulationResponseDTO result = simulationService.simulateAndSave(request);

        assertThat(result.getEnergyGenerated()).isEqualTo(3000);
        verify(simulationValidator).validate(request);
        verify(simulationRepository).save(any(Simulation.class));
    }

    @Test
    @DisplayName("Should simulate and estimate project size when size is zero")
    void testShouldSimulateAndSave_withEstimatedSize() {
        request.setProjectSize(0);
        request.setEnergyConsumption(500);
        request.setEnergyType("solar");

        mockSecurityContext("user");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(simulationCalculator.estimateProjectSize(500, "solar", request.getClimate())).thenReturn(12.5);
        when(technologyComparisonRepository.findByEnergyType("solar")).thenReturn(List.of());
        when(simulationCalculator.calculateEnergyGenerated(request)).thenReturn(3500.0);
        when(simulationCalculator.calculateEstimatedSavings(3500)).thenReturn(700.0);
        when(simulationCalculator.calculateROI(10000, 700)).thenReturn(3.2);
        when(technologyRecommender.recommendTechnology(any(), any())).thenReturn("Solar");
        when(simulationRepository.save(any())).thenReturn(simulation);

        simulationService.simulateAndSave(request);

        assertThat(request.getProjectSize()).isEqualTo(12.5);
        verify(simulationValidator).validate(request);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException if user not found in simulateAndSave")
    void testShouldSimulateAndSave_userNotFound() {
        mockSecurityContext("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> simulationService.simulateAndSave(request))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    @DisplayName("Should get user simulations")
    void testShouldGetUserSimulations() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        simulationService.getUserSimulations("user");
        verify(simulationRepository).findAllByUser(user);
    }

    @Test
    @DisplayName("Should throw when user not found in getUserSimulations")
    void testShouldGetUserSimulations_userNotFound() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> simulationService.getUserSimulations("user"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

}
