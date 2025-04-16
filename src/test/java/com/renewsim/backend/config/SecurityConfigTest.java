package com.renewsim.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private HttpSecurity httpSecurity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setupHttpSecurityMocks();
    }

    private void setupHttpSecurityMocks() {

        SecurityFilterChain securityFilterChain = mock(SecurityFilterChain.class);

        try {
            when(httpSecurity.cors(any())).thenReturn(httpSecurity);
            when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
            when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
            when(httpSecurity.oauth2ResourceServer(any())).thenReturn(httpSecurity);
            when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
            when(httpSecurity.build()).thenAnswer(invocation -> securityFilterChain);
        } catch (Exception e) {
            throw new RuntimeException("Error setting up HttpSecurity mocks", e);
        }
    }

    @Test
    @DisplayName("should create SecurityFilterChain bean and call expected configurations")
    void shouldCreateSecurityFilterChain() throws Exception {
        SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity);

        assertThat(filterChain).isNotNull();

        verify(httpSecurity).cors(any());
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).oauth2ResourceServer(any());
        verify(httpSecurity).build();
    }

    @Test
    @DisplayName("should create PasswordEncoder bean with BCrypt")
    void shouldCreatePasswordEncoder() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder.getClass().getSimpleName()).containsIgnoringCase("BCrypt");
    }



    @Test
    @DisplayName("should create JwtAuthenticationConverter bean")
    void shouldCreateJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();
        assertThat(converter).isNotNull();
    }
}
