package com.renewsim.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;


import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;

@SpringBootApplication
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
}

