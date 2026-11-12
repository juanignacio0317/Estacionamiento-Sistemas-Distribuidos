

package estacionamiento.entidades;

import jakarta.persistence.*;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"zona_id", "numero"}
        )
)
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numero;

    private int capacidadMaxima = 34;

    @ManyToOne
    @JoinColumn(name = "zona_id", nullable = false)
    private Zona zona;

    public Sector() {
    }

    public Sector(int numero, Zona zona) {
        this.numero = numero;
        this.zona = zona;
        this.capacidadMaxima = numero == 10 ? 40 : 34;
    }

    public Long getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }
}
