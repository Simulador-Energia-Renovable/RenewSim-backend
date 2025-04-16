package com.renewsim.backend.simulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renewsim.backend.simulation.dto.*;

import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("SimulationController Integration Tests")
class SimulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SimulationUseCase simulationUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user@example.com", authorities = "SCOPE_write:simulations")
    void shouldSimulateAndReturnResponse() throws Exception {
        SimulationRequestDTO request = new SimulationRequestDTO();
        request.setEnergyType("solar");
        request.setProjectSize(100);
        request.setBudget(5000);
        request.setEnergyConsumption(3000);
        request.setClimate(new ClimateData());

        SimulationResponseDTO response = new SimulationResponseDTO();
        response.setEnergyGenerated(10000);
        response.setEstimatedSavings(2000);

        Mockito.when(simulationUseCase.simulateAndSave(any(), eq("user@example.com")))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/simulation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.energyGenerated").value(10000));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "SCOPE_read:simulations")
    void shouldGetUserSimulations() throws Exception {
        SimulationHistoryDTO history = SimulationHistoryDTO.builder()
                .id(1L)
                .location("Madrid")
                .build();

        Mockito.when(simulationUseCase.getUserSimulationHistoryDTOs("user@example.com"))
                .thenReturn(List.of(history));

        mockMvc.perform(get("/api/v1/simulation/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location").value("Madrid"));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_read:simulations")
    void shouldGetTechnologiesForSimulation() throws Exception {
        TechnologyComparisonResponseDTO tech = new TechnologyComparisonResponseDTO();
        tech.setTechnologyName("Solar");

        Mockito.when(simulationUseCase.getTechnologiesForSimulation(1L))
                .thenReturn(List.of(tech));

        mockMvc.perform(get("/api/v1/simulation/1/technologies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].technologyName").value("Solar"));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "SCOPE_write:simulations")
    void shouldDeleteUserSimulations() throws Exception {
        mockMvc.perform(delete("/api/v1/simulation/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User simulations deleted successfully"));

        Mockito.verify(simulationUseCase).deleteUserSimulations("user@example.com");
    }

    @Test
    @WithMockUser(authorities = "SCOPE_read:simulations")
    void shouldGetNormalizationStats() throws Exception {
        NormalizationStatsDTO stats = NormalizationStatsDTO.builder()
                .minCo2(1).maxCo2(5)
                .minEnergy(1000).maxEnergy(5000)
                .minCost(200).maxCost(1000)
                .minEfficiency(0.1).maxEfficiency(0.9)
                .build();

        Mockito.when(simulationUseCase.getCurrentNormalizationStats()).thenReturn(stats);

        mockMvc.perform(get("/api/v1/simulation/normalization-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minCo2").value(1));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_read:simulations")
    void shouldGetNormalizedTechnologies() throws Exception {
        NormalizedTechnologyDTO tech = new NormalizedTechnologyDTO();
        tech.setTechnologyName("Wind");

        Mockito.when(simulationUseCase.getNormalizedTechnologies()).thenReturn(List.of(tech));

        mockMvc.perform(get("/api/v1/simulation/normalized-technologies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].technologyName").value("Wind"));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_read:simulations")
    void shouldGetAllTechnologies() throws Exception {
        TechnologyComparisonResponseDTO tech = new TechnologyComparisonResponseDTO();
        tech.setTechnologyName("Hydro");

        Mockito.when(simulationUseCase.getAllTechnologies()).thenReturn(List.of(tech));

        mockMvc.perform(get("/api/v1/simulation/technologies/global"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].technologyName").value("Hydro"));
    }
}

