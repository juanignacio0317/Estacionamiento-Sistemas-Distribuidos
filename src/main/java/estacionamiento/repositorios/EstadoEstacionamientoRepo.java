
package estacionamiento.repositorios;

import estacionamiento.entidades.EstadoEstacionamiento;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.Optional;

@Repository
public interface EstadoEstacionamientoRepo
        extends JpaRepository<EstadoEstacionamiento, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT e FROM EstadoEstacionamiento e WHERE e.id = :id")
    Optional<EstadoEstacionamiento> bloquearParaLectura(
            @Param("id") Long id
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EstadoEstacionamiento e WHERE e.id = :id")
    Optional<EstadoEstacionamiento> bloquearParaEscritura(
            @Param("id") Long id
    );
}

