package com.renewsim.backend.profile.dto;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UpdateProfileRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;  
    private String profileImageUrl;

}
