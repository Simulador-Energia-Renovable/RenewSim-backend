package com.renewsim.backend.models;

import jakarta.persistence.*;

@Entity
public class ExampleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}

