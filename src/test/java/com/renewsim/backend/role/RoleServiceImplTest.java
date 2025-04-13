package com.renewsim.backend.role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnRoleWhenRoleExists() {
        Role role = new Role(RoleName.ADMIN);
        when(roleRepository.findByName(RoleName.ADMIN)).thenReturn(Optional.of(role));

        Role result = roleService.getRoleByName(RoleName.ADMIN);

        assertNotNull(result);
        assertEquals(RoleName.ADMIN, result.getName());
        verify(roleRepository).findByName(RoleName.ADMIN);
    }

    @Test
    void shouldThrowExceptionWhenRoleDoesNotExist() {
        when(roleRepository.findByName(RoleName.ADMIN)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> roleService.getRoleByName(RoleName.ADMIN));

        assertTrue(exception.getMessage().contains("El rol ADMIN no existe"));
        verify(roleRepository).findByName(RoleName.ADMIN);
    }

    @Test
    void shouldReturnRolesFromStringsSuccessfully() {
        Role role = new Role(RoleName.USER);
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(role));

        Set<Role> roles = roleService.getRolesFromStrings(Set.of("USER"));

        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertTrue(roles.contains(role));
    }

    @Test
    void shouldThrowExceptionForInvalidRoleString() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> roleService.getRolesFromStrings(Set.of("INVALID_ROLE")));

        assertTrue(exception.getMessage().contains("Rol inválido"));
    }

    @Test
    void shouldReturnRolesByNamesSuccessfully() {
        Role role = new Role(RoleName.USER);
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(role));

        Set<Role> roles = roleService.getRolesByNames(List.of("USER"));

        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertTrue(roles.contains(role));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenRoleNotFoundByName() {
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> roleService.getRolesByNames(List.of("USER")));

        assertTrue(exception.getMessage().contains("Role not found: USER"));
    }
}

