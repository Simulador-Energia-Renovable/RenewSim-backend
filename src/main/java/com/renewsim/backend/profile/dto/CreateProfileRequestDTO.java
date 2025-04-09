package com.renewsim.backend.profile.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProfileRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String profileImageUrl;
}
