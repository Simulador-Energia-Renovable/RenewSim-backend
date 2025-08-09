package com.renewsim.backend.auth.infrastructure.persistence;

import com.renewsim.backend.auth.application.port.out.UserAccountGateway;
import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;
import com.renewsim.backend.role.RoleRepository;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaUserAccountGateway implements UserAccountGateway {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Optional<UserSnapshot> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::toSnapshot);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void createUser(String username, String passwordHash, Set<RoleName> roles) {
        Set<Role> roleEntities = roles.stream()
                .map(rn -> roleRepository.findByName(rn)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + rn)))
                .collect(Collectors.toSet());

        User user = new User(username, passwordHash, roleEntities);
        userRepository.save(user);
    }

    private UserSnapshot toSnapshot(User user) {
        Set<RoleName> roles = user.getRoles().stream()
                .map(r -> r.getName())
                .collect(Collectors.toSet());
        return new UserSnapshot(user.getUsername(), user.getPassword(), roles);
    }
}
