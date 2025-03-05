package com.renewsim.backend.controllers;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renewsim.backend.models.TestEntity;
import com.renewsim.backend.services.TestService;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
     private final TestService testService;

     public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping
    public String testEndpoint() {
        return "🚀 Backend is running!";
    }

    @PostMapping
    public TestEntity createTest(@RequestParam String message) {
        return testService.createTest(message);
    }
}
