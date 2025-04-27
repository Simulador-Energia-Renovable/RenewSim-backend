package com.renewsim.backend.technologyComparison;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.renewsim.backend.simulation.Simulation;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for TechnologyComparisonServiceImpl using Mockito")
class TechnologyComparisonServiceImplUnitTest {

    private static final Long EXISTING_ID = 1L;
    private static final Long NON_EXISTING_ID = 999L;

    @Mock
    private TechnologyComparisonRepository repository;

    @InjectMocks
    private TechnologyComparisonServiceImpl service;

    private TechnologyComparison solar;

    @BeforeEach
    void setUp() {
        solar = createTechnology("Solar", 85.0, 1200.0, 70.0, "Clean", 50.0, 320.0, "Solar");
    }

    private TechnologyComparison createTechnology(String name, double efficiency, double installationCost,
                                                   double maintenanceCost, String impact, double co2Reduction,
                                                   double production, String type) {
        return new TechnologyComparison(name, efficiency, installationCost, maintenanceCost,
                impact, co2Reduction, production, type);
    }

    @Test
    @DisplayName("Should add technology when name is unique")
    void testShouldAddTechnologyWhenNameIsUnique() {
        when(repository.existsByTechnologyName("Solar")).thenReturn(false);
        when(repository.save(any())).thenReturn(solar);

        TechnologyComparison result = service.addTechnology(solar);

        assertThat(result).isEqualTo(solar);
        verify(repository).save(solar);
    }

    @Test
    @DisplayName("Should throw exception when technology name already exists")
    void testShouldThrowWhenTechnologyNameExists() {
        when(repository.existsByTechnologyName("Solar")).thenReturn(true);

        assertThatThrownBy(() -> service.addTechnology(solar))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Technology with this name already exists.");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should get all technologies")
    void testShouldGetAllTechnologies() {
        when(repository.findAll()).thenReturn(List.of(solar));

        List<TechnologyComparison> result = service.getAllTechnologies();

        assertThat(result).containsExactly(solar);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should get technology by ID")
    void testShouldGetById() {
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(solar));

        Optional<TechnologyComparison> result = service.getTechnologyById(EXISTING_ID);

        assertThat(result).isPresent().contains(solar);
        verify(repository).findById(EXISTING_ID);
    }

    @Test
    @DisplayName("Should delete existing technology")
    void testShouldDeleteTechnology() {
        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(solar));

        service.deleteTechnology(EXISTING_ID);

        verify(repository).deleteById(EXISTING_ID);
    }

    @Test
    @DisplayName("Should throw when deleting nonexistent technology")
    void testShouldThrowWhenDeletingNonexistent() {
        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteTechnology(NON_EXISTING_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Technology not found");

        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw when deleting technology associated with simulations")
    void testShouldThrowWhenDeletingTechnologyWithSimulations() {
        Simulation dummySimulation = new Simulation();
        dummySimulation.setId(100L);
        dummySimulation.setLocation("Test Location");
        solar.setSimulations(List.of(dummySimulation));

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(solar));

        assertThatThrownBy(() -> service.deleteTechnology(EXISTING_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Tecnología asociada a simulaciones");

        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should update existing technology")
    void testShouldUpdateTechnology() {
        TechnologyComparison updatedSolar = createTechnology(
                "Updated Solar", 90.0, 1300.0, 60.0, "Updated Clean", 55.0, 350.0, "Solar");

        when(repository.findById(EXISTING_ID)).thenReturn(Optional.of(solar));
        when(repository.save(any())).thenReturn(updatedSolar);

        TechnologyComparison result = service.updateTechnology(EXISTING_ID, updatedSolar);

        assertThat(result.getTechnologyName()).isEqualTo("Updated Solar");
        assertThat(result.getEfficiency()).isEqualTo(90.0);
        assertThat(result.getInstallationCost()).isEqualTo(1300.0);
        assertThat(result.getMaintenanceCost()).isEqualTo(60.0);
        assertThat(result.getEnvironmentalImpact()).isEqualTo("Updated Clean");
        assertThat(result.getCo2Reduction()).isEqualTo(55.0);
        assertThat(result.getEnergyProduction()).isEqualTo(350.0);
        assertThat(result.getEnergyType()).isEqualTo("Solar");

        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should throw when updating non-existent technology")
    void testShouldThrowWhenUpdatingNonexistentTechnology() {
        TechnologyComparison updatedSolar = createTechnology(
                "Updated Solar", 90.0, 1300.0, 60.0, "Updated Clean", 55.0, 350.0, "Solar");

        when(repository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTechnology(NON_EXISTING_ID, updatedSolar))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tecnología no encontrada con ID: 999");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should filter technologies by energy type")
    void testShouldFindByEnergyType() {
        when(repository.findByEnergyType("Solar")).thenReturn(List.of(solar));

        List<TechnologyComparison> result = service.getTechnologiesByEnergyType("Solar");

        assertThat(result).hasSize(1).contains(solar);
        verify(repository).findByEnergyType("Solar");
    }
}