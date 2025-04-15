package com.renewsim.backend.technologyComparison;

import com.renewsim.backend.simulation.SimulationService;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonRequestDTO;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for TechnologyComparisonController")
class TechnologyComparisonControllerUnitTest {

    @Mock
    private TechnologyComparisonUseCase useCase;

    @Mock
    private TechnologyComparisonService service;

    @Mock
    private SimulationService simulationService;

    @Mock
    private TechnologyComparisonMapper mapper;

    @InjectMocks
    private TechnologyComparisonController controller;

    private TechnologyComparison technology;
    private TechnologyComparisonResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        technology = new TechnologyComparison(
                "Solar",
                85.0,
                1000.0,
                70.0,
                "Low impact",
                50.0,
                300.0,
                "Solar"
        );

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
    }

    @Test
    @DisplayName("Should return list of technologies")
    void testShouldReturnAllTechnologies() {
        when(service.getAllTechnologies()).thenReturn(List.of(technology));
        when(mapper.toResponseDTO(technology)).thenReturn(responseDTO);

        ResponseEntity<List<TechnologyComparisonResponseDTO>> result = controller.getAllTechnologies();

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).contains(responseDTO);
    }

    @Test
    @DisplayName("Should return technology by ID")
    void testShouldReturnTechnologyById() {
        when(service.getTechnologyById(1L)).thenReturn(Optional.of(technology));
        when(mapper.toResponseDTO(technology)).thenReturn(responseDTO);

        ResponseEntity<TechnologyComparisonResponseDTO> result = controller.getTechnologyById(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("Should return 404 when technology not found")
    void testShouldReturnNotFound() {
        when(service.getTechnologyById(99L)).thenReturn(Optional.empty());

        ResponseEntity<TechnologyComparisonResponseDTO> result = controller.getTechnologyById(99L);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should delegate creation to useCase")
    void testShouldCreateTechnology() {
        TechnologyComparisonRequestDTO requestDTO = TechnologyComparisonRequestDTO.builder()
                .technologyName("Solar")
                .efficiency(85.0)
                .installationCost(1000.0)
                .maintenanceCost(70.0)
                .energyType("Solar")
                .build();

        when(useCase.createTechnology(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<TechnologyComparisonResponseDTO> result = controller.addTechnology(requestDTO);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(responseDTO);
    }

    @Test
    @DisplayName("Should return 400 when creation fails")
    void testShouldReturnBadRequestOnCreateFailure() {
        TechnologyComparisonRequestDTO requestDTO = TechnologyComparisonRequestDTO.builder()
                .technologyName("Solar")
                .build();

        when(useCase.createTechnology(requestDTO)).thenThrow(new IllegalArgumentException("Duplicate"));

        ResponseEntity<TechnologyComparisonResponseDTO> result = controller.addTechnology(requestDTO);

        assertThat(result.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should delete technology by ID")
    void testShouldDeleteTechnology() {
        ResponseEntity<Void> result = controller.deleteTechnology(1L);

        verify(useCase).deleteTechnology(1L);
        assertThat(result.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    @DisplayName("Should return 404 when deleting nonexistent technology")
    void testShouldHandleDeleteFailure() {
        doThrow(new IllegalArgumentException("Not found")).when(useCase).deleteTechnology(99L);

        ResponseEntity<Void> result = controller.deleteTechnology(99L);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }
}
