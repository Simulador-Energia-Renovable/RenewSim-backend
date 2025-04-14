package com.renewsim.backend.user;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.role.RoleRepository;
import com.renewsim.backend.user.dto.UserResponseDTO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(UserServiceImplIntegrationTest.TestConfig.class)
@DisplayName("Integration Test - UserServiceImpl")
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(RoleName.USER);
                    return roleRepository.save(newRole);
                });

        testUser = userRepository.findByUsername("integrationUser")
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername("integrationUser");
                    newUser.setPassword("securepassword");
                    newUser.setRoles(Set.of(role));
                    return userRepository.save(newUser);
                });
    }

    @Test
    @DisplayName("Should get all users")
    void shouldGetAllUsers() {
        List<UserResponseDTO> users = userService.getAll();

        assertThat(users).isNotEmpty();
        assertThat(users.get(0).getUsername()).isEqualTo("integrationUser");
    }

    @Test
    @DisplayName("Should get user by ID")
    void shouldGetUserById() {
        UserResponseDTO user = userService.getById(testUser.getId());

        assertThat(user.getUsername()).isEqualTo("integrationUser");
    }

    @Test
    @DisplayName("Should get user by entity")
    void shouldGetByIdEntity() {
        User user = userService.getByIdEntity(testUser.getId());

        assertThat(user.getUsername()).isEqualTo("integrationUser");
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        userService.deleteUser(testUser.getId());

        Optional<User> deletedUser = userRepository.findById(testUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("Should get users without roles")
    void shouldGetUsersWithoutRoles() {
        User userWithoutRoles = new User();
        userWithoutRoles.setUsername("noRoleUser");
        userWithoutRoles.setPassword("password");
        userWithoutRoles.setRoles(Set.of());

        userRepository.save(userWithoutRoles);

        List<UserResponseDTO> result = userService.getUsersWithoutRoles();

        assertThat(result).extracting(UserResponseDTO::getUsername)
                .contains("noRoleUser");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public UserService userService(UserRepository userRepository, UserMapper userMapper) {
            return new UserServiceImpl(userRepository, userMapper);
        }

        @Bean
        public UserMapper userMapper() {
            return new UserMapperImpl();
        }
    }
}
