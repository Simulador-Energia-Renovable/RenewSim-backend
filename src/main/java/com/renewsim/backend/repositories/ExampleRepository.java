package com.renewsim.backend.repositories;

import com.renewsim.backend.models.ExampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ExampleRepository extends JpaRepository<ExampleEntity, Long> {
}


