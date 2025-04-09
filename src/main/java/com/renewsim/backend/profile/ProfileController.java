package com.renewsim.backend.profile;


import com.renewsim.backend.profile.dto.CreateProfileRequestDTO;
import com.renewsim.backend.profile.dto.ProfileDTO;
import com.renewsim.backend.profile.dto.UpdateProfileRequestDTO;
import com.renewsim.backend.profile.usecase.CreateProfileUseCase;
import com.renewsim.backend.profile.usecase.GetProfileUseCase;
import com.renewsim.backend.profile.usecase.UpdateProfileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> getProfile(@PathVariable Long userId) {
        ProfileDTO profileDTO = getProfileUseCase.execute(userId);
        return ResponseEntity.ok(profileDTO);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ProfileDTO> createProfile(
            @PathVariable Long userId,
            @RequestBody CreateProfileRequestDTO request
    ) {
        ProfileDTO createdProfile = createProfileUseCase.execute(userId, request);
        return ResponseEntity.ok(createdProfile);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ProfileDTO> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequestDTO request
    ) {
        ProfileDTO updatedProfile = updateProfileUseCase.execute(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }
}

