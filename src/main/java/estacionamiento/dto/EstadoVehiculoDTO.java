package estacionamiento.dto;

import estacionamiento.estados.EstadoVehiculo;

public class EstadoVehiculoDTO {

    private String matricula;
    private EstadoVehiculo estado;
    private Integer numeroLugar;

    public EstadoVehiculoDTO(
            String matricula,
            EstadoVehiculo estado,
            Integer numeroLugar
    ) {
        this.matricula = matricula;
        this.estado = estado;
        this.numeroLugar = numeroLugar;
    }

    public String getMatricula() {
        return matricula;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public Integer getNumeroLugar() {
        return numeroLugar;
    }
}