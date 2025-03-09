package com.renewsim.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.renewsim.backend.models.TestEntity;
import com.renewsim.backend.repositories.TestRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TestService {

    private final TestRepository testRepository;

    @Autowired // ✅ Inyección explícita (opcional)
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
