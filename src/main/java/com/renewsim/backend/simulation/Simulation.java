package com.renewsim.backend.simulation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.renewsim.backend.technologyComparison.TechnologyComparison;
import com.renewsim.backend.user.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "simulations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Simulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String location;

    @Column(name = "energy_type", nullable = false)
    private String energyType;

    @Column(name = "project_size", nullable = false)
    private double projectSize;

    @Column(nullable = false)
    private double budget;

    @Column(name = "energy_generated", nullable = false)
    private double energyGenerated;

    @Column(name = "estimated_savings", nullable = false)
    private double estimatedSavings;

    @Column(name = "return_investment", nullable = false)
    private double returnOnInvestment;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "energy_consumption")
    private double energyConsumption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "simulation_technologies",
        joinColumns = @JoinColumn(name = "simulation_id"),
        inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    @Builder.Default
    private List<TechnologyComparison> technologies = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
