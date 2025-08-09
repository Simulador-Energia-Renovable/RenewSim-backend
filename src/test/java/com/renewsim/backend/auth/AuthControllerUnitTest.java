package com.renewsim.backend.auth;

import com.renewsim.backend.auth.application.service.AuthServiceImpl;
import com.renewsim.backend.auth.web.controller.AuthController;
import com.renewsim.backend.auth.web.dto.AuthRequestDTO;
import com.renewsim.backend.auth.web.dto.AuthResponseDTO;
import com.renewsim.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("AuthController Unit Tests")
class AuthControllerUnitTest {

    private AuthServiceImpl authService;
    private AuthController authController;
    private AuthRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        authService = mock(AuthServiceImpl.class);
        authController = new AuthController(authService, null);
        requestDTO = new AuthRequestDTO("testuser", "password123");
    }

    @Test
    @DisplayName("Should register user and return AuthResponseDTO")
    void testShouldRegisterUser() {

        AuthResponseDTO expectedResponse = new AuthResponseDTO("sample.jwt.token", "testuser", Set.of("USER"));

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        when(authService.registerUserAndReturnAuth("testuser", "password123")).thenReturn(expectedResponse);

        var responseEntity = authController.register(requestDTO, mockResponse);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);

    }

    @Test
    @DisplayName("Should login user and return AuthResponseDTO")
    void testShouldLoginUser() {

        String token = "sample.jwt.token";

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        User mockUser = mock(User.class);

        when(authService.authenticate("testuser", "password123")).thenReturn(token);
        when(authService.findByUsername("testuser")).thenReturn(java.util.Optional.of(mockUser));
        when(mockUser.getUsername()).thenReturn("testuser");

        var mockRole = mock(com.renewsim.backend.role.Role.class);
        when(mockRole.getName()).thenReturn(com.renewsim.backend.role.RoleName.USER);
        when(mockUser.getRoles()).thenReturn(Set.of(mockRole));

        var responseEntity = authController.login(requestDTO, mockResponse);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(responseEntity.getBody().getToken()).isEqualTo(token);
        assertThat(responseEntity.getBody().getUsername()).isEqualTo("testuser");
        assertThat(responseEntity.getBody().getRoles()).contains("USER");

    }

    @Test
    @DisplayName("Should logout user and clear JWT cookie")
    void testShouldLogoutUser() {

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        var responseEntity = authController.logout(mockResponse);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);

    }

    @Test
    @DisplayName("Should throw ResponseStatusException when user not found during login")
    void testShouldThrowExceptionWhenUserNotFoundOnLogin() {

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        when(authService.authenticate("nonexistentuser", "password123"))
                .thenReturn("sample.jwt.token");
        when(authService.findByUsername("nonexistentuser"))
                .thenReturn(java.util.Optional.empty());

        var exception = org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> authController.login(
                        new com.renewsim.backend.auth.web.dto.AuthRequestDTO("nonexistentuser", "password123"),
                        mockResponse));

        assertThat(exception.getStatusCode().value()).isEqualTo(404);
        assertThat(exception.getReason()).isEqualTo("User not found");
    }

}
