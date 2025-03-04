package com.renewsim.backend.services;

import org.springframework.stereotype.Service;


import com.renewsim.backend.models.ExampleEntity;
import com.renewsim.backend.repositories.ExampleRepository;


import java.util.List;

@Service
public class ExampleService {

    private final ExampleRepository exampleRepository;

    public ExampleService(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    public List<ExampleEntity> getAllExamples() {
        return exampleRepository.findAll();
    }

    public ExampleEntity createExample(String name) {
        ExampleEntity entity = new ExampleEntity(name);
        return exampleRepository.save(entity);
    }
}

