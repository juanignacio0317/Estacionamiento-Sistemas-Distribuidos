package estacionamiento.repositorios;

import estacionamiento.entidades.Sector;
import estacionamiento.entidades.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehiculoRepo extends JpaRepository<Vehiculo, String> {

    List<Vehiculo> findBySectorActual(Sector sector);

    long countBySectorActual(Sector sector);
}