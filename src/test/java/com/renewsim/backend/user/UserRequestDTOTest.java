package com.renewsim.backend.user;

import org.junit.jupiter.api.Test;

import com.renewsim.backend.user.dto.UserRequestDTO;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserRequestDTOTest {

    @Test
    void shouldCreateUserRequestDTOUsingConstructor() {

        String username = "testuser";
        String password = "securepassword";
        Set<String> roles = Set.of("ADMIN", "USER");

        UserRequestDTO dto = new UserRequestDTO(username, password, roles);

        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getPassword()).isEqualTo(password);
        assertThat(dto.getRoles()).containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    void shouldCreateUserRequestDTOUsingBuilder() {

        String username = "builderUser";
        String password = "builderPassword";
        Set<String> roles = Set.of("USER");

        UserRequestDTO dto = UserRequestDTO.builder()
                .username(username)
                .password(password)
                .roles(roles)
                .build();

        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getPassword()).isEqualTo(password);
        assertThat(dto.getRoles()).containsExactly("USER");
    }

    @Test
    void shouldVerifyEqualsAndHashCode() {

        UserRequestDTO dto1 = UserRequestDTO.builder()
                .username("user")
                .password("pass")
                .roles(Set.of("ADMIN"))
                .build();

        UserRequestDTO dto2 = UserRequestDTO.builder()
                .username("user")
                .password("pass")
                .roles(Set.of("ADMIN"))
                .build();

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldReturnToStringContainingFieldValues() {

        UserRequestDTO dto = UserRequestDTO.builder()
                .username("user")
                .password("pass")
                .roles(Set.of("USER"))
                .build();

        String result = dto.toString();
        assertThat(result).contains("user");
        assertThat(result).contains("pass");
        assertThat(result).contains("USER");
    }
}
