package com.renewsim.backend.technologyComparison;

import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonRequestDTO;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for TechnologyComparisonUseCase")
class TechnologyComparisonUseCaseTest {

    @Mock
    private TechnologyComparisonService service;

    @Mock
    private TechnologyComparisonMapper mapper;

    @InjectMocks
    private TechnologyComparisonUseCase useCase;

    private TechnologyComparisonRequestDTO requestDTO;
    private TechnologyComparison entity;
    private TechnologyComparisonResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = TechnologyComparisonRequestDTO.builder()
                .technologyName("Solar")
                .efficiency(85.0)
                .installationCost(1200.0)
                .maintenanceCost(70.0)
                .environmentalImpact("Clean")
                .co2Reduction(50.0)
                .energyProduction(320.0)
                .energyType("Solar")
                .build();

        entity = new TechnologyComparison(
                "Solar",
                85.0,
                1200.0,
                70.0,
                "Clean",
                50.0,
                320.0,
                "Solar"
        );

        responseDTO = TechnologyComparisonResponseDTO.builder()
                .technologyName("Solar")
                .efficiency(85.0)
                .installationCost(1200.0)
                .maintenanceCost(70.0)
                .environmentalImpact("Clean")
                .co2Reduction(50.0)
                .energyProduction(320.0)
                .energyType("Solar")
                .build();
    }

    @Test
    @DisplayName("Should create technology and return response DTO")
    void testShouldCreateTechnology() {
        when(mapper.toEntity(requestDTO)).thenReturn(entity);
        when(service.addTechnology(entity)).thenReturn(entity);
        when(mapper.toResponseDTO(entity)).thenReturn(responseDTO);

        TechnologyComparisonResponseDTO result = useCase.createTechnology(requestDTO);

        assertThat(result).isEqualTo(responseDTO);
        verify(mapper).toEntity(requestDTO);
        verify(service).addTechnology(entity);
        verify(mapper).toResponseDTO(entity);
    }

    @Test
    @DisplayName("Should delegate deleteTechnology to service")
    void testShouldCallDeleteTechnology() {
        Long id = 1L;

        useCase.deleteTechnology(id);

        verify(service).deleteTechnology(id);
    }

    @Test
    @DisplayName("Should return list of filtered technologies by type")
    void testShouldFilterByType() {
        List<TechnologyComparison> entities = List.of(entity);
        List<TechnologyComparisonResponseDTO> expectedDtos = List.of(responseDTO);

        when(service.getTechnologiesByEnergyType("Solar")).thenReturn(entities);
        when(mapper.toResponseDTO(entity)).thenReturn(responseDTO);

        List<TechnologyComparisonResponseDTO> result = useCase.filterByType("Solar");

        assertThat(result).isEqualTo(expectedDtos);
        verify(service).getTechnologiesByEnergyType("Solar");
        verify(mapper).toResponseDTO(entity);
    }
}
