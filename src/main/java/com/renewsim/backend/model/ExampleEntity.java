package com.renewsim.backend.model;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "example")
@Getter
@Setter
@NoArgsConstructor
public class ExampleEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;

    public ExampleEntity(String name) {
        this.name = name;
    }
}


