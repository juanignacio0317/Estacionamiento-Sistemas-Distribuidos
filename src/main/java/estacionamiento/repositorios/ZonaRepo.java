package estacionamiento.repositorios;

import estacionamiento.entidades.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ZonaRepo extends JpaRepository<Zona, Long> {

    Optional<Zona> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}