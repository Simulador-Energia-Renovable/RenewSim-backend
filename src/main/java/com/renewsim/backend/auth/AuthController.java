package com.renewsim.backend.auth;


import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserMapper;

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
    public ResponseEntity<AuthResponseDTO> register(@RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.registerUserAndReturnAuth(
            request.getUsername(),
            request.getPassword()
        );
        return ResponseEntity.ok(response);
    }
    

   @PostMapping("/login")
public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
    String token = authService.authenticate(request.getUsername(), request.getPassword());

    User user = authService.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // Convertimos Set<Role> a Set<String>
    Set<String> roles = user.getRoles()
            .stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toSet());

    return ResponseEntity.ok(new AuthResponseDTO(token, user.getUsername(), roles));
}


}
