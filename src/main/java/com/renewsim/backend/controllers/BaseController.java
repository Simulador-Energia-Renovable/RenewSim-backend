package com.renewsim.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class BaseController {

    @GetMapping("/health")
    public String healthCheck() {
        return "API v1 is running";
    }
}
