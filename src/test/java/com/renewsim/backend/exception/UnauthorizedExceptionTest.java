package com.renewsim.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UnauthorizedException Test")
class UnauthorizedExceptionTest {

    @Test
    @DisplayName("Should create UnauthorizedException with provided message")
    void testShouldCreateExceptionWithMessage() {
        String expectedMessage = "Unauthorized access";

        UnauthorizedException exception = new UnauthorizedException(expectedMessage);

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException with correct message")
    void testShouldThrowUnauthorizedException() {
        String expectedMessage = "Access denied for this operation";

        UnauthorizedException thrown = assertThrows(
            UnauthorizedException.class,
            () -> { throw new UnauthorizedException(expectedMessage); }
        );

        assertEquals(expectedMessage, thrown.getMessage());
    }
}

