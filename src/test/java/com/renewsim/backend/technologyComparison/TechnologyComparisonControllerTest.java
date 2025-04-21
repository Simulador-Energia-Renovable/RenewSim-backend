package com.renewsim.backend.technologyComparison;

import com.renewsim.backend.simulation.SimulationService;

import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TechnologyComparisonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TechnologyComparisonUseCase useCase;

    @MockBean
    private TechnologyComparisonService service;

    @MockBean
    private SimulationService simulationService;

    @MockBean
    private TechnologyComparisonMapper mapper;

    private TechnologyComparison technology;
    private TechnologyComparisonResponseDTO dto;

    @BeforeEach
    void setup() {
        technology = new TechnologyComparison();
        dto = TechnologyComparisonResponseDTO.builder()
                .technologyName("Solar")
                .build();

        when(mapper.toResponseDTO(any())).thenReturn(dto);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return all technologies")
    void testShouldReturnAllTechnologies() throws Exception {
        when(service.getAllTechnologies()).thenReturn(List.of(technology));

        mockMvc.perform(get("/api/v1/technologies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].technologyName").value("Solar"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return technology by ID if it exists")
    void testShouldReturnTechnologyByIdIfExists() throws Exception {
        when(service.getTechnologyById(1L)).thenReturn(Optional.of(technology));

        mockMvc.perform(get("/api/v1/technologies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technologyName").value("Solar"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 404 if technology not found by ID")
    void shouldReturn404IfTechnologyNotFound() throws Exception {
        when(service.getTechnologyById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/technologies/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should filter technologies by energy type")
    void shouldFilterTechnologiesByType() throws Exception {
        when(useCase.filterByType("solar")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/technologies/type/solar"))
                .andExpect(status().isOk());
    }
}
