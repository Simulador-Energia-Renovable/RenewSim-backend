package com.renewsim.backend.profile.usecase;

import com.renewsim.backend.profile.ProfileService;
import com.renewsim.backend.profile.dto.CreateProfileRequestDTO;
import com.renewsim.backend.profile.dto.ProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateProfileUseCase {

    private final ProfileService profileService;

    public ProfileDTO execute(Long userId, CreateProfileRequestDTO request) {
        return profileService.createProfile(userId, request);
    }
}
