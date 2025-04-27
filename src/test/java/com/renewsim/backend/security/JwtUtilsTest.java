package com.renewsim.backend.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtUtils Tests")
class JwtUtilsTest {

    private static final String SECRET_KEY = "VGhpcyBpcyBhIHZlcnkgc2VjdXJlIHNlY3JldCB0ZXN0aW5nIHRva2VuIQ==";
    private static final long EXPIRATION_TIME = 3600000;
    private static final String TEST_USERNAME = "testuser";
    private static final Set<String> TEST_ROLES = Set.of("USER", "ADMIN");
    private static final Set<String> TEST_SCOPES = Set.of("read:simulations", "write:simulations");

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
         jwtUtils = new JwtUtils(); 
        ReflectionTestUtils.setField(jwtUtils, "secret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtils, "expiration", EXPIRATION_TIME);
        ReflectionTestUtils.invokeMethod(jwtUtils, "init");
    }

    @Test
    @DisplayName("should generate a valid token")
    void testShouldGenerateValidToken() {
        String token = generateTestToken();

        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token), "Generated token should be valid");
    }

    @Test
    @DisplayName("should validate a valid token")
    void testShouldValidateValidToken() {
        String token = generateTestToken();

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    @DisplayName("should invalidate an invalid token")
    void testShouldInvalidateInvalidToken() {
        String invalidToken = "invalid.token.value";

        assertFalse(jwtUtils.validateToken(invalidToken));
    }

    @Test
    @DisplayName("should extract username from token")
    void testShouldExtractUsername() {
        String token = generateTestToken();

        String username = jwtUtils.extractUsername(token);

        assertEquals(TEST_USERNAME, username);
    }

    @Test
    @DisplayName("should extract roles from token")
    void testShouldExtractRoles() {
        String token = generateTestToken();

        Set<String> roles = jwtUtils.extractRoles(token);

        assertEquals(TEST_ROLES, roles);
    }

    @Test
    @DisplayName("should extract scopes from token")
    void testShouldExtractScopes() {
        String token = generateTestToken();

        String scopes = jwtUtils.extractScopes(token);

        for (String scope : TEST_SCOPES) {
            assertTrue(scopes.contains(scope), "Scopes should contain: " + scope);
        }
    }

    @Test
    @DisplayName("should throw exception when extracting from invalid token")
    void testShouldThrowExceptionWhenExtractingFromInvalidToken() {
        String invalidToken = "invalid.token.value";

        assertThrows(JwtException.class, () -> jwtUtils.extractUsername(invalidToken));
        assertThrows(JwtException.class, () -> jwtUtils.extractRoles(invalidToken));
        assertThrows(JwtException.class, () -> jwtUtils.extractScopes(invalidToken));
    }

    @Test
    @DisplayName("should extract JWT from Authorization header")
    void testShouldExtractJwtFromHeader() {
        String token = generateTestToken();
        HttpServletRequest request = mockRequestWithToken(token);

        String extractedToken = jwtUtils.getJwtFromHeader(request);

        assertEquals(token, extractedToken);
    }

    @Test
    @DisplayName("should return null if Authorization header is missing")
    void testShouldReturnNullIfAuthorizationHeaderIsMissing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        String extractedToken = jwtUtils.getJwtFromHeader(request);

        assertNull(extractedToken);
    }

    @Test
    @DisplayName("should return null if Authorization header does not start with Bearer")
    void testShouldReturnNullIfAuthorizationHeaderInvalid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        String extractedToken = jwtUtils.getJwtFromHeader(request);

        assertNull(extractedToken);
    }

    @Test
    @DisplayName("Should create JWT cookie with correct properties")
    void testShouldCreateJwtCookieWithCorrectProperties() {

        HttpServletResponse response = mock(HttpServletResponse.class);
        String token = "sample.jwt.token";

        jwtUtils.addJwtCookie(response, token);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());

        Cookie cookie = cookieCaptor.getValue();
        assertThat(cookie.getName()).isEqualTo("jwt");
        assertThat(cookie.getValue()).isEqualTo(token);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge()).isEqualTo(7 * 24 * 60 * 60);
    }

    @Test
    @DisplayName("Should clear JWT cookie by setting value to null and max age to 0")
    void testShouldClearJwtCookie() {
  
        HttpServletResponse response = mock(HttpServletResponse.class);

        jwtUtils.clearJwtCookie(response);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());

        Cookie cookie = cookieCaptor.getValue();
        assertThat(cookie.getName()).isEqualTo("jwt");
        assertThat(cookie.getValue()).isNull();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge()).isEqualTo(0);
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
