package com.renewsim.backend.user.dto;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseDTOTest {

    @Test
    void shouldCreateUserResponseDTOUsingConstructor() {

        Long id = 1L;
        String username = "testuser";
        Set<String> roles = Set.of("ADMIN", "USER");

        UserResponseDTO dto = new UserResponseDTO(id, username, roles);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getRoles()).containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    void shouldCreateUserResponseDTOUsingBuilder() {

        Long id = 2L;
        String username = "builderUser";
        Set<String> roles = Set.of("USER");

        UserResponseDTO dto = UserResponseDTO.builder()
                .id(id)
                .username(username)
                .roles(roles)
                .build();

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getRoles()).containsExactly("USER");
    }

    @Test
    void shouldVerifyEqualsAndHashCode() {

        UserResponseDTO dto1 = UserResponseDTO.builder()
                .id(3L)
                .username("user")
                .roles(Set.of("ADMIN"))
                .build();

        UserResponseDTO dto2 = UserResponseDTO.builder()
                .id(3L)
                .username("user")
                .roles(Set.of("ADMIN"))
                .build();

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldReturnToStringContainingFieldValues() {

        UserResponseDTO dto = UserResponseDTO.builder()
                .id(4L)
                .username("user")
                .roles(Set.of("USER"))
                .build();

        String result = dto.toString();
        assertThat(result).contains("4");
        assertThat(result).contains("user");
        assertThat(result).contains("USER");
    }
}

