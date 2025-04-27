package com.renewsim.backend.auth;

import com.renewsim.backend.config.SpringContext;
import com.renewsim.backend.exception.UnauthorizedException;
import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.security.UserDetailsImpl;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AuthUtils Tests")
class AuthUtilsTest {

    private static final String TEST_USERNAME = "testuser";

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("should return User from UserDetailsImpl when authenticated")
    void shouldReturnUserFromUserDetails() {

        User user = createTestUserWithRole(RoleName.USER);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        authenticate(userDetails);

        User result = AuthUtils.getCurrentUser();

        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.getUsername());
    }

    @Test
    @DisplayName("should return User from JWT principal when authenticated")
    void shouldReturnUserFromJwt() {

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn(TEST_USERNAME);
        authenticate(jwt);

        try (MockedStatic<SpringContext> springContextMock = mockStatic(SpringContext.class)) {
            UserRepository mockRepo = mock(UserRepository.class);
            User user = createTestUser();
            when(mockRepo.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

            springContextMock.when(() -> SpringContext.getBean(UserRepository.class)).thenReturn(mockRepo);

            User result = AuthUtils.getCurrentUser();

            assertNotNull(result);
            assertEquals(TEST_USERNAME, result.getUsername());
            verify(mockRepo).findByUsername(TEST_USERNAME);
        }
    }

    @Test
    @DisplayName("should throw UnauthorizedException when JWT user is not found")
    void shouldThrowWhenJwtUserNotFound() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn(TEST_USERNAME);
        authenticate(jwt);

        try (MockedStatic<SpringContext> springContextMock = mockStatic(SpringContext.class)) {
            UserRepository mockRepo = mock(UserRepository.class);
            when(mockRepo.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
            springContextMock.when(() -> SpringContext.getBean(UserRepository.class)).thenReturn(mockRepo);

            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    AuthUtils::getCurrentUser);

            assertEquals("User not found", exception.getMessage());
            verify(mockRepo).findByUsername(TEST_USERNAME);
        }
    }

    @Test
    @DisplayName("should throw UnauthorizedException when authentication is null")
    void shouldThrowWhenAuthenticationIsNull() {
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                AuthUtils::getCurrentUser);

        assertEquals("User is not authenticated", exception.getMessage());
    }

    @Test
    @DisplayName("should throw UnauthorizedException when principal is invalid type")
    void shouldThrowWhenPrincipalIsInvalidType() {
        authenticate("invalidPrincipal");

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                AuthUtils::getCurrentUser);

        assertEquals("Invalid authentication principal", exception.getMessage());
    }

    @Test
    @DisplayName("should throw UnauthorizedException when authentication is not authenticated")
    void shouldThrowWhenAuthenticationIsNotAuthenticated() {
        Authentication unauthenticatedAuth = new UsernamePasswordAuthenticationToken("somePrincipal", null);
        SecurityContextHolder.getContext().setAuthentication(unauthenticatedAuth);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                AuthUtils::getCurrentUser);

        assertEquals("User is not authenticated", exception.getMessage());
    }

    @Test
    @DisplayName("should throw UnsupportedOperationException when trying to instantiate AuthUtils")
    void shouldThrowExceptionWhenInstantiatingAuthUtils() throws Exception {
        var constructor = AuthUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance);

        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof UnsupportedOperationException);
        assertEquals("Utility class should not be instantiated", cause.getMessage());
    }


    private void authenticate(Object principal) {
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, Set.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private User createTestUserWithRole(RoleName roleName) {
        Role role = new Role();
        role.setName(roleName);

        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setRoles(Set.of(role));

        return user;
    }

    private User createTestUser() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        return user;
    }
}
