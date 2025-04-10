package com.renewsim.backend.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("JwtUtils Tests")
class JwtUtilsTest {

    private static final String SECRET_KEY = "VGhpcyBpcyBhIHZlcnkgc2VjdXJlIHNlY3JldCBmb3IgdGVzdGluZw==";
    private static final long EXPIRATION_TIME = 3600000;
    private static final String TEST_USERNAME = "testuser";
    private static final Set<String> TEST_ROLES = Set.of("USER", "ADMIN");
    private static final Set<String> TEST_SCOPES = Set.of("read:simulations", "write:simulations");

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(SECRET_KEY, EXPIRATION_TIME);
    }

    @Test
    @DisplayName("should generate a valid token")
    void shouldGenerateValidToken() {
        String token = generateTestToken();

        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token), "Generated token should be valid");
    }

    @Test
    @DisplayName("should validate a valid token")
    void shouldValidateValidToken() {
        String token = generateTestToken();

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    @DisplayName("should invalidate an invalid token")
    void shouldInvalidateInvalidToken() {
        String invalidToken = "invalid.token.value";

        assertFalse(jwtUtils.validateToken(invalidToken));
    }

    @Test
    @DisplayName("should extract username from token")
    void shouldExtractUsername() {
        String token = generateTestToken();

        String username = jwtUtils.extractUsername(token);

        assertEquals(TEST_USERNAME, username);
    }

    @Test
    @DisplayName("should extract roles from token")
    void shouldExtractRoles() {
        String token = generateTestToken();

        Set<String> roles = jwtUtils.extractRoles(token);

        assertEquals(TEST_ROLES, roles);
    }

    @Test
    @DisplayName("should extract scopes from token")
    void shouldExtractScopes() {
        String token = generateTestToken();

        String scopes = jwtUtils.extractScopes(token);

        for (String scope : TEST_SCOPES) {
            assertTrue(scopes.contains(scope), "Scopes should contain: " + scope);
        }
    }

    @Test
    @DisplayName("should throw exception when extracting from invalid token")
    void shouldThrowExceptionWhenExtractingFromInvalidToken() {
        String invalidToken = "invalid.token.value";

        assertThrows(JwtException.class, () -> jwtUtils.extractUsername(invalidToken));
        assertThrows(JwtException.class, () -> jwtUtils.extractRoles(invalidToken));
        assertThrows(JwtException.class, () -> jwtUtils.extractScopes(invalidToken));
    }

    @Test
    @DisplayName("should extract JWT from Authorization header")
    void shouldExtractJwtFromHeader() {
        String token = generateTestToken();
        HttpServletRequest request = mockRequestWithToken(token);

        String extractedToken = jwtUtils.getJwtFromHeader(request);

        assertEquals(token, extractedToken);
    }

    @Test
    @DisplayName("should return null if Authorization header is missing")
    void shouldReturnNullIfAuthorizationHeaderIsMissing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        String extractedToken = jwtUtils.getJwtFromHeader(request);

        assertNull(extractedToken);
    }

    @Test
    @DisplayName("should return null if Authorization header does not start with Bearer")
    void shouldReturnNullIfAuthorizationHeaderInvalid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        String extractedToken = jwtUtils.getJwtFromHeader(request);

        assertNull(extractedToken);
    }

    private String generateTestToken() {
        return jwtUtils.generateToken(TEST_USERNAME, TEST_ROLES, TEST_SCOPES);
    }

    private HttpServletRequest mockRequestWithToken(String token) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        return request;
    }
}
