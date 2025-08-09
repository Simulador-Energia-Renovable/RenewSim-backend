package com.renewsim.backend.auth.domain;

import java.util.Set;

public record AuthenticatedUser(
        String username,       
        Set<String> roles,
        Set<String> scopes) {
}
