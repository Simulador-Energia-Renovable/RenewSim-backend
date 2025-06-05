package com.renewsim.backend.user;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.role.RoleRepository;
import com.renewsim.backend.user.dto.UserResponseDTO;
import com.renewsim.backend.util.TestDataFactory;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(UserServiceImplIntegrationContainerTest.TestConfig.class)
@DisplayName("Integration Test - UserServiceImpl")
class UserServiceImplIntegrationContainerTest {

    private static final String TEST_USERNAME = "integrationUser";
    private static final String TEST_PASSWORD = "securepassword";
    private static final String USER_WITHOUT_ROLE = "noRoleUser";
    private static final String PASSWORD_NO_ROLE = "password";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseGet(() -> roleRepository.save(TestDataFactory.createRole(RoleName.USER)));

        testUser = userRepository.findByUsername(TEST_USERNAME)
                .orElseGet(() -> userRepository.save(TestDataFactory.createUser(TEST_USERNAME, TEST_PASSWORD, Set.of(role))));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("Should get all users")
    void testShouldGetAllUsers() {
        List<UserResponseDTO> users = userService.getAll();

        assertThat(users).isNotEmpty();
        assertThat(users.get(0).getUsername()).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("Should get user by ID")
    void testShouldGetUserById() {
        UserResponseDTO user = userService.getById(testUser.getId());

        assertThat(user.getUsername()).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("Should get user by entity")
    void testShouldGetByIdEntity() {
        User user = userService.getByIdEntity(testUser.getId());

        assertThat(user.getUsername()).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("Should delete user")
    void testShouldDeleteUser() {
        userService.deleteUser(testUser.getId());

        Optional<User> deletedUser = userRepository.findById(testUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("Should get users without roles")
    void testShouldGetUsersWithoutRoles() {
        User userWithoutRoles = TestDataFactory.createUserWithoutRoles(USER_WITHOUT_ROLE, PASSWORD_NO_ROLE);
        userRepository.save(userWithoutRoles);

        List<UserResponseDTO> result = userService.getUsersWithoutRoles();

        assertThat(result).extracting(UserResponseDTO::getUsername)
                .contains(USER_WITHOUT_ROLE);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public UserService userService(UserRepository userRepository, UserMapper userMapper) {
            return new UserServiceImpl(userRepository, userMapper, new BCryptPasswordEncoder());
        }

        @Bean
        public UserMapper userMapper() {
            return new UserMapperImpl();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}

