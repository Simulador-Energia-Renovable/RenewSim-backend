package com.renewsim.backend.auth.web.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.renewsim.backend.auth_service.web.dto.AuthRequestDTO;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthRequestDTO Tests")
class AuthRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should build AuthRequestDTO with valid data")
    void shouldBuildAuthRequestDTO() {
        AuthRequestDTO dto = AuthRequestDTO.builder()
                .username("testuser")
                .password("securePassword")
                .build();

        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.getPassword()).isEqualTo("securePassword");
    }

    @Test
    @DisplayName("Should pass validation for valid data")
    void shouldPassValidationWithValidData() {
        AuthRequestDTO dto = AuthRequestDTO.builder()
                .username("testuser")
                .password("securePassword")
                .build();

        Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

}
