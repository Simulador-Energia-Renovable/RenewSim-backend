package com.renewsim.backend.role.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UpdateRolesRequestDTOTest {

    @Test
    void shouldCreateUpdateRolesRequestDTOWithBuilder() {
        UpdateRolesRequestDTO dto = UpdateRolesRequestDTO.builder()
                .roles(List.of("ADMIN", "USER"))
                .build();

        assertNotNull(dto);
        assertEquals(2, dto.getRoles().size());
        assertTrue(dto.getRoles().contains("ADMIN"));
        assertTrue(dto.getRoles().contains("USER"));
    }

    @Test
    void shouldSetAndGetRoles() {
        UpdateRolesRequestDTO dto = new UpdateRolesRequestDTO();
        dto.setRoles(List.of("USER"));

        assertEquals(1, dto.getRoles().size());
        assertEquals("USER", dto.getRoles().get(0));
    }

    @Test
    void shouldHaveToStringRepresentation() {
        UpdateRolesRequestDTO dto = UpdateRolesRequestDTO.builder()
                .roles(List.of("ADMIN"))
                .build();

        String toString = dto.toString();
        assertTrue(toString.contains("roles=[ADMIN]"));
    }
}

