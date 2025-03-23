package com.renewsim.backend.controllers.user;

import org.springframework.web.bind.annotation.PostMapping;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renewsim.backend.models.user.UserEntity;
import com.renewsim.backend.services.user.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserEntity register(@RequestParam String username, @RequestParam String password) {
        return userService.registerUser(username, password, "USER");
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
        String token = userService.authenticate(username, password);
        return Map.of("token", token);
    }

}
