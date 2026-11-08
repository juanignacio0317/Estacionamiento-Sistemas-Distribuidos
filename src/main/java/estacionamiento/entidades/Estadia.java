
package estacionamiento.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Estadia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "lugar_id", nullable = false)
    private Lugar lugar;

    private LocalDateTime fechaEntrada;

    private LocalDateTime fechaSalida;

    public Estadia() {
    }

    public Estadia(Vehiculo vehiculo, Lugar lugar) {
        this.vehiculo = vehiculo;
        this.lugar = lugar;
        this.fechaEntrada = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Lugar getLugar() {
        return lugar;
    }

    public LocalDateTime getFechaEntrada() {
        return fechaEntrada;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
}
