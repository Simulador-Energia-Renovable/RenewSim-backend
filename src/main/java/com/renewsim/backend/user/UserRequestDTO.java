package com.renewsim.backend.user;

import java.util.Set;

public record UserRequestDTO(String username, String password, Set<String> roles) {
}
