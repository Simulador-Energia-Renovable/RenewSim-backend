package com.renewsim.backend.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserMapper;
import com.renewsim.backend.user.UserResponseDTO;

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
    public ResponseEntity<UserResponseDTO> register(@RequestBody AuthRequestDTO request) {
        User user = authService.registerUser(request.getUsername(), request.getPassword(), request.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponseDTO(user));

    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthRequestDTO request) {
        String token = authService.authenticate(request.getUsername(), request.getPassword());
        return new AuthResponseDTO(token);
    }

}
