package com.renewsim.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResourceNotFoundException Test")
class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Should create ResourceNotFoundException with provided message")
    void testShouldCreateExceptionWithMessage() {
        String expectedMessage = "Resource not found";

        ResourceNotFoundException exception = new ResourceNotFoundException(expectedMessage);

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException with correct message")
    void testShouldThrowResourceNotFoundException() {
        String expectedMessage = "Requested resource does not exist";

        ResourceNotFoundException thrown = assertThrows(
            ResourceNotFoundException.class,
            () -> { throw new ResourceNotFoundException(expectedMessage); }
        );

        assertEquals(expectedMessage, thrown.getMessage());
    }
}

