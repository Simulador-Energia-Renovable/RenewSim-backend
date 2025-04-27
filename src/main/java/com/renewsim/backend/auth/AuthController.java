package com.renewsim.backend.auth;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.renewsim.backend.auth.dto.AuthRequestDTO;
import com.renewsim.backend.auth.dto.AuthResponseDTO;
import com.renewsim.backend.security.JwtUtils;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;
  

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.jwtUtils = new JwtUtils();
    
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRequestDTO request, HttpServletResponse response) {
        AuthResponseDTO authResponse = authService.registerUserAndReturnAuth(
                request.getUsername(),
                request.getPassword());

        jwtUtils.addJwtCookie(response, authResponse.getToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request, HttpServletResponse response) {
        String token = authService.authenticate(request.getUsername(), request.getPassword());

        User user = authService.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Set<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

                jwtUtils.addJwtCookie(response, token);
        return ResponseEntity.ok(new AuthResponseDTO(token, user.getUsername(), roles));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        jwtUtils.clearJwtCookie(response);
        return ResponseEntity.noContent().build();
    }  


}
