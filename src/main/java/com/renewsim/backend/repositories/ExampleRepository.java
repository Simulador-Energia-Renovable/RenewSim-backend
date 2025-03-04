package com.renewsim.backend.repositories;

import com.renewsim.backend.models.ExampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleRepository extends JpaRepository<ExampleEntity, Long> {
}

