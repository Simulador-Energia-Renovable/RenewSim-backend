package com.renewsim.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class BaseController {

    @GetMapping("/status")
    public Map<String, String> getStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("service", "Renewable Energy Simulator API");
        status.put("status", "running");
        status.put("version", "1.0.0");
        return status;
    }
}

