

package estacionamiento.servicios;

import estacionamiento.entidades.*;
import estacionamiento.estados.EstadoVehiculo;
import estacionamiento.repositorios.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CirculacionServicio {

    private static final int MAXIMO_MOVIMIENTOS_POR_CICLO = 10;

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

    @Transactional
    public void avanzarVehiculos() {

        List<Zona> zonas = zonaRepo.findAllByOrderByIdAsc();

        Map<Zona, List<Vehiculo>> vehiculosSector1 =
                new LinkedHashMap<>();

        Map<Long, List<Vehiculo>> vehiculosPorSector =
                new LinkedHashMap<>();

        // Guardamos una fotografía de los vehículos de cada sector.
        for (Zona zona : zonas) {

            for (int numero = 1; numero <= 10; numero++) {

                Sector sector = sectorRepo.findByZonaAndNumero(
                        zona,
                        numero
                ).orElseThrow();

                List<Vehiculo> vehiculos =
                        vehiculoRepo.findBySectorActualAndEstado(
                                sector,
                                EstadoVehiculo.ESPERANDO
                        );

                vehiculosPorSector.put(
                        sector.getId(),
                        new ArrayList<>(vehiculos)
                );

                if (numero == 1) {
                    vehiculosSector1.put(
                            zona,
                            new ArrayList<>(vehiculos)
                    );
                }
            }
        }

        // Movemos desde el sector 10 hacia el sector 2.
        for (Zona zona : zonas) {

            for (int numero = 10; numero >= 2; numero--) {

                Sector origen = sectorRepo.findByZonaAndNumero(
                        zona,
                        numero
                ).orElseThrow();

                // Bloqueamos el destino antes de consultar su capacidad.
                Sector destino = sectorRepo.findByZonaAndNumeroForUpdate(
                        zona,
                        numero - 1
                ).orElseThrow();

                List<Vehiculo> candidatos =
                        vehiculosPorSector.get(origen.getId());

                if (candidatos == null || candidatos.isEmpty()) {
                    continue;
                }

                long cantidadEnDestino =
                        vehiculoRepo.countBySectorActual(destino);

                int espacioDisponible =
                        (int) (destino.getCapacidadMaxima()
                                - cantidadEnDestino);

                int cantidadAMover = Math.min(
                        MAXIMO_MOVIMIENTOS_POR_CICLO,
                        espacioDisponible
                );

                cantidadAMover = Math.max(0, cantidadAMover);

                int movidos = 0;

                for (Vehiculo vehiculo : candidatos) {

                    if (movidos >= cantidadAMover) {
                        break;
                    }

                    if (vehiculo.getSectorActual() != null
                            && vehiculo.getSectorActual().getId()
                            .equals(origen.getId())) {

                        vehiculo.setSectorActual(destino);
                        vehiculoRepo.save(vehiculo);

                        movidos++;
                    }
                }
            }
        }

        // Procesamos los intentos de ingreso de los vehículos
        // que estaban en el sector 1 al comienzo del ciclo.
        ingresoServicio.procesarIngresos(vehiculosSector1);
    }
}
