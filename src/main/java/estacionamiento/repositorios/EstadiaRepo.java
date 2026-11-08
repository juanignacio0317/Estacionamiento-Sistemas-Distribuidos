package estacionamiento.repositorios;

import estacionamiento.entidades.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadiaRepo extends JpaRepository<Estadia, Long> {

    Optional<Estadia>
    findFirstByVehiculoAndFechaSalidaIsNullOrderByFechaEntradaDesc(
            Vehiculo vehiculo
    );
}