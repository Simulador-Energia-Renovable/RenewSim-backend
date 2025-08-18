package com.renewsim.backend.auth_service.application.service;

import com.renewsim.backend.auth_service.application.port.in.AuthUseCase;
import com.renewsim.backend.auth_service.application.port.out.RoleProvider;
import com.renewsim.backend.auth_service.application.port.out.ScopePolicy;
import com.renewsim.backend.auth_service.application.port.out.TokenProvider;
import com.renewsim.backend.auth_service.application.port.out.UserAccountGateway;
import com.renewsim.backend.auth_service.application.port.out.UserAccountGateway.UserSnapshot;
import com.renewsim.backend.auth_service.domain.AuthenticatedUser;
import com.renewsim.backend.auth_service.web.dto.AuthRequestDTO;
import com.renewsim.backend.auth_service.web.dto.AuthResponseDTO;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.shared.exception.AuthenticationException;
import com.renewsim.backend.shared.exception.ResourceConflictException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthUseCase {

    private static final String INVALID_MSG = "Invalid username or password";

    private final UserAccountGateway userGateway;
    private final RoleProvider roleProvider;
    private final ScopePolicy scopePolicy;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final Clock clock;

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {
        UserSnapshot user = userGateway.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: bad credentials for username [{}]", request.getUsername());
                    return new AuthenticationException(INVALID_MSG);
                });

        if (!passwordEncoder.matches(request.getPassword(), user.passwordHash())) {
            log.warn("Login failed: bad credentials for username [{}]", request.getUsername());
            throw new AuthenticationException(INVALID_MSG);
        }

        Set<String> roleNames = user.roles().stream().map(Enum::name).collect(Collectors.toSet());
        Set<String> scopes = user.roles().stream()
                .flatMap(r -> scopePolicy.scopesFor(r).stream())
                .collect(Collectors.toSet());

        String token = tokenProvider.generate(new AuthenticatedUser(user.username(), roleNames, scopes));
        log.info("Login success for username [{}]", request.getUsername());

        return buildResponse(token, user.username(), roleNames, scopes);
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

        return buildResponse(token, username, roleNames, scopes);
    }

    private AuthResponseDTO buildResponse(String token, String username, Set<String> roles, Set<String> scopes) {
        Instant now = Instant.now(clock);
        long expiresIn = tokenProvider.expiresInSeconds();

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresAt(now.plusSeconds(expiresIn))
                .username(username)
                .roles(roles)
                .scopes(scopes)
                .build();
    }
}
