
package estacionamiento.dto;

public class TiempoPromedioDTO {

    private String periodo;
    private long cantidadEstadias;
    private double promedioMinutos;

    public TiempoPromedioDTO(
            String periodo,
            long cantidadEstadias,
            double promedioMinutos
    ) {
        this.periodo = periodo;
        this.cantidadEstadias = cantidadEstadias;
        this.promedioMinutos = promedioMinutos;
    }

    public String getPeriodo() {
        return periodo;
    }

    public long getCantidadEstadias() {
        return cantidadEstadias;
    }

    public double getPromedioMinutos() {
        return promedioMinutos;
    }
}

