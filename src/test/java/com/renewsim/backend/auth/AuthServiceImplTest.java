package com.renewsim.backend.auth;

import com.renewsim.backend.auth.application.port.out.UserAccountLoader;
import com.renewsim.backend.auth.application.service.AuthServiceImpl;
import com.renewsim.backend.auth.domain.AuthenticatedUser;
import com.renewsim.backend.auth.infrastructure.security.JwtUtils;
import com.renewsim.backend.auth.web.dto.AuthRequestDTO;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Test
    void login_ok_returnsToken() {
        UserAccountLoader loader = mock(UserAccountLoader.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);

        when(loader.loadByUsername("alice"))
                .thenReturn(Optional.of(new AuthenticatedUser("alice", "hashed", Set.of("ADMIN"), Set.of("simulation:read"))));
        when(encoder.matches("secret", "hashed")).thenReturn(true);
        when(jwtUtils.generateToken(any(), any(), any())).thenReturn("token");
        when(jwtUtils.getExpirationSeconds()).thenReturn(3600L);

        var svc = new AuthServiceImpl(loader, encoder, jwtUtils);

        var dto = svc.login(AuthRequestDTO.builder().username("alice").password("secret").build());

        assertThat(dto.getToken()).isEqualTo("token");
        assertThat(dto.getUsername()).isEqualTo("alice");
        verify(jwtUtils, times(1)).generateToken(any(), any(), any());
    }

    @Test
    void login_invalidCredentials_throws() {
        UserAccountLoader loader = mock(UserAccountLoader.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtUtils jwtUtils = mock(JwtUtils.class);

        when(loader.loadByUsername("alice"))
                .thenReturn(Optional.of(new AuthenticatedUser("alice", "hashed", Set.of("ADMIN"), Set.of())));
        when(encoder.matches("wrong", "hashed")).thenReturn(false);

        var svc = new AuthServiceImpl(loader, encoder, jwtUtils);

        assertThatThrownBy(() -> svc.login(AuthRequestDTO.builder().username("alice").password("wrong").build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid username or password");
    }
}

