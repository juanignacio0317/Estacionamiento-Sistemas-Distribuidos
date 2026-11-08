
package estacionamiento.entidades;
import estacionamiento.estados.EstadoVehiculo;
import jakarta.persistence.*;

@Entity
public class Vehiculo {

    @Id
    private String matricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVehiculo estado;

    @ManyToOne
    @JoinColumn(name = "sector_actual_id")
    private Sector sectorActual;

    public Vehiculo() {
    }

    public Vehiculo(String matricula) {
        this.matricula = matricula;
        this.estado = EstadoVehiculo.ESPERANDO;
    }

    public String getMatricula() {
        return matricula;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    public Sector getSectorActual() {
        return sectorActual;
    }

    public void setSectorActual(Sector sectorActual) {
        this.sectorActual = sectorActual;
    }
}
