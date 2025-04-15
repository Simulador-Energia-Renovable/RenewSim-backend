package com.renewsim.backend.technologyComparison;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.renewsim.backend.technologyComparison.dto.TechnologyComparisonRequestDTO;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validation tests for TechnologyComparisonRequestDTO")
public class TechnologyComparisonRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    public void testValidRequestDTO() {
        TechnologyComparisonRequestDTO dto = TechnologyComparisonRequestDTO.builder()
                .technologyName("Solar Panel")
                .efficiency(85.0)
                .installationCost(1500.0)
                .maintenanceCost(100.0)
                .environmentalImpact("Low emissions")
                .co2Reduction(50.0)
                .energyProduction(300.0)
                .energyType("Solar")
                .build();

        Set<ConstraintViolation<TechnologyComparisonRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "DTO should be valid");
    }

    @Test
    @DisplayName("Should fail validation when required fields are missing")
    public void testInvalidRequestDTO_MissingRequiredFields() {
        TechnologyComparisonRequestDTO dto = new TechnologyComparisonRequestDTO(); // campos vacíos

        Set<ConstraintViolation<TechnologyComparisonRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should have validation errors");

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("technologyName")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("efficiency")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("installationCost")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("maintenanceCost")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("energyType")));
    }

    @Test
    @DisplayName("Should fail validation when efficiency is negative")
    public void testInvalidValues_NegativeEfficiency() {
        TechnologyComparisonRequestDTO dto = TechnologyComparisonRequestDTO.builder()
                .technologyName("Wind")
                .efficiency(-1.0) // inválido
                .installationCost(1000.0)
                .maintenanceCost(200.0)
                .energyType("Wind")
                .build();

        Set<ConstraintViolation<TechnologyComparisonRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("efficiency")));
    }
}
