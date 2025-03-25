package com.renewsim.backend.role;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Método para obtener un solo rol por nombre (enum)
    public Role getRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol " + roleName + " no existe"));
    }

    // Método para obtener múltiples roles a partir de strings
    public Set<Role> getRolesFromStrings(Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            try {
                RoleName enumName = RoleName.valueOf(roleName.toUpperCase());
                Role role = getRoleByName(enumName);
                roles.add(role);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol inválido: " + roleName);
            }
        }
        return roles;
    }
}

