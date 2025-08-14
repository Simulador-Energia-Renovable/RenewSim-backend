package com.renewsim.backend.auth.web.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthResponseDTO Tests")
class AuthResponseDTOTest {

    @Test
    @DisplayName("Should build AuthResponseDTO with valid data")
    void shouldBuildAuthResponseDTO() {
        String token = "sample.jwt.token";
        String username = "testuser";
        Set<String> roles = Set.of("USER", "ADMIN");

        AuthResponseDTO dto = AuthResponseDTO.builder()
                .token(token)
                .username(username)
                .roles(roles)
                .build();

        assertThat(dto.getToken()).isEqualTo(token);
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getRoles()).containsExactlyInAnyOrderElementsOf(roles);
    }
}
