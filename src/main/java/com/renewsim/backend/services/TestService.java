package com.renewsim.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.renewsim.backend.models.TestEntity;
import com.renewsim.backend.repositories.TestRepository;



@Service
public class TestService {

    private final TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public List<TestEntity> getAllTests() {
        return testRepository.findAll();
    }

    public TestEntity createTest(String message) {
        return testRepository.save(new TestEntity(message));
    }
}

