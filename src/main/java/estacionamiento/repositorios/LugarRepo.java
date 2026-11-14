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

    List<Lugar> findAllByOrderByNumeroAsc();

    List<Lugar> findAllByEstadoOrderByNumeroAsc(EstadoLugar estado);


    @Query("""
    SELECT COUNT(l)
    FROM Lugar l
    WHERE l.estado = estacionamiento.estados.EstadoLugar.LIBRE
    AND NOT EXISTS (
        SELECT r.id
        FROM Reserva r
        WHERE r.lugar = l
        AND r.activa = true
    )
""")
    long contarLugaresLibresNoReservados();

    @Query("""
    SELECT l
    FROM Lugar l
    WHERE l.estado = estacionamiento.estados.EstadoLugar.LIBRE
    AND NOT EXISTS (
        SELECT r.id
        FROM Reserva r
        WHERE r.lugar = l
        AND r.activa = true
    )
    ORDER BY l.numero ASC
""")
    List<Lugar> buscarLugaresLibresNoReservados();

}