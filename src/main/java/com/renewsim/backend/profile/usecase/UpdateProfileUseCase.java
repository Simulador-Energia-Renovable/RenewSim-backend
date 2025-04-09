package com.renewsim.backend.profile.usecase;


import com.renewsim.backend.profile.ProfileService;
import com.renewsim.backend.profile.dto.ProfileDTO;
import com.renewsim.backend.profile.dto.UpdateProfileRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateProfileUseCase {

    private final ProfileService profileService;

    public ProfileDTO execute(Long userId, UpdateProfileRequestDTO request) {
        return profileService.updateProfile(userId, request);
    }
}

