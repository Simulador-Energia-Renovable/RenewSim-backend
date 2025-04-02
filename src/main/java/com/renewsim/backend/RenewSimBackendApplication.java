package com.renewsim.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.renewsim.backend.role.RoleRepository;
import com.renewsim.backend.user.User;
import com.renewsim.backend.user.UserRepository;
import com.renewsim.backend.role.Role;
import com.renewsim.backend.role.RoleName;




@SpringBootApplication
@EnableCaching  //Habilita la caché en toda la aplicación
public class RenewSimBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(RenewSimBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner checkDatabase(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println("✅ Conectado a la base de datos: " + metaData.getURL());
            }
        };
    }

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin@renewsim.com");                
                admin.setPassword(encoder.encode("admin123"));

                Set<Role> roles = new HashSet<>();
                Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found"));
                roles.add(adminRole);
                admin.setRoles(roles);

                userRepository.save(admin);
                System.out.println("✅ Admin user created!");
            }
        };
}
}
