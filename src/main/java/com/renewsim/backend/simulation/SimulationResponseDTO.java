package com.renewsim.backend.simulation;

public class SimulationResponseDTO {
    private double energiaGenerada;
    private double ahorroEstimado;
    private double retornoInversion;

    public SimulationResponseDTO(double energiaGenerada, double ahorroEstimado, double retornoInversion) {
        this.energiaGenerada = energiaGenerada;
        this.ahorroEstimado = ahorroEstimado;
        this.retornoInversion = retornoInversion;
    }

}

