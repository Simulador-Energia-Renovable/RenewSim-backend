package com.renewsim.backend.role.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleDTOTest {

    @Test
    void shouldCreateRoleDTOWithBuilder() {
        RoleDTO roleDTO = RoleDTO.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        assertNotNull(roleDTO);
        assertEquals(1L, roleDTO.getId());
        assertEquals("ADMIN", roleDTO.getName());
    }

    @Test
    void shouldSetAndGetFields() {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(2L);
        roleDTO.setName("USER");

        assertEquals(2L, roleDTO.getId());
        assertEquals("USER", roleDTO.getName());
    }

    @Test
    void shouldHaveToStringRepresentation() {
        RoleDTO roleDTO = RoleDTO.builder()
                .id(3L)
                .name("MODERATOR")
                .build();

        String toString = roleDTO.toString();
        assertTrue(toString.contains("id=3"));
        assertTrue(toString.contains("name=MODERATOR"));
    }
}
