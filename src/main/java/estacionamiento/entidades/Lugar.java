package estacionamiento.entidades;
import estacionamiento.estados.EstadoLugar;
import jakarta.persistence.*;

@Entity
public class Lugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private int numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLugar estado;

    public Lugar() {
    }

    public Lugar(int numero) {
        this.numero = numero;
        this.estado = EstadoLugar.LIBRE;
    }

    public Long getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public EstadoLugar getEstado() {
        return estado;
    }

    public void setEstado(EstadoLugar estado) {
        this.estado = estado;
    }
}
