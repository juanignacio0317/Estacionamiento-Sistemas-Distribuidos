
package estacionamiento.servicios;

import estacionamiento.entidades.*;
import estacionamiento.repositorios.*;
import estacionamiento.estados.EstadoVehiculo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SalidaServicio {

    private final EstadiaRepo estadiaRepo;
    private final EntradaRepo entradaRepo;
    private final ZonaRepo zonaRepo;
    private final SectorRepo sectorRepo;
    private final VehiculoRepo vehiculoRepo;
    private final EstacionamientoServicio estacionamientoServicio;

    public SalidaServicio(
            EstadiaRepo estadiaRepo,
            EntradaRepo entradaRepo,
            ZonaRepo zonaRepo,
            SectorRepo sectorRepo,
            VehiculoRepo vehiculoRepo,
            EstacionamientoServicio estacionamientoServicio
    ) {
        this.estadiaRepo = estadiaRepo;
        this.entradaRepo = entradaRepo;
        this.zonaRepo = zonaRepo;
        this.sectorRepo = sectorRepo;
        this.vehiculoRepo = vehiculoRepo;
        this.estacionamientoServicio = estacionamientoServicio;
    }

    @Transactional
    public boolean finalizarEstadia(String matricula) {

        Vehiculo vehiculo = vehiculoRepo.findById(matricula)
                .orElseThrow();

        Optional<Estadia> estadiaOptional =
                estadiaRepo.findFirstByVehiculoAndFechaSalidaIsNullOrderByFechaEntradaDesc(
                        vehiculo
                );

        if (estadiaOptional.isEmpty()) {
            return true;
        }

        Estadia estadia = estadiaOptional.get();

        Lugar lugar = estadia.getLugar();

        // Determina por qué entrada saldrá el vehículo.
        Entrada entradaSalida = buscarPrimeraEntrada(lugar);

        Zona zonaSalida = zonaRepo.findByEntrada(entradaSalida)
                .orElseThrow();

        Sector sector10 = sectorRepo.findByZonaAndNumero(
                zonaSalida, 10
        ).orElseThrow();

        long cantidad =
                vehiculoRepo.countBySectorActual(sector10);

        if (cantidad >= sector10.getCapacidadMaxima()) {
            return false;
        }

        // Libera el lugar y guarda la entrada de salida.
        estacionamientoServicio.liberarLugar(
                matricula,
                entradaSalida
        );

        vehiculo.setSectorActual(sector10);
        vehiculo.setEstado(EstadoVehiculo.ESPERANDO);
        vehiculo.setEntradaIngreso(null);

        vehiculoRepo.save(vehiculo);

        return true;
    }

    private Entrada buscarPrimeraEntrada(Lugar lugar) {

        List<Entrada> entradas = entradaRepo.findAll();

        if (entradas.isEmpty()) {
            throw new IllegalStateException("No hay entradas registradas");
        }

        int posicionLugar = lugar.getNumero();

        Entrada primeraEntrada = null;
        int menorDistancia = Integer.MAX_VALUE;

        for (Entrada entrada : entradas) {

            int distancia =
                    entrada.getPosicionRecorrido() - posicionLugar;

            if (distancia < 0) {
                distancia += 100;
            }

            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                primeraEntrada = entrada;
            }
        }

        return primeraEntrada;
    }
}
