package com.renewsim.backend.profile;

import com.renewsim.backend.profile.dto.CreateProfileRequestDTO;
import com.renewsim.backend.profile.dto.ProfileDTO;
import com.renewsim.backend.profile.dto.UpdateProfileRequestDTO;
import com.renewsim.backend.profile.usecase.CreateProfileUseCase;
import com.renewsim.backend.profile.usecase.GetProfileUseCase;
import com.renewsim.backend.profile.usecase.UpdateProfileUseCase;
import static com.renewsim.backend.security.AuthUtils.getCurrentUser;
import com.renewsim.backend.user.User;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final CreateProfileUseCase createProfileUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final ProfileService profileService;

   @GetMapping("/me")
public ResponseEntity<ProfileDTO> getProfile() {
    User user = getCurrentUser();
    ProfileDTO profileDTO = getProfileUseCase.execute(user.getId());
    return ResponseEntity.ok(profileDTO);
}

@PostMapping
public ResponseEntity<ProfileDTO> createProfile(@RequestBody CreateProfileRequestDTO request) {
    User user = getCurrentUser();
    ProfileDTO createdProfile = createProfileUseCase.execute(user.getId(), request);
    return ResponseEntity.ok(createdProfile);
}

@PutMapping
public ResponseEntity<ProfileDTO> updateProfile(@RequestBody UpdateProfileRequestDTO request) {
    User user = getCurrentUser();
    ProfileDTO updatedProfile = updateProfileUseCase.execute(user.getId(), request);
    return ResponseEntity.ok(updatedProfile);
}

@DeleteMapping
public ResponseEntity<Void> deleteProfile() {
    User user = getCurrentUser();
    profileService.deleteProfile(user.getId());
    return ResponseEntity.noContent().build();
}

}

