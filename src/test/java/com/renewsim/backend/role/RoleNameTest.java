package com.renewsim.backend.role;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoleNameTest {

    @Test
    void shouldReturnCorrectDisplayNameForEachRole() {
        assertEquals("User", RoleName.USER.getDisplayName(), "USER role display name should be 'User'");
        assertEquals("Administrator", RoleName.ADMIN.getDisplayName(), "ADMIN role display name should be 'Administrator'");
    }

    @Test
    void shouldContainExpectedNumberOfRoles() {
        int expectedNumberOfRoles = 2;
        assertEquals(expectedNumberOfRoles, RoleName.values().length, "Enum should contain 2 roles");
    }
}

