package com.renewsim.backend.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.role.RoleService;
import com.renewsim.backend.security.JwtUtils;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.roleService = roleService;
       
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public AuthResponseDTO registerUserAndReturnAuth(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya existe");
        }

        Role defaultRole = roleService.getRoleByName(RoleName.USER);
        Set<Role> roles = Set.of(defaultRole);
        User user = new User(username, passwordEncoder.encode(password), roles);
        userRepository.save(user);       

        Set<String> roleNames = Set.of(defaultRole.getName().name());
        Set<String> scopes = getScopesFromRole(defaultRole.getName());

        String token = jwtUtils.generateToken(username, roleNames, scopes);
        return new AuthResponseDTO(token, username, roleNames);
    }

    public String authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();
            Set<String> roleNames = user.getRoles().stream()
                    .map(r -> r.getName().name())
                    .collect(Collectors.toSet());

            Set<String> scopes = user.getRoles().stream()
                    .flatMap(r -> getScopesFromRole(r.getName()).stream())
                    .collect(Collectors.toSet());

            return jwtUtils.generateToken(username, roleNames, scopes);
        }
        throw new RuntimeException("Credenciales inválidas");
    }

    private Set<String> getScopesFromRole(RoleName roleName) {
        return switch (roleName) {
            case USER -> Set.of("read:simulations", "write:simulations", "compare:simulations");           
            case ADMIN -> Set.of("read:simulations", "write:simulations", "compare:simulations",
                    "export:simulations", "delete:simulations", "read:users", "manage:users");
        };
    }

}
