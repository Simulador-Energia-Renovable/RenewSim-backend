package com.renewsim.backend.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleService;
import com.renewsim.backend.security.JwtUtils;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;

import java.util.Optional;
import java.util.Set;

//lógica de login/registro con JWT y contraseña encriptada

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // private final RoleRepository roleRepository;
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

    public User registerUser(String username, String password, String roleString) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya existe");
        }
    
        Set<Role> roles = roleService.getRolesFromStrings(Set.of(roleString));
    
        User user = new User(username, passwordEncoder.encode(password), roles);
        return userRepository.save(user);
    }

    public String authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return jwtUtils.generateToken(username);
        }
        throw new RuntimeException("Credenciales inválidas");
    }
}
