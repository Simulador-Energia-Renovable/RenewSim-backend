package com.renewsim.backend.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserRequestDTO Test")
class UserRequestDTOTest {

    private String username;
    private String password;
    private Set<String> roles;

    @BeforeEach
    void setUp() {
        username = "testuser";
        password = "securepassword";
        roles = Set.of("ADMIN", "USER");
    }

    @Test
    @DisplayName("Should create UserRequestDTO using constructor")
    void shouldCreateUserRequestDTOUsingConstructor() {
        UserRequestDTO dto = new UserRequestDTO(username, password, roles);

        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getPassword()).isEqualTo(password);
        assertThat(dto.getRoles()).containsExactlyInAnyOrderElementsOf(roles);
    }

    @Test
    @DisplayName("Should create UserRequestDTO using builder")
    void shouldCreateUserRequestDTOUsingBuilder() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .username(username)
                .password(password)
                .roles(roles)
                .build();

        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getPassword()).isEqualTo(password);
        assertThat(dto.getRoles()).containsExactlyInAnyOrderElementsOf(roles);
    }

    @Test
    @DisplayName("Should verify equals and hashCode")
    void shouldVerifyEqualsAndHashCode() {
        UserRequestDTO dto1 = UserRequestDTO.builder()
                .username(username)
                .password(password)
                .roles(roles)
                .build();

        UserRequestDTO dto2 = UserRequestDTO.builder()
                .username(username)
                .password(password)
                .roles(roles)
                .build();

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should return toString containing field values")
    void shouldReturnToStringContainingFieldValues() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .username(username)
                .password(password)
                .roles(roles)
                .build();

        String result = dto.toString();
        assertThat(result).contains(username, password);
        roles.forEach(role -> assertThat(result).contains(role));
    }
}
