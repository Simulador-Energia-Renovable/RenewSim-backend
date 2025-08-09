package com.renewsim.backend.auth;

import com.renewsim.backend.auth.application.service.AuthServiceImpl;
import com.renewsim.backend.auth.infrastructure.security.JwtUtils;
import com.renewsim.backend.auth.web.dto.AuthResponseDTO;
import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.role.RoleService;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;
    private RoleService roleService;
    private AuthServiceImpl authService;

    private final String username = "testuser";
    private final String password = "password123";
    private final String encodedPassword = "encodedPassword";
    private final String token = "sample.jwt.token";

    private Role mockRole;
    private User mockUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtils = mock(JwtUtils.class);
        roleService = mock(RoleService.class);

        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtUtils, roleService);

        mockRole = mock(Role.class);
        when(mockRole.getName()).thenReturn(RoleName.USER);

        mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn(encodedPassword);
        when(mockUser.getRoles()).thenReturn(Set.of(mockRole));
    }

    @Test
    @DisplayName("Should register new user and return AuthResponseDTO")
    void testShouldRegisterNewUser() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(roleService.getRoleByName(RoleName.USER)).thenReturn(mockRole);
        when(jwtUtils.generateToken(eq(username), anySet(), anySet())).thenReturn(token);

        AuthResponseDTO response = authService.registerUserAndReturnAuth(username, password);

        assertThat(response.getToken()).isEqualTo(token);
        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(response.getRoles()).containsExactly("USER");

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering existing user")
    void testShouldThrowExceptionWhenUserAlreadyExists() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> authService.registerUserAndReturnAuth(username, password))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("El nombre de usuario ya existe")
                .extracting("statusCode")
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Should authenticate user and return token")
    void testShouldAuthenticateUser() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtils.generateToken(eq(username), anySet(), anySet())).thenReturn(token);

        String resultToken = authService.authenticate(username, password);

        assertThat(resultToken).isEqualTo(token);
    }

    @Test
    @DisplayName("Should throw exception when user not found during authentication")
    void testShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(username, password))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Credenciales inválidas")
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should throw exception when password is invalid")
    void testShouldThrowExceptionWhenPasswordInvalid() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        assertThatThrownBy(() -> authService.authenticate(username, password))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Credenciales inválidas")
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should authenticate user with ADMIN role and generate full scope token")
    void testShouldAuthenticateAdminUser() {
        when(mockRole.getName()).thenReturn(RoleName.ADMIN);
        when(mockUser.getRoles()).thenReturn(Set.of(mockRole));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtils.generateToken(eq(username), anySet(),
                argThat(scopes -> scopes.contains("manage:users") && scopes.contains("export:simulations"))))
                .thenReturn(token);

        String resultToken = authService.authenticate(username, password);

        assertThat(resultToken).isEqualTo(token);
    }

}
