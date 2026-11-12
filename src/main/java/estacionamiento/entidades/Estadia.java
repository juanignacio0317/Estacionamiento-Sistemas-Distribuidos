

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

    @ManyToOne
    @JoinColumn(name = "entrada_ingreso_id")
    private Entrada entradaIngreso;

    @ManyToOne
    @JoinColumn(name = "entrada_salida_id")
    private Entrada entradaSalida;

    private LocalDateTime fechaEntrada;

    private LocalDateTime fechaSalida;

    public Estadia() {
    }

    public Estadia(Vehiculo vehiculo, Lugar lugar, Entrada entradaIngreso) {
        this.vehiculo = vehiculo;
        this.lugar = lugar;
        this.entradaIngreso = entradaIngreso;
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

    public Entrada getEntradaIngreso() {
        return entradaIngreso;
    }

    public Entrada getEntradaSalida() {
        return entradaSalida;
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

    public void setEntradaSalida(Entrada entradaSalida) {
        this.entradaSalida = entradaSalida;
    }
}
