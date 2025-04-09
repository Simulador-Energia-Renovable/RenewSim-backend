package com.renewsim.backend.profile;

import com.renewsim.backend.profile.dto.CreateProfileRequestDTO;
import com.renewsim.backend.profile.dto.ProfileDTO;
import com.renewsim.backend.profile.dto.UpdateProfileRequestDTO;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Override
    public ProfileDTO getProfileByUserId(Long userId) {
        Profile profile = profileRepository.findByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("Profile not found for user id: " + userId);
        }
        return ProfileMapper.toDTO(profile);
    }

    @Override
    @Transactional
    public ProfileDTO createProfile(Long userId, CreateProfileRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Profile existingProfile = profileRepository.findByUserId(user.getId());
        if (existingProfile != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Profile already exists for user");
        }

        Profile profile = ProfileMapper.toEntity(request);
        profile.setUser(user);
        Profile savedProfile = profileRepository.save(profile);

        return ProfileMapper.toDTO(savedProfile);
    }

    @Override
    public ProfileDTO updateProfile(Long userId, UpdateProfileRequestDTO request) {
        Profile profile = profileRepository.findByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("Profile not found for user id: " + userId);
        }

        ProfileMapper.updateEntity(profile, request);
        Profile updatedProfile = profileRepository.save(profile);

        return ProfileMapper.toDTO(updatedProfile);
    }
}


