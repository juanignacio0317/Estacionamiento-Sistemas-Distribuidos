
package estacionamiento.dto;

public class VehiculoTopEstadiasDTO {

    private String matricula;
    private long cantidadEstadias;

    public VehiculoTopEstadiasDTO(
            String matricula,
            long cantidadEstadias
    ) {
        this.matricula = matricula;
        this.cantidadEstadias = cantidadEstadias;
    }

    public String getMatricula() {
        return matricula;
    }

    public long getCantidadEstadias() {
        return cantidadEstadias;
    }
}

