package estacionamiento.repositorios;

import estacionamiento.entidades.Entrada;
import estacionamiento.entidades.Zona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZonaRepo extends JpaRepository<Zona, Long> {

    Optional<Zona> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    Optional<Zona> findByEntrada(Entrada entrada);

    List<Zona> findAllByOrderByIdAsc();
}