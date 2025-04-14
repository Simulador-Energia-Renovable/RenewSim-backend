package com.renewsim.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponseDTO {

    private final String token;
    private final String username;
    private final Set<String> roles;
}


