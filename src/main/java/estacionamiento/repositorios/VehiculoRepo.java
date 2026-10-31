package estacionamiento.repositorios;

import estacionamiento.entidades.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepo extends JpaRepository<Vehiculo, String> {
}