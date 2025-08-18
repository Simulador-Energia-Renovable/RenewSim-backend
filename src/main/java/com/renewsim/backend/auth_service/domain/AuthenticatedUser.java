package com.renewsim.backend.auth_service.domain;

import java.util.Set;

public record AuthenticatedUser(
        String username,       
        Set<String> roles,
        Set<String> scopes) {
}
