package com.renewsim.backend.user;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.simulation.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Entity Test")
class UserTest {

    private Long userId;
    private String username;
    private String password;
    private Set<Role> roles;
    private List<Simulation> simulations;

    @BeforeEach
    void setUp() {
        userId = 1L;
        username = "testuser";
        password = "securepassword";

        Role role = new Role();
        role.setName(RoleName.ADMIN);
        roles = Set.of(role);

        simulations = new ArrayList<>();
    }

    @Test
    @DisplayName("Should create User using constructor")
    void shouldCreateUserUsingConstructor() {
        User user = new User(username, password, roles);

        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRoles()).isEqualTo(roles);
    }

    @Test
    @DisplayName("Should set and get properties via setters and getters")
    void shouldSetAndGetProperties() {
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(roles);
        user.setSimulations(simulations);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRoles()).isEqualTo(roles);
        assertThat(user.getSimulations()).isEqualTo(simulations);
    }

    @Test
    @DisplayName("Should manage simulations correctly")
    void shouldManageSimulationsCorrectly() {
        User user = new User();
        user.setSimulations(new ArrayList<>());

        Simulation simulation = new Simulation();
        user.getSimulations().add(simulation);

        assertThat(user.getSimulations()).contains(simulation);
    }
}

