package com.renewsim.backend.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renewsim.backend.dto.AuthRequestDTO;
import com.renewsim.backend.dto.AuthResponseDTO;
import com.renewsim.backend.dto.UserResponseDTO;
import com.renewsim.backend.mapper.UserMapper;
import com.renewsim.backend.model.User;
import com.renewsim.backend.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private UserMapper userMapper;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody AuthRequestDTO request) {
        User user = authService.registerUser(request.getUsername(), request.getPassword(), "USER");
        return userMapper.toResponseDto(user);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthRequestDTO request) {
        String token = authService.authenticate(request.getUsername(), request.getPassword());
        return new AuthResponseDTO(token);
    }

}
