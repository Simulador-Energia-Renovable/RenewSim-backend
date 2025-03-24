package com.renewsim.backend.dto;

import java.util.Set;

public record UserRequestDTO(String username, String password, Set<String> roles) {
}
