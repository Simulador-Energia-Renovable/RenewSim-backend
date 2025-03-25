package com.renewsim.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.renewsim.backend.model.TestEntity;
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

    public TestEntity createTest(String name) {
        return testRepository.save(new TestEntity(null, name));
    }
}
