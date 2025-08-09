package com.renewsim.backend.auth.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDTO {
    private final String token;
    private final String tokenType;
    private final Instant expiresAt;
    private final String username;
    private final Set<String> roles;
    private final Set<String> scopes;
}
