package com.renewsim.backend.user;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.dto.RoleDTO;
import com.renewsim.backend.security.UserDetailsImpl;
import com.renewsim.backend.user.dto.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserUseCase userUseCase;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userUseCase = mock(UserUseCase.class);
        userController = new UserController(userUseCase, userService);
    }

    @Test
    @DisplayName("Should get all users")
    void shouldGetAllUsers() {
        List<UserResponseDTO> users = List.of(new UserResponseDTO());
        when(userService.getAll()).thenReturn(users);

        var response = userController.getAllUsers();

        assertThat(response.getBody()).isEqualTo(users);
        verify(userService).getAll();
    }

    @Test
    @DisplayName("Should get user by ID")
    void shouldGetUserById() {
        UserResponseDTO user = new UserResponseDTO();
        when(userService.getById(1L)).thenReturn(user);

        var response = userController.getUserById(1L);

        assertThat(response.getBody()).isEqualTo(user);
        verify(userService).getById(1L);
    }

    @Test
    @DisplayName("Should get user roles")
    void shouldGetUserRoles() {

        List<RoleDTO> roles = List.of(new RoleDTO(1L, "ADMIN"));
        when(userService.getRolesByUserId(1L)).thenReturn(roles);

        var response = userController.getUserRoles(1L);

        assertThat(response.getBody()).isEqualTo(roles);
        verify(userService).getRolesByUserId(1L);
    }

    @Test
    @DisplayName("Should get current user roles")
    void shouldGetCurrentUserRoles() {

        User user = new User();
        Role role = new Role();
        role.setId(1L);
        role.setName(com.renewsim.backend.role.RoleName.ADMIN);
        user.setRoles(Set.of(role));

        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUser()).thenReturn(user);

        var response = userController.getCurrentUserRoles(userDetails);

        assertThat(response.getBody())
                .hasSize(1)
                .extracting(RoleDTO::getName)
                .containsExactly("ADMIN");
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        var response = userController.deleteUser(1L);
        assertThat(response.getBody()).isEqualTo("Usuario eliminado correctamente");
        verify(userUseCase).deleteUser(1L);
    }

    @Test
    @DisplayName("Should get users without roles")
    void shouldGetUsersWithoutRoles() {
        List<UserResponseDTO> users = List.of(new UserResponseDTO());
        when(userUseCase.getUsersWithoutRoles()).thenReturn(users);

        var response = userController.getUsersWithoutRoles();

        assertThat(response.getBody()).isEqualTo(users);
        verify(userUseCase).getUsersWithoutRoles();
    }

    @Test
    @DisplayName("Should get current user")
    void shouldGetCurrentUser() {
        User user = new User();
        
        UserResponseDTO responseDTO = new UserResponseDTO();

        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUser()).thenReturn(user);
        when(userService.getCurrentUser(user)).thenReturn(responseDTO);

        var response = userController.getCurrentUser(userDetails);

        assertThat(response.getBody()).isEqualTo(responseDTO);
        verify(userService).getCurrentUser(user);
    }
}

