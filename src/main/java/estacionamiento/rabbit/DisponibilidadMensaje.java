
package estacionamiento.rabbit;

public class DisponibilidadMensaje {

    private long lugaresDisponibles;

    public DisponibilidadMensaje() {
    }

    public DisponibilidadMensaje(long lugaresDisponibles) {
        this.lugaresDisponibles = lugaresDisponibles;
    }

    public long getLugaresDisponibles() {
        return lugaresDisponibles;
    }

    public void setLugaresDisponibles(long lugaresDisponibles) {
        this.lugaresDisponibles = lugaresDisponibles;
    }
}
