
package estacionamiento.dto;

public class UsoEntradaDTO {

    private String entrada;
    private long cantidad;

    public UsoEntradaDTO(String entrada, long cantidad) {
        this.entrada = entrada;
        this.cantidad = cantidad;
    }

    public String getEntrada() {
        return entrada;
    }

    public long getCantidad() {
        return cantidad;
    }
}

