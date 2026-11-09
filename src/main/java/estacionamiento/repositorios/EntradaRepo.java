package estacionamiento.repositorios;

import estacionamiento.entidades.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntradaRepo extends JpaRepository<Entrada, Long> {

    Optional<Entrada> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}