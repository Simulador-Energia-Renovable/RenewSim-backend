package com.renewsim.backend.user;

import com.renewsim.backend.exception.UserNotFoundException;
import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.role.dto.RoleDTO;
import com.renewsim.backend.user.dto.UserRequestDTO;
import com.renewsim.backend.user.dto.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("UserServiceImpl Test")
class UserServiceImplTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserServiceImpl userService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, userMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Should get all users")
    void testShouldGetAllUsers() {
        User user = new User();
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponseDto(user)).thenReturn(new UserResponseDTO());

        List<UserResponseDTO> result = userService.getAll();

        assertThat(result).hasSize(1);
        verify(userRepository).findAll();
        verify(userMapper).toResponseDto(user);
    }

    @Test
    @DisplayName("Should get user by ID")
    void testShouldGetUserById() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(new UserResponseDTO());

        UserResponseDTO result = userService.getById(userId);

        assertThat(result).isNotNull();
        verify(userRepository).findById(userId);
        verify(userMapper).toResponseDto(user);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void testShouldThrowExceptionWhenUserNotFoundById() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(userId));

        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("Should save user")
    void testShouldSaveUser() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .username("testuser")
                .password("testpass")
                .roles(Set.of("ADMIN"))
                .build();

        User user = new User();
        User savedUser = new User();

        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(new UserResponseDTO());

        UserResponseDTO result = userService.save(dto);

        assertThat(result).isNotNull();
        verify(userMapper).toEntity(dto);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDto(savedUser);
    }

    @Test
    @DisplayName("Should delete user by ID")
    void testShouldDeleteUserById() {
        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Should get roles by user ID")
    void testShouldGetRolesByUserId() {
        Long userId = 1L;
        Role role = new Role();
        role.setName(RoleName.ADMIN);
        role.setId(1L);

        User user = new User();
        user.setRoles(Set.of(role));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<RoleDTO> result = userService.getRolesByUserId(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("ADMIN");
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should return user when roles are empty")
    void testShouldReturnUserWithEmptyRoles() {
        User user = new User();
        user.setRoles(Set.of());

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(new UserResponseDTO());

        List<UserResponseDTO> result = userService.getUsersWithoutRoles();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should return user when roles are null")
    void testShouldReturnUserWithNullRoles() {
        User user = new User();
        user.setRoles(null);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(new UserResponseDTO());

        List<UserResponseDTO> result = userService.getUsersWithoutRoles();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should get current user")
    void testShouldGetCurrentUser() {
        User user = new User();
        UserResponseDTO responseDTO = new UserResponseDTO();

        when(userMapper.toResponseDto(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getCurrentUser(user);

        assertThat(result).isEqualTo(responseDTO);
        verify(userMapper).toResponseDto(user);
    }

    @Test
    @DisplayName("Should change password when current is correct")
    void testShouldChangePasswordSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        userService.changePassword(user, "oldPassword", "newPassword");

        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    @DisplayName("Should throw when current password is incorrect")
    void testShouldThrowWhenCurrentPasswordIncorrect() {
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(user, "wrongPassword", "newPassword"));

        verify(userRepository, never()).save(any());
    }
}

