package estacionamiento.entidades;

import jakarta.persistence.*;

@Entity
public class Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private int posicionRecorrido;

    public Entrada() {
    }

    public Entrada(String nombre, int posicionRecorrido) {
        this.nombre = nombre;
        this.posicionRecorrido = posicionRecorrido;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPosicionRecorrido() {
        return posicionRecorrido;
    }

    public void setPosicionRecorrido(int posicionRecorrido) {
        this.posicionRecorrido = posicionRecorrido;
    }
}