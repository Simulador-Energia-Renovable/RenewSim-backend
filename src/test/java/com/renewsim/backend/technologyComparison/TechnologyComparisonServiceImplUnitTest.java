package com.renewsim.backend.technologyComparison;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for TechnologyComparisonServiceImpl using Mockito")
class TechnologyComparisonServiceImplUnitTest {

    @Mock
    private TechnologyComparisonRepository repository;

    @InjectMocks
    private TechnologyComparisonServiceImpl service;

    private TechnologyComparison solar;

    @BeforeEach
    void setUp() {
        solar = new TechnologyComparison(
                "Solar",
                85.0,
                1200.0,
                70.0,
                "Clean",
                50.0,
                320.0,
                "Solar"
        );
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
        when(repository.findById(1L)).thenReturn(Optional.of(solar));

        Optional<TechnologyComparison> result = service.getTechnologyById(1L);

        assertThat(result).isPresent().contains(solar);
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Should delete existing technology")
    void testShouldDeleteTechnology() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteTechnology(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw when deleting nonexistent technology")
    void testShouldThrowWhenDeletingNonexistent() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteTechnology(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Technology with ID 999 does not exist.");

        verify(repository, never()).deleteById(any());
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
