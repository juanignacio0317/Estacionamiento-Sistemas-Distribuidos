package estacionamiento.repositorios;

import estacionamiento.entidades.Vehiculo;
import estacionamiento.entidades.Sector;
import estacionamiento.estados.EstadoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoRepo extends JpaRepository<Vehiculo, String> {

    List<Vehiculo> findBySectorActual(Sector sector);

    long countBySectorActual(Sector sector);

    List<Vehiculo> findBySectorActualAndEstado(
            Sector sector,
            EstadoVehiculo estado
    );
}