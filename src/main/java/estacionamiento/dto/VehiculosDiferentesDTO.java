
package estacionamiento.dto;

public class VehiculosDiferentesDTO {

    private String periodo;
    private long cantidad;

    public VehiculosDiferentesDTO(String periodo, long cantidad) {
        this.periodo = periodo;
        this.cantidad = cantidad;
    }

    public String getPeriodo() {
        return periodo;
    }

    public long getCantidad() {
        return cantidad;
    }
}

