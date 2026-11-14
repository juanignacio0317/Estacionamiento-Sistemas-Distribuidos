
package estacionamiento.dto;

import java.time.LocalDateTime;

public class EstadiaDTO {

    private LocalDateTime fechaEntrada;
    private String puertaEntrada;
    private Integer numeroLugar;
    private LocalDateTime fechaSalida;
    private String puertaSalida;

    public EstadiaDTO(
            LocalDateTime fechaEntrada,
            String puertaEntrada,
            Integer numeroLugar,
            LocalDateTime fechaSalida,
            String puertaSalida) {

        this.fechaEntrada = fechaEntrada;
        this.puertaEntrada = puertaEntrada;
        this.numeroLugar = numeroLugar;
        this.fechaSalida = fechaSalida;
        this.puertaSalida = puertaSalida;
    }

    public LocalDateTime getFechaEntrada() {
        return fechaEntrada;
    }

    public String getPuertaEntrada() {
        return puertaEntrada;
    }

    public Integer getNumeroLugar() {
        return numeroLugar;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public String getPuertaSalida() {
        return puertaSalida;
    }
}
