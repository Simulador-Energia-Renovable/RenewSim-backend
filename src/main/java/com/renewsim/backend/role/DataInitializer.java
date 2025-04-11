package com.renewsim.backend.role;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        List<RoleName> roles = List.of(RoleName.USER, RoleName.ADMIN);

        roles.forEach(roleName -> {
            roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(roleName)));
        });

        System.out.println("✅ Roles inicializados correctamente.");
    }
}
