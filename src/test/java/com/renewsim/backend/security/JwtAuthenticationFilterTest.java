package com.renewsim.backend.security;

import com.renewsim.backend.auth.application.port.out.JwtTokenValidator;
import com.renewsim.backend.auth.application.port.out.UserAccountLoader;
import com.renewsim.backend.auth.infrastructure.security.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Authentication Filter Tests")
class JwtAuthenticationFilterTest {

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String INVALID_TOKEN = "invalid.jwt.token";
    private static final String TEST_USERNAME = "testuser";
    private static final String BEARER_TOKEN = "Bearer " + VALID_TOKEN;
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Mock
    private JwtTokenValidator jwtTokenValidator;

    @Mock
    private UserAccountLoader userDetailsLoader;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenValidator, userDetailsLoader);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        testUserDetails = User.builder()
            .username(TEST_USERNAME)
            .password("password")
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
            .build();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should authenticate user with valid JWT token")
    void shouldAuthenticateUserWithValidToken() throws ServletException, IOException {
        request.addHeader(AUTHORIZATION_HEADER, BEARER_TOKEN);
        request.setRequestURI("/api/v1/protected-resource");
        
        when(jwtTokenValidator.isTokenValid(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenValidator.extractUsername(VALID_TOKEN)).thenReturn(TEST_USERNAME);
        when(jwtTokenValidator.isTokenValidForUser(VALID_TOKEN, testUserDetails)).thenReturn(true);
        when(userDetailsLoader.loadUserByUsername(TEST_USERNAME)).thenReturn(testUserDetails);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(TEST_USERNAME);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
            .hasSize(1)
            .extracting("authority")
            .contains("ROLE_USER");
        
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenValidator).isTokenValid(VALID_TOKEN);
        verify(jwtTokenValidator).extractUsername(VALID_TOKEN);
        verify(jwtTokenValidator).isTokenValidForUser(VALID_TOKEN, testUserDetails);
        verify(userDetailsLoader).loadUserByUsername(TEST_USERNAME);
    }

    @Test
    @DisplayName("Should not authenticate with invalid JWT token")
    void shouldNotAuthenticateWithInvalidToken() throws ServletException, IOException {
        request.addHeader(AUTHORIZATION_HEADER, "Bearer " + INVALID_TOKEN);
        request.setRequestURI("/api/v1/protected-resource");
        
        when(jwtTokenValidator.isTokenValid(INVALID_TOKEN)).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenValidator).isTokenValid(INVALID_TOKEN);
        verify(jwtTokenValidator, never()).extractUsername(any());
        verify(userDetailsLoader, never()).loadUserByUsername(any());
    }

    @Test
    @DisplayName("Should continue filter chain when no token is provided")
    void shouldContinueFilterChainWhenNoTokenProvided() throws ServletException, IOException {
        request.setRequestURI("/api/v1/protected-resource");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenValidator, never()).isTokenValid(any());
        verify(userDetailsLoader, never()).loadUserByUsername(any());
    }

    @Test
    @DisplayName("Should handle malformed Authorization header gracefully")
    void shouldHandleMalformedAuthorizationHeader() throws ServletException, IOException {
        request.addHeader(AUTHORIZATION_HEADER, "InvalidFormat token");
        request.setRequestURI("/api/v1/protected-resource");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenValidator, never()).isTokenValid(any());
    }

    @Test
    @DisplayName("Should skip authentication for public endpoints")
    void shouldSkipAuthenticationForPublicEndpoints() throws ServletException, IOException {
        request.setRequestURI("/api/v1/auth/login");
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        verify(jwtTokenValidator, never()).isTokenValid(any());
        reset(jwtTokenValidator, filterChain);

        request.setRequestURI("/api/v1/auth/register");
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        verify(jwtTokenValidator, never()).isTokenValid(any());
        reset(jwtTokenValidator, filterChain);

        request.setRequestURI("/api/v1/health");
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        verify(jwtTokenValidator, never()).isTokenValid(any());
        reset(jwtTokenValidator, filterChain);

        request.setRequestURI("/swagger-ui/index.html");
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        verify(jwtTokenValidator, never()).isTokenValid(any());
        reset(jwtTokenValidator, filterChain);

        request.setRequestURI("/api/v1/users");
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle username not found exception gracefully")
    void shouldHandleUsernameNotFoundExceptionGracefully() throws ServletException, IOException {
        request.addHeader(AUTHORIZATION_HEADER, BEARER_TOKEN);
        request.setRequestURI("/api/v1/protected-resource");
        
        when(jwtTokenValidator.isTokenValid(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenValidator.extractUsername(VALID_TOKEN)).thenReturn(TEST_USERNAME);
        when(userDetailsLoader.loadUserByUsername(TEST_USERNAME))
            .thenThrow(new UsernameNotFoundException("User not found"));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

       assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenValidator).isTokenValid(VALID_TOKEN);
        verify(jwtTokenValidator).extractUsername(VALID_TOKEN);
        verify(userDetailsLoader).loadUserByUsername(TEST_USERNAME);
        verify(jwtTokenValidator, never()).isTokenValidForUser(any(), any());
    }

    @Test
    @DisplayName("Should handle authentication exception gracefully")
    void shouldHandleAuthenticationExceptionGracefully() throws ServletException, IOException {
        request.addHeader(AUTHORIZATION_HEADER, BEARER_TOKEN);
        request.setRequestURI("/api/v1/protected-resource");
        
        when(jwtTokenValidator.isTokenValid(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenValidator.extractUsername(VALID_TOKEN)).thenReturn(TEST_USERNAME);
        when(userDetailsLoader.loadUserByUsername(TEST_USERNAME)).thenReturn(testUserDetails);
        when(jwtTokenValidator.isTokenValidForUser(VALID_TOKEN, testUserDetails)).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenValidator).isTokenValid(VALID_TOKEN);
        verify(jwtTokenValidator).extractUsername(VALID_TOKEN);
        verify(userDetailsLoader).loadUserByUsername(TEST_USERNAME);
        verify(jwtTokenValidator).isTokenValidForUser(VALID_TOKEN, testUserDetails);
    }

    @Test
    @DisplayName("Should not process token when authentication already exists")
    void shouldNotProcessTokenWhenAuthenticationExists() throws ServletException, IOException {
        request.addHeader(AUTHORIZATION_HEADER, BEARER_TOKEN);
        request.setRequestURI("/api/v1/protected-resource");
        
        SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "existingUser", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            )
        );
        
        when(jwtTokenValidator.isTokenValid(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenValidator.extractUsername(VALID_TOKEN)).thenReturn(TEST_USERNAME);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("existingUser");
        
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenValidator).isTokenValid(VALID_TOKEN);
        verify(jwtTokenValidator).extractUsername(VALID_TOKEN);
        verify(userDetailsLoader, never()).loadUserByUsername(any());
        verify(jwtTokenValidator, never()).isTokenValidForUser(any(), any());
    }

    @Test
    @DisplayName("Should handle empty token after Bearer prefix")
    void shouldHandleEmptyTokenAfterBearerPrefix() throws ServletException, IOException {
        request.addHeader(AUTHORIZATION_HEADER, "Bearer   ");
        request.setRequestURI("/api/v1/protected-resource");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenValidator, never()).isTokenValid(any());
    }

    @Test
    @DisplayName("Should handle runtime exceptions during token processing")
    void shouldHandleRuntimeExceptionsDuringTokenProcessing() throws ServletException, IOException {
        request.addHeader(AUTHORIZATION_HEADER, BEARER_TOKEN);
        request.setRequestURI("/api/v1/protected-resource");
        
        when(jwtTokenValidator.isTokenValid(VALID_TOKEN)).thenThrow(new RuntimeException("Unexpected error"));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenValidator).isTokenValid(VALID_TOKEN);
        verify(userDetailsLoader, never()).loadUserByUsername(any());
    }
}