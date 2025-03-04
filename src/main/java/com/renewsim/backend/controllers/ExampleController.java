package com.renewsim.backend.controllers;

import com.renewsim.backend.services.ExampleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/example")
public class ExampleController {
    private final ExampleService exampleService;

    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping
    public String testService() {
        return exampleService.exampleMethod();
    }
}

