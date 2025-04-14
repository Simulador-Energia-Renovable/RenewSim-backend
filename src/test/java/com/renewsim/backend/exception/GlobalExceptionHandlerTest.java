package com.renewsim.backend.exception;

import com.renewsim.backend.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;



@DisplayName("GlobalExceptionHandler Test")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

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

}
