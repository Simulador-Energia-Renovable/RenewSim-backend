package com.renewsim.backend.auth.application;

import com.renewsim.backend.auth.application.port.out.RoleProvider;
import com.renewsim.backend.auth.application.port.out.ScopePolicy;
import com.renewsim.backend.auth.application.port.out.TokenProvider;
import com.renewsim.backend.auth.application.port.out.UserAccountGateway;
import com.renewsim.backend.auth.application.port.out.UserAccountGateway.UserSnapshot;
import com.renewsim.backend.auth.application.service.AuthServiceImpl;
import com.renewsim.backend.auth.domain.AuthenticatedUser;

import com.renewsim.backend.auth.web.dto.AuthRequestDTO;
import com.renewsim.backend.auth.web.dto.AuthResponseDTO;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.testutil.UnitTestBase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest extends UnitTestBase {

    @Mock
    private UserAccountGateway userAccountGateway;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private RoleProvider roleProvider;

    @Mock
    private ScopePolicy scopePolicy;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private AuthRequestDTO loginReq;
    private AuthenticatedUser john;

    private UserSnapshot snapshot;

    @BeforeEach
    void setUp() {
        loginReq = new AuthRequestDTO("john", "secret");
        john = new AuthenticatedUser("john", Set.of("USER"), Set.of("simulation:read"));
        snapshot = new UserSnapshot("john", "$2a$10$abcdefgHashed", Set.of(RoleName.USER));
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(tokenProvider, roleProvider, userAccountGateway);
    }

    @Test
    @DisplayName("login → returns AuthResponseDTO with token when credentials are valid")
    void login_ok() {

        when(userAccountGateway.findByUsername("john")).thenReturn(Optional.of(snapshot));
        when(passwordEncoder.matches("secret", "$2a$10$abcdefgHashed")).thenReturn(true);
        when(scopePolicy.scopesFor(RoleName.USER)).thenReturn(Set.of("read"));
        when(tokenProvider.generate(any(AuthenticatedUser.class))).thenReturn("jwt-token");
        when(tokenProvider.expiresInSeconds()).thenReturn(3600L);

        AuthResponseDTO res = authService.login(loginReq);

        assertThat(res.getUsername()).isEqualTo("john");
        assertThat(res.getToken()).isEqualTo("jwt-token");
        assertThat(res.getRoles()).containsExactlyInAnyOrder("USER");
        assertThat(res.getScopes()).containsExactlyInAnyOrder("read");

        verify(userAccountGateway).findByUsername("john");
        verify(passwordEncoder).matches("secret", "$2a$10$abcdefgHashed");
        verify(scopePolicy).scopesFor(RoleName.USER);
        verify(tokenProvider).generate(argThat(au -> au.username().equals("john") &&
                au.roles().contains("USER") &&
                au.scopes().contains("read")));
        verify(tokenProvider).expiresInSeconds();

        verifyNoMoreInteractions(roleProvider);
    }

    @Test
    @DisplayName("login → throws on invalid credentials")
    void login_invalid() {
        when(userAccountGateway.findByUsername("john")).thenReturn(Optional.of(snapshot));
        when(passwordEncoder.matches("bad", "$2a$10$abcdefgHashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new AuthRequestDTO("john", "bad")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid username or password");

        verify(userAccountGateway).findByUsername("john");
        verify(passwordEncoder).matches("bad", "$2a$10$abcdefgHashed");
        verifyNoInteractions(scopePolicy, tokenProvider);
    }

    @Test
    @DisplayName("register → throws when username already exists")
    void register_username_exists() {
        when(userAccountGateway.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new AuthRequestDTO("john", "secret")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Username already exists");

        verify(userAccountGateway).existsByUsername("john");
        verifyNoInteractions(roleProvider, scopePolicy, passwordEncoder, tokenProvider);
    }

    @Test
    @DisplayName("register → creates user with default role, builds token and scopes")
    void register_ok() {
        when(userAccountGateway.existsByUsername("john")).thenReturn(false);
        when(roleProvider.defaultRole()).thenReturn(RoleName.USER);
        when(passwordEncoder.encode("secret")).thenReturn("$2a$10$hash");
        when(scopePolicy.scopesFor(RoleName.USER)).thenReturn(Set.of("simulation:read"));
        when(tokenProvider.generate(any(AuthenticatedUser.class))).thenReturn("jwt-token");
        when(tokenProvider.expiresInSeconds()).thenReturn(3600L);

        Instant before = Instant.now();

        var res = authService.register(new AuthRequestDTO("john", "secret"));

        Instant after = Instant.now();

        assertThat(res.getUsername()).isEqualTo("john");
        assertThat(res.getToken()).isEqualTo("jwt-token");
        assertThat(res.getRoles()).containsExactly("USER");
        assertThat(res.getScopes()).containsExactly("simulation:read");
        assertThat(res.getExpiresAt()).isAfterOrEqualTo(before).isBeforeOrEqualTo(after.plusSeconds(3600));

        verify(userAccountGateway).existsByUsername("john");
        verify(roleProvider).defaultRole();
        verify(passwordEncoder).encode("secret");
        verify(userAccountGateway).createUser("john", "$2a$10$hash", Set.of(RoleName.USER));
        verify(scopePolicy).scopesFor(RoleName.USER);
        verify(tokenProvider).generate(any(AuthenticatedUser.class));
        verify(tokenProvider).expiresInSeconds();
    }

}
