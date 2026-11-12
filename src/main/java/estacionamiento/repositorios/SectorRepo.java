
package estacionamiento.repositorios;

import estacionamiento.entidades.Sector;
import estacionamiento.entidades.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface SectorRepo extends JpaRepository<Sector, Long> {

    Optional<Sector> findByZonaAndNumero(Zona zona, int numero);

    List<Sector> findByZona(Zona zona);

    List<Sector> findByZonaOrderByNumeroAsc(Zona zona);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Sector s WHERE s.zona = :zona AND s.numero = :numero")
    Optional<Sector> findByZonaAndNumeroForUpdate(
            @Param("zona") Zona zona,
            @Param("numero") int numero
    );
}
