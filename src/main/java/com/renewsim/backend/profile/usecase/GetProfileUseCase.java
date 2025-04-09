package com.renewsim.backend.profile.usecase;

import com.renewsim.backend.profile.ProfileService;
import com.renewsim.backend.profile.dto.ProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetProfileUseCase {

    private final ProfileService profileService;

    public ProfileDTO execute(Long userId) {
        return profileService.getProfileByUserId(userId);
    }
}
