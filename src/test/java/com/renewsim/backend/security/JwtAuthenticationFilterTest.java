package com.renewsim.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

@DisplayName("JwtAuthenticationFilter Tests")
class JwtAuthenticationFilterTest {

    private static final String TEST_TOKEN = "Bearer valid.token";
    private static final String TEST_USERNAME = "testuser";
    private static final String SECRET_KEY = "VGhpcyBpcyBhIHZlcnkgc2VjdXJlIHNlY3JldCBmb3IgdGVzdGluZw==";

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
        ReflectionTestUtils.setField(jwtUtils, "secret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtils, "expiration", 3600000L);
        ReflectionTestUtils.invokeMethod(jwtUtils, "init");
    }

    @Test
    @DisplayName("should authenticate and set SecurityContext when token is valid")
    void shouldSetAuthenticationWhenTokenIsValid() throws Exception {
        mockValidToken();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(TEST_USERNAME, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("should not authenticate when token is invalid")
    void shouldNotSetAuthenticationWhenTokenIsInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(TEST_TOKEN);
        when(jwtUtils.validateToken(anyString())).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("should skip filter for /api/v1/auth/ paths")
    void shouldSkipFilterForExcludedPaths() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/auth/login");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("should handle exceptions gracefully")
    void shouldHandleExceptionGracefully() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(TEST_TOKEN);
        when(jwtUtils.validateToken(anyString())).thenThrow(new RuntimeException("Unexpected error"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("should not authenticate when token is missing")
    void shouldNotSetAuthenticationWhenTokenIsMissing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    private void mockValidToken() {
        when(request.getHeader("Authorization")).thenReturn(TEST_TOKEN);
        when(jwtUtils.validateToken(anyString())).thenReturn(true);
        when(jwtUtils.extractUsername(anyString())).thenReturn(TEST_USERNAME);
        when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
    }

}
