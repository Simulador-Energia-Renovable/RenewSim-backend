package com.renewsim.backend.user;

import com.renewsim.backend.exception.RoleNotFoundException;
import com.renewsim.backend.exception.UserNotFoundException;
import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.role.RoleService;
import com.renewsim.backend.user.dto.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("UserUseCase Test")
class UserUseCaseTest {

    private UserService userService;
    private RoleService roleService;
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        roleService = mock(RoleService.class);
        userUseCase = new UserUseCase(userService, roleService);
    }

    @Test
    @DisplayName("Should update user roles successfully")
    void testShouldUpdateUserRolesSuccessfully() {
        Long userId = 1L;
        List<String> roleNames = List.of("ADMIN");

        User user = new User();
        user.setId(userId);

        Role role = new Role();
        role.setName(RoleName.ADMIN);

        when(userService.getByIdEntity(userId)).thenReturn(user);
        when(roleService.getRolesByNames(roleNames)).thenReturn(Set.of(role));

        userUseCase.updateUserRoles(userId, roleNames);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).saveUser(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getRoles()).containsExactly(role);
    }

    @Test
    @DisplayName("Should throw exception when user not found in updateUserRoles")
    void testShouldThrowExceptionWhenUserNotFound() {
        Long userId = 99L;
        List<String> roleNames = List.of("ADMIN");

        when(userService.getByIdEntity(userId)).thenThrow(new UserNotFoundException(userId));

        assertThrows(UserNotFoundException.class, () -> userUseCase.updateUserRoles(userId, roleNames));

        verify(userService).getByIdEntity(userId);
        verify(roleService, never()).getRolesByNames(anyList());
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when role not found in updateUserRoles")
    void testShouldThrowExceptionWhenRoleNotFound() {
        Long userId = 1L;
        List<String> roleNames = List.of("NON_EXISTENT_ROLE");

        User user = new User();
        user.setId(userId);

        when(userService.getByIdEntity(userId)).thenReturn(user);
        when(roleService.getRolesByNames(roleNames))
                .thenThrow(new RoleNotFoundException("NON_EXISTENT_ROLE"));

        assertThrows(RoleNotFoundException.class, () -> userUseCase.updateUserRoles(userId, roleNames));

        verify(userService).getByIdEntity(userId);
        verify(roleService).getRolesByNames(roleNames);
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testShouldDeleteUserSuccessfully() {
        Long userId = 1L;

        userUseCase.deleteUser(userId);

        verify(userService).deleteUser(userId);
    }

    @Test
    @DisplayName("Should get users without roles")
    void testShouldGetUsersWithoutRoles() {
        List<UserResponseDTO> expectedUsers = List.of(
                UserResponseDTO.builder()
                        .id(1L)
                        .username("user1")
                        .roles(Set.of())
                        .build());

        when(userService.getUsersWithoutRoles()).thenReturn(expectedUsers);

        List<UserResponseDTO> result = userUseCase.getUsersWithoutRoles();

        assertThat(result).isEqualTo(expectedUsers);
        verify(userService).getUsersWithoutRoles();
    }
}
