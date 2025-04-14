package com.renewsim.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RoleNotFoundException Test")
class RoleNotFoundExceptionTest {

    @Test
    @DisplayName("Should create RoleNotFoundException with provided role name")
    void testShouldCreateExceptionWithRoleName() {
        String roleName = "ADMIN";

        RoleNotFoundException exception = new RoleNotFoundException(roleName);

        assertNotNull(exception);
        assertEquals("Role not found with name: " + roleName, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException with correct message")
    void testShouldThrowRoleNotFoundException() {
        String roleName = "USER";

        RoleNotFoundException thrown = assertThrows(
            RoleNotFoundException.class,
            () -> { throw new RoleNotFoundException(roleName); }
        );

        assertEquals("Role not found with name: " + roleName, thrown.getMessage());
    }
}

