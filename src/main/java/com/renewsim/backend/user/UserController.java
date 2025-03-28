package com.renewsim.backend.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renewsim.backend.role.RoleDTO;
import com.renewsim.backend.role.UpdateRolesRequestDTO;
import com.renewsim.backend.security.UserDetailsImpl;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles")
    public ResponseEntity<String> updateUserRoles(
            @PathVariable Long id,
            @RequestBody UpdateRolesRequestDTO request) {
        userService.updateUserRoles(id, request.getRoles());
        return ResponseEntity.ok("Roles updated successfully");
    }

}
