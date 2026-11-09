
package estacionamiento.entidades;

import jakarta.persistence.*;

@Entity
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private int capacidadMaxima;

    @OneToOne
    @JoinColumn(name = "entrada_id", unique = true)
    private Entrada entrada;

    public Zona() {
    }

    public Zona(String nombre, int capacidadMaxima, Entrada entrada) {
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.entrada = entrada;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }
}
