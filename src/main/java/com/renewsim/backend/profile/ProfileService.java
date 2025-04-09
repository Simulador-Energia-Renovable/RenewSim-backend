package com.renewsim.backend.profile;

import com.renewsim.backend.profile.dto.CreateProfileRequestDTO;
import com.renewsim.backend.profile.dto.ProfileDTO;
import com.renewsim.backend.profile.dto.UpdateProfileRequestDTO;

public interface ProfileService {
    ProfileDTO getProfileByUserId(Long userId);

    ProfileDTO createProfile(Long userId, CreateProfileRequestDTO request);
    
    ProfileDTO updateProfile(Long userId, UpdateProfileRequestDTO request);
}

