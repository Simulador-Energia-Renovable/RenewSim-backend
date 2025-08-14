package com.renewsim.backend.auth.application.service;

import com.renewsim.backend.auth.application.port.in.AuthUseCase;
import com.renewsim.backend.auth.application.port.out.RoleProvider;
import com.renewsim.backend.auth.application.port.out.ScopePolicy;
import com.renewsim.backend.auth.application.port.out.TokenProvider;
import com.renewsim.backend.auth.application.port.out.UserAccountGateway;
import com.renewsim.backend.auth.application.port.out.UserAccountGateway.UserSnapshot;
import com.renewsim.backend.auth.domain.AuthenticatedUser;
import com.renewsim.backend.auth.web.dto.AuthRequestDTO;
import com.renewsim.backend.auth.web.dto.AuthResponseDTO;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.shared.exception.AuthenticationException;
import com.renewsim.backend.shared.exception.ResourceConflictException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthUseCase {

    private final UserAccountGateway userGateway;
    private final RoleProvider roleProvider;
    private final ScopePolicy scopePolicy;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {

        final String invalidMsg = "Invalid username or password";

        UserSnapshot user = userGateway.findByUsername(request.getUsername())
                .orElseThrow(() -> {

                    log.warn("Login failed: username not found [{}]", request.getUsername());
                    return new IllegalArgumentException(invalidMsg);
                });

        if (!passwordEncoder.matches(request.getPassword(), user.passwordHash())) {
            log.warn("Login failed: bad credentials for username [{}]", request.getUsername());
            throw new AuthenticationException(invalidMsg);
        }

        Set<String> roleNames = user.roles().stream().map(Enum::name).collect(Collectors.toSet());
        Set<String> scopes = user.roles().stream()
                .flatMap(r -> scopePolicy.scopesFor(r).stream())
                .collect(Collectors.toSet());

        String token = tokenProvider.generate(new AuthenticatedUser(user.username(), roleNames, scopes));
        log.info("Login success for username [{}]", request.getUsername());
        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresAt(Instant.now().plusSeconds(tokenProvider.expiresInSeconds()))
                .username(user.username())
                .roles(roleNames)
                .scopes(scopes)
                .build();
    }

    @Override
    public AuthResponseDTO register(AuthRequestDTO request) {
        String username = request.getUsername();

        if (userGateway.existsByUsername(username)) {
            log.warn("Register failed: username already exists [{}]", username);
            throw new ResourceConflictException("Username already exists");
        }

        RoleName defaultRole = roleProvider.defaultRole();
        Set<RoleName> roles = Set.of(defaultRole);

        String hash = passwordEncoder.encode(request.getPassword());
        userGateway.createUser(username, hash, roles);

        Set<String> roleNames = roles.stream().map(Enum::name).collect(Collectors.toSet());
        Set<String> scopes = scopePolicy.scopesFor(defaultRole);
        String token = tokenProvider.generate(new AuthenticatedUser(username, roleNames, scopes));
        log.info("Register success for username [{}] with default role [{}]", username, defaultRole.name());
        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresAt(Instant.now().plusSeconds(tokenProvider.expiresInSeconds()))
                .username(username)
                .roles(roleNames)
                .scopes(scopes)
                .build();
    }
}
