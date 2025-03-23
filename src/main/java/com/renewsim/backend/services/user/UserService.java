package com.renewsim.backend.services.user;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.renewsim.backend.models.user.UserEntity;
import com.renewsim.backend.repositories.user.UserRepository;
import com.renewsim.backend.security.JwtUtils;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity registerUser(String username, String password, String role) {
        UserEntity user = new UserEntity(null, username, passwordEncoder.encode(password), Set.of(role));
        return userRepository.save(user);
    }

    public String authenticate(String username, String password) {
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return jwtUtils.generateToken(username);
        }
        throw new RuntimeException("Invalid credentials");
    }
}

