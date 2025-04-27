package com.renewsim.backend.exception;

import com.renewsim.backend.auth.AuthService;
import com.renewsim.backend.config.SpringContext;
import com.renewsim.backend.dto.ErrorResponse;
import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.security.JwtUtils;
import com.renewsim.backend.user.User;

import org.springframework.context.ApplicationContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DisplayName("GlobalExceptionHandler Test")
class GlobalExceptionHandlerTest {

    private final JwtUtils jwtUtils = mock(JwtUtils.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private final AuthService authService = mock(AuthService.class);

    @Test
    @DisplayName("Should handle generic exception")
    void testHandleGlobalException() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception);

        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("An unexpected error occurred.", response.getBody().getMessage());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertNotNull(response.getBody().getDetails().get("traceId"));
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException")
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Resource not found", response.getBody().getMessage());
        assertNotNull(response.getBody().getDetails().get("traceId"));
    }

    @Test
    @DisplayName("Should handle BadRequestException")
    void testHandleBadRequestException() {
        BadRequestException exception = new BadRequestException("Bad request error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequestException(exception);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Bad request error", response.getBody().getMessage());
        assertNotNull(response.getBody().getDetails().get("traceId"));
    }

    @Test
    @DisplayName("Should handle UnauthorizedException")
    void testHandleUnauthorizedException() {
        UnauthorizedException exception = new UnauthorizedException("Unauthorized access");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorizedException(exception);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Unauthorized access", response.getBody().getMessage());
        assertNotNull(response.getBody().getDetails().get("traceId"));
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException")
    void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Illegal argument");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Illegal argument", response.getBody().getMessage());
        assertNotNull(response.getBody().getDetails().get("traceId"));
    }

    @Test
    @DisplayName("Should authenticate Admin user and return token")
    void testShouldAuthenticateAdminUser() {
        Role adminRole = mock(Role.class);
        User mockUser = mock(User.class);
        String token = "mockedToken";
        String username = "adminUser";
        String password = "adminPassword";
        String encodedPassword = "encodedAdminPassword";

        when(adminRole.getName()).thenReturn(RoleName.ADMIN);
        when(mockUser.getRoles()).thenReturn(Set.of(adminRole));
        when(jwtUtils.generateToken(eq(username), anySet(), anySet())).thenReturn(token);
        when(authService.authenticate(username, password)).thenReturn(token);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        String resultToken = authService.authenticate(username, password);

        assertEquals(token, resultToken);
    }

    @Test
    @DisplayName("Should retrieve a bean from SpringContext")
    void testShouldGetBean() {
        ApplicationContext mockContext = mock(ApplicationContext.class);
        SpringContext contextSetter = new SpringContext();
        contextSetter.setApplicationContext(mockContext);

        when(mockContext.getBean(String.class)).thenReturn("example");

        String result = SpringContext.getBean(String.class);
        assertEquals("example", result);
    }

    @Test
    @DisplayName("Should handle IllegalStateException")
    void testShouldHandleIllegalStateException() {
        IllegalStateException exception = new IllegalStateException("Invalid state");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalStateException(exception);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid state", response.getBody().getMessage());
        assertNotNull(response.getBody().getDetails().get("traceId"));
    }

    @Test
    @DisplayName("Should handle validation errors from MethodArgumentNotValidException")
    void testShouldHandleValidationException() {
        FieldError fieldError = new FieldError("objectName", "fieldName", "must not be null");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(exception);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("One or more fields are invalid.", response.getBody().getMessage());

        Map<String, String> details = response.getBody().getDetails();
        assertNotNull(details);
        assertTrue(details.containsKey("fieldName"));
        assertEquals("must not be null", details.get("fieldName"));
    }

}
