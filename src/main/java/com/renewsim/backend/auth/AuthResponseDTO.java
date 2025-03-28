package com.renewsim.backend.auth;

import java.util.Set;

public class AuthResponseDTO {

    private final String token;
    private final String username;
    private final Set<String> roles;

    public AuthResponseDTO(String token, String username, Set<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
