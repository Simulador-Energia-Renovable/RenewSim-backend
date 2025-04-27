package com.renewsim.backend.technologyComparison;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for TechnologyDataInitializer using Mockito")
class TechnologyDataInitializerTest {

    @Mock
    private TechnologyComparisonRepository repository;

    @InjectMocks
    private TechnologyDataInitializer initializer;

    @Test
    @DisplayName("Should not initialize technologies if repository is not empty")
    void shouldNotInitializeIfRepositoryNotEmpty() {

        when(repository.count()).thenReturn(5L);
        initializer.init();
        verify(repository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should initialize technologies when repository is empty")
    void shouldInitializeTechnologiesIfRepositoryEmpty() {

        when(repository.count()).thenReturn(0L);
        initializer.init();
        verify(repository, times(1)).saveAll(any());
    }
}
