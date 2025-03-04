package com.renewsim.backend.controllers;

import com.renewsim.backend.services.ExampleService;
import org.springframework.web.bind.annotation.*;



import com.renewsim.backend.models.ExampleEntity;


import java.util.List;

@RestController
@RequestMapping("/api/example")
public class ExampleController {

    private final ExampleService exampleService;

    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping
    public List<ExampleEntity> getAllExamples() {
        return exampleService.getAllExamples();
    }

    @PostMapping
    public ExampleEntity createExample(@RequestBody ExampleEntity entity) {
        return exampleService.createExample(entity.getName());
    }
}


