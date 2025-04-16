package com.renewsim.backend.simulation.logic;

import com.renewsim.backend.simulation.dto.SimulationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SimulationValidator Unit Tests")
class SimulationValidatorTest {

    private SimulationValidator validator;
    private SimulationRequestDTO dto;

    @BeforeEach
    void setUp() {
        validator = new SimulationValidator();
        dto = new SimulationRequestDTO();
        dto.setProjectSize(100);      
        dto.setBudget(10000);      
        dto.setEnergyConsumption(1000); 
    }

    @Test
    @DisplayName("Should validate a correct SimulationRequestDTO")
    void testShouldValidateCorrectDTO() {
        assertThatCode(() -> validator.validate(dto))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should fail when project size is zero")
    void testShouldFailWhenProjectSizeIsZero() {
        dto.setProjectSize(0);
        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tamaño del proyecto");
    }

    @Test
    @DisplayName("Should fail when project size is negative")
    void testShouldFailWhenProjectSizeIsNegative() {
        dto.setProjectSize(-50);
        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should fail when project size exceeds 500")
    void testShouldFailWhenProjectSizeExceedsMax() {
        dto.setProjectSize(501);
        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should fail when budget is zero")
    void testShouldFailWhenBudgetIsZero() {
        dto.setBudget(0);
        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("presupuesto");
    }

    @Test
    @DisplayName("Should fail when budget is negative")
    void testShouldFailWhenBudgetIsNegative() {
        dto.setBudget(-100);
        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should fail when energy consumption is below 50")
    void testShouldFailWhenConsumptionTooLow() {
        dto.setEnergyConsumption(49);
        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("consumo energético");
    }

    @Test
    @DisplayName("Should fail when energy consumption exceeds 100000")
    void testShouldFailWhenConsumptionTooHigh() {
        dto.setEnergyConsumption(100001);
        assertThatThrownBy(() -> validator.validate(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should validate edge values correctly")
    void testShouldAllowEdgeValues() {
        dto.setProjectSize(1);
        dto.setBudget(1);
        dto.setEnergyConsumption(50);

        assertThatCode(() -> validator.validate(dto)).doesNotThrowAnyException();

        dto.setProjectSize(500);
        dto.setEnergyConsumption(100000);

        assertThatCode(() -> validator.validate(dto)).doesNotThrowAnyException();
    }
}

