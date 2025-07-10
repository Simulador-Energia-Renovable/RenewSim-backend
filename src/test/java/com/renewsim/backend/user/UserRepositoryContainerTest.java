package com.renewsim.backend.user;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.role.RoleRepository;
import com.renewsim.backend.util.TestDataFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = { "spring.profiles.active=testcontainer" })
@Testcontainers
@DisplayName("Integration Test - UserRepository with MySQLContainer")
class UserRepositoryContainerTest {

    private static final String TEST_USER_1 = "testuser1";
    private static final String EXISTING_USER = "existinguser";
    private static final String NON_EXISTENT_USER = "nonexistentuser";


    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role savedRole;

    @BeforeEach
    void setUp() {
        savedRole = roleRepository.findByName(RoleName.USER)
                .orElseGet(() -> roleRepository.save(TestDataFactory.createRole(RoleName.USER)));
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and find user by username")
    void testShouldSaveAndFindByUsername() {
        User user = createUser(TEST_USER_1);

        Optional<User> found = userRepository.findByUsername(TEST_USER_1);

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(TEST_USER_1);
    }

    @Test
    @DisplayName("Should return true if username exists")
    void testShouldReturnTrueIfUsernameExists() {
        createUser(EXISTING_USER);

        boolean exists = userRepository.existsByUsername(EXISTING_USER);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false if username does not exist")
    void testShouldReturnFalseIfUsernameDoesNotExist() {
        boolean exists = userRepository.existsByUsername(NON_EXISTENT_USER);

        assertThat(exists).isFalse();
    }

    private User createUser(String username) {
        return userRepository.save(TestDataFactory.createUser(username, savedRole));
    }
}

