package com.renewsim.backend.user.dto;

import java.util.Set;



public class UserResponseDTO {
    private Long id;
    private String username;
    private Set<String> roles;

    // Constructor vacío (necesario para deserialización)
    public UserResponseDTO() {}


    

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


