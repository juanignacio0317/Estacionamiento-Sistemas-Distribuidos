
package estacionamiento.repositorios;

import estacionamiento.entidades.Reserva;
import estacionamiento.entidades.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservaRepo extends JpaRepository<Reserva, Long> {

    Optional<Reserva> findByLugarAndActivaTrue(Lugar lugar);

    List<Reserva> findAllByActivaTrue();

    List<Reserva> findAllByActivaTrueOrderByLugarNumeroAsc();

    boolean existsByLugarAndActivaTrue(Lugar lugar);
}
