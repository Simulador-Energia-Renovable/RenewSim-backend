package com.renewsim.backend.security;

import com.renewsim.backend.auth.AuthUtils;
import com.renewsim.backend.config.SpringContext;
import com.renewsim.backend.exception.UnauthorizedException;
import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
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
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("getCurrentUser()")
    class GetCurrentUserTests {

        @Test
        @DisplayName("should return User from UserDetailsImpl when authenticated")
        void shouldReturnUserFromUserDetails() {
            Role mockRole = new Role();
            mockRole.setName(RoleName.USER);

            User mockUser = new User();
            mockUser.setUsername(TEST_USERNAME);
            mockUser.setRoles(Set.of(mockRole));

            UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
            var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);

            User result = AuthUtils.getCurrentUser();

            assertNotNull(result);
            assertEquals(TEST_USERNAME, result.getUsername());
        }

        @Test
        @DisplayName("should return User from JWT principal when authenticated")
        void shouldReturnUserFromJwt() {
            Jwt jwt = mock(Jwt.class);
            when(jwt.getClaimAsString("sub")).thenReturn(TEST_USERNAME);

            User mockUser = new User();
            mockUser.setUsername(TEST_USERNAME);

            Authentication auth = new UsernamePasswordAuthenticationToken(jwt, null, Set.of());
            SecurityContextHolder.getContext().setAuthentication(auth);

            try (MockedStatic<SpringContext> springContextMock = mockStatic(SpringContext.class)) {
                UserRepository mockRepo = mock(UserRepository.class);
                springContextMock.when(() -> SpringContext.getBean(UserRepository.class)).thenReturn(mockRepo);
                when(mockRepo.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(mockUser));

                User result = AuthUtils.getCurrentUser();

                assertNotNull(result);
                assertEquals(TEST_USERNAME, result.getUsername());
            }
        }

        @Test
        @DisplayName("should throw UnauthorizedException when authentication is null")
        void shouldThrowWhenAuthenticationIsNull() {
            SecurityContextHolder.clearContext();

            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    AuthUtils::getCurrentUser);

            assertEquals("User is not authenticated", exception.getMessage());
        }

        @Test
        @DisplayName("should throw UnauthorizedException when principal is unknown type")
        void shouldThrowWhenPrincipalIsInvalidType() {

            Authentication auth = new UsernamePasswordAuthenticationToken("someString", null, Set.of());
            SecurityContextHolder.getContext().setAuthentication(auth);

            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    AuthUtils::getCurrentUser);

            assertEquals("Invalid authentication principal", exception.getMessage());
        }

        @Test
        @DisplayName("should throw UnauthorizedException when authentication is not authenticated")
        void shouldThrowWhenAuthenticationIsNotAuthenticated() {
            Authentication unauthenticatedAuth = new UsernamePasswordAuthenticationToken("someString", null);
            SecurityContextHolder.getContext().setAuthentication(unauthenticatedAuth);

            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    AuthUtils::getCurrentUser);

            assertEquals("User is not authenticated", exception.getMessage());
        }
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

}
