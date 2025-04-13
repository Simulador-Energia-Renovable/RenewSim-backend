package com.renewsim.backend.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserResponseDTO Test")
class UserResponseDTOTest {

    private Long id;
    private String username;
    private Set<String> roles;

    @BeforeEach
    void setUp() {
        id = 1L;
        username = "testuser";
        roles = Set.of("ADMIN", "USER");
    }

    @Test
    @DisplayName("Should create UserResponseDTO using constructor")
    void shouldCreateUserResponseDTOUsingConstructor() {
        UserResponseDTO dto = new UserResponseDTO(id, username, roles);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getRoles()).containsExactlyInAnyOrderElementsOf(roles);
    }

    @Test
    @DisplayName("Should create UserResponseDTO using builder")
    void shouldCreateUserResponseDTOUsingBuilder() {
        UserResponseDTO dto = UserResponseDTO.builder()
                .id(id)
                .username(username)
                .roles(roles)
                .build();

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getRoles()).containsExactlyInAnyOrderElementsOf(roles);
    }

    @Test
    @DisplayName("Should verify equals and hashCode")
    void shouldVerifyEqualsAndHashCode() {
        UserResponseDTO dto1 = UserResponseDTO.builder()
                .id(id)
                .username(username)
                .roles(roles)
                .build();

        UserResponseDTO dto2 = UserResponseDTO.builder()
                .id(id)
                .username(username)
                .roles(roles)
                .build();

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should return toString containing field values")
    void shouldReturnToStringContainingFieldValues() {
        UserResponseDTO dto = UserResponseDTO.builder()
                .id(id)
                .username(username)
                .roles(roles)
                .build();

        String result = dto.toString();
        assertThat(result).contains(String.valueOf(id), username);
        roles.forEach(role -> assertThat(result).contains(role));
    }
}

