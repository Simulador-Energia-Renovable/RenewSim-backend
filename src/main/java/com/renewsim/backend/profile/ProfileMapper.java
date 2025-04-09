package com.renewsim.backend.profile;

import com.renewsim.backend.profile.dto.CreateProfileRequestDTO;
import com.renewsim.backend.profile.dto.ProfileDTO;
import com.renewsim.backend.profile.dto.UpdateProfileRequestDTO;

public class ProfileMapper {

    public static ProfileDTO toDTO(Profile profile) {
        if (profile == null) {
            return null;
        }

        return new ProfileDTO(
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getEmail(),
                profile.getPhone(),
                profile.getProfileImageUrl());
    }

    public static Profile toEntity(CreateProfileRequestDTO request) {
        Profile profile = new Profile();
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setEmail(request.getEmail());
        profile.setPhone(request.getPhone());
        profile.setProfileImageUrl(request.getProfileImageUrl());
        return profile;
    }

    public static void updateEntity(Profile profile, UpdateProfileRequestDTO request) {
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setEmail(request.getEmail());
        profile.setPhone(request.getPhone());
        profile.setProfileImageUrl(request.getProfileImageUrl());
    }
}
