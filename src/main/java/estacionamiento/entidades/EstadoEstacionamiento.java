
package estacionamiento.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class EstadoEstacionamiento {

    @Id
    private Long id;

    @Column(nullable = false)
    private boolean entradasAbiertas;

    private LocalDateTime fechaActualizacion;

    public EstadoEstacionamiento() {
    }

    public EstadoEstacionamiento(Long id) {
        this.id = id;
        this.entradasAbiertas = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public boolean isEntradasAbiertas() {
        return entradasAbiertas;
    }

    public void setEntradasAbiertas(boolean entradasAbiertas) {
        this.entradasAbiertas = entradasAbiertas;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}

