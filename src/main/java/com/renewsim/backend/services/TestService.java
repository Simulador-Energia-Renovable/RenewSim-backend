package com.renewsim.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.renewsim.backend.models.TestEntity;
import com.renewsim.backend.repositories.TestRepository;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TestService {
    private static final Logger logger = LoggerFactory.getLogger(TestService.class);
    private final TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public List<TestEntity> getAllTests() {
        logger.info("🔍 Fetching all test data from database...");
        List<TestEntity> tests = testRepository.findAll();
        logger.info("✅ {} test records retrieved", tests.size());
        return tests;
    }

    public TestEntity createTest(String message) {
        logger.info("📝 Creating new test entity with message: {}", message);
        TestEntity test = new TestEntity(message);
        TestEntity savedTest = testRepository.save(test);
        logger.info("✅ New test entity saved: {}", savedTest);
        return savedTest;
    }
}
