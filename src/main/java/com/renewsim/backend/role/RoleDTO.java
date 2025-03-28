package com.renewsim.backend.role;

public class RoleDTO {
    private Long id;
    private String name;

    public RoleDTO() {
    }

    public RoleDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
