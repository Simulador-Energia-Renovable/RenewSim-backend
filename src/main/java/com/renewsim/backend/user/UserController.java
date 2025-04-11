package com.renewsim.backend.user;

import java.util.List;
import java.util.stream.Collectors;

import com.renewsim.backend.role.RoleDTO;
import com.renewsim.backend.role.UpdateRolesRequestDTO;
import com.renewsim.backend.security.UserDetailsImpl;
import com.renewsim.backend.user.dto.UserResponseDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserUseCase userUseCase;
    private final UserService userService;

    public UserController(UserUseCase userUseCase, UserService userService) {
        this.userUseCase = userUseCase;
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping("/{id}/roles")
    public List<RoleDTO> getUserRoles(@PathVariable Long id) {
        return userService.getRolesByUserId(id);
    }

    @GetMapping("/me/roles")
    public List<RoleDTO> getCurrentUserRoles(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userDetails.getUser().getRoles().stream()
                .map(role -> new RoleDTO(role.getId(), role.getName().name()))
                .collect(Collectors.toList());
    }

    // ⬇️ Actualizamos para que use el UseCase
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles")
    public ResponseEntity<String> updateUserRoles(
            @PathVariable Long id,
            @RequestBody UpdateRolesRequestDTO request) {
        userUseCase.updateUserRoles(id, request.getRoles());
        return ResponseEntity.ok("Roles actualizados correctamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userUseCase.deleteUser(id);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/without-roles")
    public List<UserResponseDTO> getUsersWithoutRoles() {
        return userUseCase.getUsersWithoutRoles();
    }

    @GetMapping("/me")
    public UserResponseDTO getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getCurrentUser(userDetails.getUser());
    }
}
