package com.renewsim.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserNotFoundException Test")
class UserNotFoundExceptionTest {

    @Test
    @DisplayName("Should create UserNotFoundException with provided user ID")
    void testShouldCreateExceptionWithUserId() {
        Long userId = 42L;

        UserNotFoundException exception = new UserNotFoundException(userId);

        assertNotNull(exception);
        assertEquals("User not found with id: " + userId, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException with correct message")
    void testShouldThrowUserNotFoundException() {
        Long userId = 99L;

        UserNotFoundException thrown = assertThrows(
            UserNotFoundException.class,
            () -> { throw new UserNotFoundException(userId); }
        );

        assertEquals("User not found with id: " + userId, thrown.getMessage());
    }
}

