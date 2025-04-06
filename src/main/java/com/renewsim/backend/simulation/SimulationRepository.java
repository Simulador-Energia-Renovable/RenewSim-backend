package com.renewsim.backend.simulation;

import org.springframework.stereotype.Repository;
import com.renewsim.backend.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Long> {

    List<Simulation> findAllByUser(User user);

    void deleteByUser(User user);
}
