package com.renewsim.backend.user;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.role.RoleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Integration Test - UserRepository")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role savedRole;

    @BeforeEach
    void setUp() {
        // Preparamos el Role USER una sola vez por test
        Role role = new Role();
        role.setName(RoleName.USER);
        savedRole = roleRepository.save(role);
    }

    @AfterEach
    void cleanUp() {

        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and find user by username")
    void shouldSaveAndFindByUsername() {
   
        User user = createUser("testuser1");

        Optional<User> found = userRepository.findByUsername("testuser1");

           assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser1");
    }

    @Test
    @DisplayName("Should return true if username exists")
    void shouldReturnTrueIfUsernameExists() {

        createUser("existinguser");

        boolean exists = userRepository.existsByUsername("existinguser");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false if username does not exist")
    void shouldReturnFalseIfUsernameDoesNotExist() {

        boolean exists = userRepository.existsByUsername("nonexistentuser");

        assertThat(exists).isFalse();
    }

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("securepassword");
        user.setRoles(Set.of(savedRole));

        return userRepository.save(user);
    }
}


