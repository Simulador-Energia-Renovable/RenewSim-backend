package com.renewsim.backend.simulation;

import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("SimulationRepository Integration Tests")
class SimulationRepositoryTest {

    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1, user2;

    @BeforeEach
    void setUp() {
        user1 = createAndSaveUser("user1@example.com");
        user2 = createAndSaveUser("user2@example.com");
    }

    private User createAndSaveUser(String email) {
        User user = new User();
        user.setUsername(email);
        user.setPassword("pass");
        return userRepository.save(user);
    }

    private Simulation createSimulation(String location, String energyType, User user) {
        return Simulation.builder()
                .location(location)
                .energyType(energyType)
                .projectSize(100)
                .budget(5000)
                .energyGenerated(12000)
                .estimatedSavings(4000)
                .returnOnInvestment(1.25)
                .timestamp(LocalDateTime.now())
                .user(user)
                .build();
    }

    @Test
    @DisplayName("Should return simulations for a given user")
    void testShouldReturnSimulationsForGivenUser() {
        simulationRepository.saveAll(List.of(
                createSimulation("Madrid", "solar", user1),
                createSimulation("Barcelona", "wind", user1)
        ));

        List<Simulation> result = simulationRepository.findAllByUser(user1);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Simulation::getLocation).contains("Madrid", "Barcelona");
    }

    @Test
    @DisplayName("Should return empty list when user has no simulations")
    void shouldReturnEmptyListForUserWithoutSimulations() {
        List<Simulation> result = simulationRepository.findAllByUser(user2);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should delete simulations belonging to a user")
    void testShouldDeleteSimulationsBelongingToUser() {
        simulationRepository.save(createSimulation("Seville", "hydro", user1));

        assertThat(simulationRepository.findAllByUser(user1)).hasSize(1);

        simulationRepository.deleteByUser(user1);

        assertThat(simulationRepository.findAllByUser(user1)).isEmpty();
    }

    @Test
    @DisplayName("Should not delete simulations of other users")
    void testShouldNotDeleteSimulationsFromOtherUsers() {
        simulationRepository.saveAll(List.of(
                createSimulation("Bilbao", "solar", user1),
                createSimulation("Valencia", "wind", user2)
        ));

        simulationRepository.deleteByUser(user1);

        assertThat(simulationRepository.findAllByUser(user1)).isEmpty();
        assertThat(simulationRepository.findAllByUser(user2)).hasSize(1);
    }
}


