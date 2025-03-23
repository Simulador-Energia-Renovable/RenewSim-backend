package com.renewsim.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.renewsim.backend.models.TestEntity;


@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
}

