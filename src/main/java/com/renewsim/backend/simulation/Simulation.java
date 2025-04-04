package com.renewsim.backend.simulation;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.renewsim.backend.technologyComparison.TechnologyComparison;
import com.renewsim.backend.user.User;

import jakarta.persistence.*;

@Entity
@Table(name = "simulations")
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

    @Column(name = "estimated_Savings", nullable = false)
    private double estimatedSavings;

    @Column(name = "return_investment", nullable = false)
    private double returnOnInvestment;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "energy_consumption")
    private double energyConsumption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToMany
    @JoinTable(name = "simulation_technology", joinColumns = @JoinColumn(name = "simulation_id"), inverseJoinColumns = @JoinColumn(name = "technology_comparison_id"))
    private List<TechnologyComparison> technologies;

    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getEnergyType() {
        return energyType;
    }

    public double getProjectSize() {
        return projectSize;
    }

    public double getBudget() {
        return budget;
    }

    public double getEnergyGenerated() {
        return energyGenerated;
    }

    public double getEstimatedSavings() {
        return estimatedSavings;
    }

    public double getReturnOnInvestment() {
        return returnOnInvestment;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEnergyType(String energyType) {
        this.energyType = energyType;
    }

    public void setProjectSize(double projectSize) {
        this.projectSize = projectSize;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public void setEnergyGenerated(double energyGenerated) {
        this.energyGenerated = energyGenerated;
    }

    public void setEstimatedSavings(double estimatedSavings) {
        this.estimatedSavings = estimatedSavings;
    }

    public void setReturnOnInvestment(double returnOnInvestment) {
        this.returnOnInvestment = returnOnInvestment;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public void setTechnologies(List<TechnologyComparison> technologies) {
        this.technologies = technologies;
    }
}
