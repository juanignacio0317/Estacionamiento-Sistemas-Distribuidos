package estacionamiento.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Vehiculo {

    @Id
    @Column(unique = true, nullable = false)
    private String matricula;

    public Vehiculo(){}

    public Vehiculo(String matricula) {
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }
}
