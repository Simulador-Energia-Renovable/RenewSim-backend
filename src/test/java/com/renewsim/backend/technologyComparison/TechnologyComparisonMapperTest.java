package com.renewsim.backend.technologyComparison;

import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonRequestDTO;
import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.Field;

import java.util.List;

@DisplayName("Unit tests for TechnologyComparisonMapper")
class TechnologyComparisonMapperTest {

    private TechnologyComparisonMapper mapper;
    private TechnologyComparisonRequestDTO requestDTO;
    private TechnologyComparison entity;

    @BeforeEach
    void setUp() {
        mapper = new TechnologyComparisonMapper();

        requestDTO = TechnologyComparisonRequestDTO.builder()
                .technologyName("Solar")
                .efficiency(85.0)
                .installationCost(1000.0)
                .maintenanceCost(70.0)
                .environmentalImpact("Low impact")
                .co2Reduction(50.0)
                .energyProduction(300.0)
                .energyType("Solar")
                .build();

        entity = new TechnologyComparison(
                "Wind", 75.0, 1500.0, 90.0,
                "Moderate", 60.0, 400.0, "Wind"
        );
    }

    @Test
    @DisplayName("Should map TechnologyComparisonRequestDTO to TechnologyComparison entity")
    void testShouldMapRequestDtoToEntity() {
        TechnologyComparison mappedEntity = mapper.toEntity(requestDTO);

        assertThat(mappedEntity.getTechnologyName()).isEqualTo(requestDTO.getTechnologyName());
        assertThat(mappedEntity.getEfficiency()).isEqualTo(requestDTO.getEfficiency());
        assertThat(mappedEntity.getInstallationCost()).isEqualTo(requestDTO.getInstallationCost());
        assertThat(mappedEntity.getMaintenanceCost()).isEqualTo(requestDTO.getMaintenanceCost());
        assertThat(mappedEntity.getEnvironmentalImpact()).isEqualTo(requestDTO.getEnvironmentalImpact());
        assertThat(mappedEntity.getCo2Reduction()).isEqualTo(requestDTO.getCo2Reduction());
        assertThat(mappedEntity.getEnergyProduction()).isEqualTo(requestDTO.getEnergyProduction());
        assertThat(mappedEntity.getEnergyType()).isEqualTo(requestDTO.getEnergyType());
    }

    @Test
    @DisplayName("Should map TechnologyComparison entity to TechnologyComparisonResponseDTO")
    void testShouldMapEntityToResponseDto() throws NoSuchFieldException, IllegalAccessException {

        Field idField = TechnologyComparison.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, 1L);
    
        entity.setSimulations(List.of());

        TechnologyComparisonResponseDTO dto = mapper.toResponseDTO(entity);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTechnologyName()).isEqualTo(entity.getTechnologyName());
        assertThat(dto.getEfficiency()).isEqualTo(entity.getEfficiency());
        assertThat(dto.getInstallationCost()).isEqualTo(entity.getInstallationCost());
        assertThat(dto.getMaintenanceCost()).isEqualTo(entity.getMaintenanceCost());
        assertThat(dto.getEnvironmentalImpact()).isEqualTo(entity.getEnvironmentalImpact());
        assertThat(dto.getCo2Reduction()).isEqualTo(entity.getCo2Reduction());
        assertThat(dto.getEnergyProduction()).isEqualTo(entity.getEnergyProduction());
        assertThat(dto.getEnergyType()).isEqualTo(entity.getEnergyType());
        assertThat(dto.isInUse()).isFalse(); 
    }
}



