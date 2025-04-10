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

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Obtener todos los usuarios
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAll();
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    // Obtener roles por ID de usuario
    @GetMapping("/{id}/roles")
    public List<RoleDTO> getUserRoles(@PathVariable Long id) {
        return userService.getRolesByUserId(id);
    }

    // Obtener roles del usuario autenticado
    @GetMapping("/me/roles")
    public List<RoleDTO> getCurrentUserRoles(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userDetails.getUser().getRoles().stream()
                .map(role -> new RoleDTO(role.getId(), role.getName().name()))
                .collect(Collectors.toList());
    }

    // Actualizar roles de un usuario
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles")
    public ResponseEntity<String> updateUserRoles(
            @PathVariable Long id,
            @RequestBody UpdateRolesRequestDTO request) {
        userService.updateUserRoles(id, request.getRoles());
        return ResponseEntity.ok("Roles actualizados correctamente");
    }

    // Eliminar usuario (no se permite si es ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }

    // Obtener usuarios sin roles@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/without-roles")
    public List<UserResponseDTO> getUsersWithoutRoles() {
        return userService.getUsersWithoutRoles();
    }

    // Obtener datos del usuario autenticado
    @GetMapping("/me")
    public UserResponseDTO getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getCurrentUser(userDetails.getUser());
    }

}
