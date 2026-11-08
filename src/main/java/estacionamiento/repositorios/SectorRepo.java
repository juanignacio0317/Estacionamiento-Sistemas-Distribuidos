package estacionamiento.repositorios;

import estacionamiento.entidades.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SectorRepo extends JpaRepository<Sector, Long> {

    Optional<Sector> findByZonaAndNumero(Zona zona, int numero);

    List<Sector> findByZona(Zona zona);

    List<Sector> findByZonaOrderByNumeroAsc(Zona zona);
}