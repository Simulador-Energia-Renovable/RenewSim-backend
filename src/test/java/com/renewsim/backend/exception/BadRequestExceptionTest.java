package com.renewsim.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BadRequestException Test")
class BadRequestExceptionTest {

    @Test
    @DisplayName("Should create BadRequestException with provided message")
    void testShouldCreateExceptionWithMessage() {
        String expectedMessage = "This is a bad request";

        BadRequestException exception = new BadRequestException(expectedMessage);

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw BadRequestException with correct message")
    void testShouldThrowBadRequestException() {
        String expectedMessage = "Invalid input provided";

        BadRequestException thrown = assertThrows(
            BadRequestException.class,
            () -> { throw new BadRequestException(expectedMessage); }
        );

        assertEquals(expectedMessage, thrown.getMessage());
    }
}
