package com.renewsim.backend.user;

import com.renewsim.backend.role.dto.RoleDTO;
import com.renewsim.backend.role.dto.UpdateRolesRequestDTO;
import com.renewsim.backend.security.UserDetailsImpl;
import com.renewsim.backend.user.dto.ChangePasswordDTO;
import com.renewsim.backend.user.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<List<RoleDTO>> getUserRoles(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getRolesByUserId(id));
    }

    @GetMapping("/me/roles")
    public ResponseEntity<List<RoleDTO>> getCurrentUserRoles(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<RoleDTO> roles = userDetails.getUser().getRoles().stream()
                .map(role -> new RoleDTO(role.getId(), role.getName().name()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles")
    public ResponseEntity<String> updateUserRoles(
            @PathVariable Long id,
            @Validated @RequestBody UpdateRolesRequestDTO request) {
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
    @GetMapping("/filter/without-roles")
    public ResponseEntity<List<UserResponseDTO>> getUsersWithoutRoles() {
        return ResponseEntity.ok(userUseCase.getUsersWithoutRoles());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userService.getCurrentUser(userDetails.getUser()));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ChangePasswordDTO request) {
        try {           

            String username = jwt.getSubject();

            User user = userService.findByUsername(username);
            userUseCase.changePassword(user, request.getCurrentPassword(), request.getNewPassword());

            return ResponseEntity.ok("Contraseña cambiada correctamente");
        } catch (IllegalArgumentException e) {
                        
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al cambiar la contraseña");
        }
    }

}
