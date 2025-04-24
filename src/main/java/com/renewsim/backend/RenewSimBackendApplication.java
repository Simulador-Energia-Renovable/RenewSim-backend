package com.renewsim.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableCaching // Habilita la caché en toda la aplicación
public class RenewSimBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(RenewSimBackendApplication.class, args);
    }

    @Bean
    @Profile("!test") // Importante: se ejecuta solo fuera del perfil de test
    CommandLineRunner checkDatabase(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println("✅ Conectado a la base de datos: " + metaData.getURL());
            }
        };
    }

}