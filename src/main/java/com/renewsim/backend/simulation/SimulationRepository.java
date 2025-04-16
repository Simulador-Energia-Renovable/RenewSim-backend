package com.renewsim.backend.simulation;

import com.renewsim.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Long> {

    List<Simulation> findAllByUser(User user);

    void deleteByUser(User user);
}
