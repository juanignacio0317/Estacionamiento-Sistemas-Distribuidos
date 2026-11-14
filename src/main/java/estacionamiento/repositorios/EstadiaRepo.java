package estacionamiento.repositorios;

import estacionamiento.entidades.Estadia;
import estacionamiento.entidades.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EstadiaRepo extends JpaRepository<Estadia, Long> {

    Optional<Estadia> findFirstByVehiculoAndFechaSalidaIsNullOrderByFechaEntradaDesc(
            Vehiculo vehiculo
    );

    List<Estadia> findAllByFechaSalidaIsNull();

    @Query("""
    SELECT e.lugar.numero, e.vehiculo.matricula
    FROM Estadia e
    WHERE e.fechaSalida IS NULL
""")
    List<Object[]> buscarLugaresOcupadosConMatricula();
}