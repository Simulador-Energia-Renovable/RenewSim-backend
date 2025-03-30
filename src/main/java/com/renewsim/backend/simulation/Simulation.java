package com.renewsim.backend.simulation;

import java.time.LocalDateTime;

import org.apache.catalina.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @Column(name = "energia_generada", nullable = false)
    private double energiaGenerada;

    @Column(name = "ahorro_estimado", nullable = false)
    private double ahorroEstimado;

    @Column(name = "retorno_inversion", nullable = false)
    private double retornoInversion;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 🛠 Getters and Setters

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

    public double getEnergiaGenerada() {
        return energiaGenerada;
    }

    public double getAhorroEstimado() {
        return ahorroEstimado;
    }

    public double getRetornoInversion() {
        return retornoInversion;
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

    public void setEnergiaGenerada(double energiaGenerada) {
        this.energiaGenerada = energiaGenerada;
    }

    public void setAhorroEstimado(double ahorroEstimado) {
        this.ahorroEstimado = ahorroEstimado;
    }

    public void setRetornoInversion(double retornoInversion) {
        this.retornoInversion = retornoInversion;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setUser(User user) {
        this.user = user;
    }
}


