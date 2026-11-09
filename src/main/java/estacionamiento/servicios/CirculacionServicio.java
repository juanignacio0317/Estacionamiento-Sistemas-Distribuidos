package estacionamiento.servicios;

import estacionamiento.entidades.*;
import estacionamiento.estados.EstadoVehiculo;
import estacionamiento.repositorios.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CirculacionServicio {

    private final VehiculoRepo vehiculoRepo;
    private final SectorRepo sectorRepo;
    private final ZonaRepo zonaRepo;
    private final IngresoServicio ingresoServicio;

    public CirculacionServicio(
            VehiculoRepo vehiculoRepo,
            SectorRepo sectorRepo,
            ZonaRepo zonaRepo,
            IngresoServicio ingresoServicio
    ) {
        this.vehiculoRepo = vehiculoRepo;
        this.sectorRepo = sectorRepo;
        this.zonaRepo = zonaRepo;
        this.ingresoServicio = ingresoServicio;
    }

    public void avanzarVehiculos() {

        List<Zona> zonas = zonaRepo.findAllByOrderByIdAsc();

        Map<Zona, List<Vehiculo>> vehiculosSector1 =
                new LinkedHashMap<>();

        // Guardamos los vehículos que ya estaban en sector 1.
        for (Zona zona : zonas) {

            Sector sector1 = sectorRepo.findByZonaAndNumero(
                    zona, 1
            ).orElseThrow();

            List<Vehiculo> vehiculos =
                    vehiculoRepo.findBySectorActualAndEstado(
                            sector1,
                            EstadoVehiculo.ESPERANDO
                    );

            vehiculosSector1.put(zona, vehiculos);
        }

        // Avanzamos los vehículos de los sectores 2 a 10.
        for (Zona zona : zonas) {

            for (int numero = 2; numero <= 10; numero++) {

                Sector origen = sectorRepo.findByZonaAndNumero(
                        zona, numero
                ).orElseThrow();

                Sector destino = sectorRepo.findByZonaAndNumero(
                        zona, numero - 1
                ).orElseThrow();

                List<Vehiculo> vehiculos =
                        vehiculoRepo.findBySectorActualAndEstado(
                                origen,
                                EstadoVehiculo.ESPERANDO
                        );

                for (Vehiculo vehiculo : vehiculos) {

                    long cantidad =
                            vehiculoRepo.countBySectorActual(destino);

                    if (cantidad < destino.getCapacidadMaxima()) {
                        vehiculo.setSectorActual(destino);
                        vehiculoRepo.save(vehiculo);
                    }
                }
            }
        }

        // Procesamos juntos los intentos de las tres entradas.
        ingresoServicio.procesarIngresos(vehiculosSector1);
    }
}