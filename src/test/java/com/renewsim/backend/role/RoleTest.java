package com.renewsim.backend.role;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void shouldCreateRoleWithName() {
        Role role = new Role(RoleName.ADMIN);

        assertNotNull(role);
        assertEquals(RoleName.ADMIN, role.getName());
    }

    @Test
    void shouldSetAndGetIdAndName() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.USER);

        assertEquals(1L, role.getId());
        assertEquals(RoleName.USER, role.getName());
    }

    @Test
    void shouldVerifyEqualsAndHashCode() {
        Role role1 = new Role(RoleName.ADMIN);
        Role role2 = new Role(RoleName.ADMIN);
        Role role3 = new Role(RoleName.USER);

        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());

        assertNotEquals(role1, role3);
        assertNotEquals(role1.hashCode(), role3.hashCode());
    }

    @Test
    void shouldHaveToStringRepresentation() {
        Role role = new Role(RoleName.USER);
        String toString = role.toString();

        assertTrue(toString.contains("Role{name=USER}"));
    }
}

