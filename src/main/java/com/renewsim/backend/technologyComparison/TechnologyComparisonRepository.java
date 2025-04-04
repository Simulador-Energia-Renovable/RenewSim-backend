package com.renewsim.backend.technologyComparison;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnologyComparisonRepository extends JpaRepository<TechnologyComparison, Long> {
   
    boolean existsByTechnologyName(String technologyName);
}

