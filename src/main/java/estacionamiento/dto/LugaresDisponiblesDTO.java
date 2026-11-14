package estacionamiento.dto;

import java.util.List;

public class LugaresDisponiblesDTO {

    private long cantidadLibres;
    private List<Integer> lugaresLibres;

    public LugaresDisponiblesDTO(
            long cantidadLibres,
            List<Integer> lugaresLibres
    ) {
        this.cantidadLibres = cantidadLibres;
        this.lugaresLibres = lugaresLibres;
    }

    public long getCantidadLibres() {
        return cantidadLibres;
    }

    public List<Integer> getLugaresLibres() {
        return lugaresLibres;
    }
}