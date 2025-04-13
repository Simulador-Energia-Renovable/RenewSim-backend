package com.renewsim.backend.role.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRolesRequestDTO {

    @NotEmpty(message = "The roles list cannot be empty")
    private List<String> roles;
}

