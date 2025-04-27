package com.renewsim.backend.role.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

import jakarta.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRolesRequestDTO {

    @NotEmpty(message = "The roles list cannot be empty")
    private List<String> roles;
}

