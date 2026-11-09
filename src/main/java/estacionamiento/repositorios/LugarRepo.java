package estacionamiento.repositorios;

import estacionamiento.entidades.Lugar;
import estacionamiento.estados.EstadoLugar;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LugarRepo extends JpaRepository<Lugar, Long> {

    boolean existsByNumero(int numero);

    long countByEstado(EstadoLugar estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT l FROM Lugar l
    ORDER BY l.numero ASC
""")
    List<Lugar> buscarTodosBloqueados();
}