package com.renewsim.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renewsim.backend.models.TestEntity;
import com.renewsim.backend.services.TestService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping
    public List<TestEntity> getAllTests() {
        logger.info("📥 [GET] /api/v1/test - Request received");
        List<TestEntity> tests = testService.getAllTests();
        logger.info("📤 [GET] /api/v1/test - Response: {}", tests);
        return tests;
    }

    @PostMapping
    public TestEntity createTest(@RequestParam String message) {
        logger.info("📥 [POST] /api/v1/test - Request received with message: {}", message);
        TestEntity newTest = testService.createTest(message);
        logger.info("📤 [POST] /api/v1/test - New test created: {}", newTest);
        return newTest;
    }
}
