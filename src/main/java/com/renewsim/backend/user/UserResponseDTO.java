package com.renewsim.backend.user;

import java.util.Set;

import java.util.stream.Collectors;

public class UserResponseDTO {
    private Long id;
    private String username;
    private Set<String> roles;

    // Constructor vacío (necesario para deserialización)
    public UserResponseDTO() {}

    // Constructor que convierte User a UserResponseDTO
    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.roles = user.getRoles().stream()
                         .map(roleName -> roleName.toString())
                         .collect(Collectors.toSet());
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}


