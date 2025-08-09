package com.renewsim.backend.auth.web.controller;

import com.renewsim.backend.auth.application.port.in.AuthUseCase;
import com.renewsim.backend.auth.web.dto.AuthRequestDTO;
import com.renewsim.backend.auth.web.dto.AuthResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(authUseCase.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .cacheControl(CacheControl.noStore()) 
                .body(authUseCase.register(request));
    }
}

