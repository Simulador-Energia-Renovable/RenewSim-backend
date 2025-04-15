package com.renewsim.backend.technologyComparison;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonRequestDTO;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;
import com.renewsim.backend.security.JwtAuthenticationFilter;
import com.renewsim.backend.simulation.Simulation;
import com.renewsim.backend.simulation.SimulationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(controllers = TechnologyComparisonController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
@ActiveProfiles("test")
@DisplayName("Integration tests for TechnologyComparisonController")
class TechnologyComparisonControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private TechnologyComparisonUseCase useCase;
    @MockBean private TechnologyComparisonService service;
    @MockBean private TechnologyComparisonMapper mapper;
    @MockBean private SimulationService simulationService;

    private TechnologyComparisonRequestDTO requestDTO;
    private TechnologyComparisonResponseDTO responseDTO;
    private TechnologyComparison tech;

    @BeforeEach
    void setUp() {
        requestDTO = TechnologyComparisonRequestDTO.builder()
                .technologyName("Solar")
                .efficiency(85.0)
                .installationCost(1000.0)
                .maintenanceCost(70.0)
                .energyType("Solar")
                .build();

        responseDTO = TechnologyComparisonResponseDTO.builder()
                .technologyName("Solar")
                .efficiency(85.0)
                .installationCost(1000.0)
                .maintenanceCost(70.0)
                .environmentalImpact("Low impact")
                .co2Reduction(50.0)
                .energyProduction(300.0)
                .energyType("Solar")
                .build();

        tech = new TechnologyComparison(
                "Solar", 85.0, 1000.0, 70.0, "Clean", 50.0, 300.0, "Solar");
    }

    @Test
    @DisplayName("POST /api/technologies - should create technology and return 200 OK")
    void testShouldCreateTechnology() throws Exception {
        when(useCase.createTechnology(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/technologies")
                .with(jwt().authorities(() -> "SCOPE_write:technologies"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technologyName").value("Solar"))
                .andExpect(jsonPath("$.efficiency").value(85.0));
    }

    @Test
    @DisplayName("POST /api/technologies - should return 400 BadRequest when data is invalid")
    void testShouldReturnBadRequestOnValidationFailure() throws Exception {
        TechnologyComparisonRequestDTO invalidDTO = TechnologyComparisonRequestDTO.builder()
                .efficiency(-5.0).build();

        mockMvc.perform(post("/api/technologies")
                .with(jwt().authorities(() -> "SCOPE_write:technologies"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/technologies - should return list of technologies")
    void testShouldGetAllTechnologies() throws Exception {
        when(service.getAllTechnologies()).thenReturn(List.of(tech));
        when(mapper.toResponseDTO(tech)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/technologies").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].technologyName").value("Solar"));
    }

    @Test
    @DisplayName("GET /api/technologies/{id} - should return technology by ID")
    void testShouldGetTechnologyById() throws Exception {
        when(service.getTechnologyById(1L)).thenReturn(Optional.of(tech));
        when(mapper.toResponseDTO(tech)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/technologies/1").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technologyName").value("Solar"));
    }

    @Test
    @DisplayName("GET /api/technologies/{id} - should return 404 if not found")
    void testShouldReturn404IfNotFound() throws Exception {
        when(service.getTechnologyById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/technologies/99").with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/technologies/simulation/{id} - should return technologies for simulation")
    void testShouldReturnTechnologiesBySimulationId() throws Exception {
        Simulation simulation = new Simulation();
        simulation.setTechnologies(List.of(tech));

        when(simulationService.getSimulationById(1L)).thenReturn(simulation);
        when(mapper.toResponseDTO(tech)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/technologies/simulation/1")
                .with(jwt().authorities(() -> "SCOPE_read:simulations"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].technologyName").value("Solar"))
                .andExpect(jsonPath("$[0].efficiency").value(85.0));
    }
}

