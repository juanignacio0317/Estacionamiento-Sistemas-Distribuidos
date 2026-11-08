package estacionamiento.repositorios;

import estacionamiento.entidades.Lugar;
import estacionamiento.estados.EstadoLugar;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;

public interface LugarRepo extends JpaRepository<Lugar, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT l FROM Lugar l
        WHERE l.estado = :estado
        ORDER BY l.numero ASC
    """)
    List<Lugar> buscarPrimerLibreBloqueado(
            @Param("estado") EstadoLugar estado,
            Pageable pageable
    );

    boolean existsByNumero(int numero);
}