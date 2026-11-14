
package estacionamiento.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lugar_id", nullable = false)
    private Lugar lugar;

    @Column(nullable = false)
    private LocalDateTime fechaReserva;

    private LocalDateTime fechaCancelacion;

    @Column(nullable = false)
    private boolean activa;

    public Reserva() {
    }

    public Reserva(Lugar lugar) {
        this.lugar = lugar;
        this.fechaReserva = LocalDateTime.now();
        this.activa = true;
    }

    public Long getId() {
        return id;
    }

    public Lugar getLugar() {
        return lugar;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public LocalDateTime getFechaCancelacion() {
        return fechaCancelacion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void cancelar() {
        this.activa = false;
        this.fechaCancelacion = LocalDateTime.now();
    }
}
