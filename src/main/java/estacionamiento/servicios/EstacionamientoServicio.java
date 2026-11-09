package estacionamiento.servicios;

import estacionamiento.entidades.*;
import estacionamiento.estados.EstadoLugar;
import estacionamiento.estados.EstadoVehiculo;
import estacionamiento.repositorios.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EstacionamientoServicio {

    private final LugarRepo lugarRepo;
    private final EstadiaRepo estadiaRepo;
    private final VehiculoRepo vehiculoRepo;
    private final EntradaRepo entradaRepo;
    private final ZonaRepo zonaRepo;
    private final SectorRepo sectorRepo;

    public EstacionamientoServicio(
            LugarRepo lugarRepo,
            EstadiaRepo estadiaRepo,
            VehiculoRepo vehiculoRepo,
            EntradaRepo entradaRepo,
            ZonaRepo zonaRepo,
            SectorRepo sectorRepo
    ) {
        this.lugarRepo = lugarRepo;
        this.estadiaRepo = estadiaRepo;
        this.vehiculoRepo = vehiculoRepo;
        this.entradaRepo = entradaRepo;
        this.zonaRepo = zonaRepo;
        this.sectorRepo = sectorRepo;
    }

    public long cantidadLugaresLibres() {
        return lugarRepo.countByEstado(EstadoLugar.LIBRE);
    }

    @Transactional
    public boolean buscarLugar(String matricula, Long entradaId) {

        Vehiculo vehiculo = vehiculoRepo.findById(matricula)
                .orElseThrow();

        Entrada entrada = entradaRepo.findById(entradaId)
                .orElseThrow();

        if (vehiculo.getEstado() != EstadoVehiculo.ESPERANDO ||
                vehiculo.getSectorActual() == null ||
                vehiculo.getSectorActual().getNumero() != 1) {
            return false;
        }

        Optional<Estadia> estadiaActiva =
                estadiaRepo.findFirstByVehiculoAndFechaSalidaIsNullOrderByFechaEntradaDesc(
                        vehiculo
                );

        if (estadiaActiva.isPresent()) {
            return false;
        }

        // Bloquea los lugares y los obtiene en orden circular.
        List<Lugar> lugares = lugarRepo.buscarTodosBloqueados();

        Lugar lugarEncontrado = null;

        int posicionInicial = entrada.getPosicionRecorrido() - 1;

        for (int i = 0; i < lugares.size(); i++) {

            int indice = (posicionInicial + i) % lugares.size();

            Lugar lugar = lugares.get(indice);

            if (lugar.getEstado() == EstadoLugar.LIBRE) {
                lugarEncontrado = lugar;
                break;
            }
        }

        // Completó el recorrido y no encontró lugar.
        if (lugarEncontrado == null) {
            devolverAEntrada(vehiculo, entrada);
            return false;
        }

        // Ocupa el primer lugar libre encontrado.
        lugarEncontrado.setEstado(EstadoLugar.OCUPADO);
        lugarRepo.save(lugarEncontrado);

        Estadia estadia = new Estadia(vehiculo, lugarEncontrado);
        estadiaRepo.save(estadia);

        vehiculo.setEstado(EstadoVehiculo.ESTACIONADO);
        vehiculo.setSectorActual(null);
        vehiculo.setEntradaIngreso(entrada);

        vehiculoRepo.save(vehiculo);

        return true;
    }

    private void devolverAEntrada(Vehiculo vehiculo, Entrada entrada) {

        Zona zona = zonaRepo.findByEntrada(entrada)
                .orElseThrow();

        Sector sector10 = sectorRepo.findByZonaAndNumero(zona, 10)
                .orElseThrow();

        long cantidad = vehiculoRepo.countBySectorActual(sector10);

        if (cantidad >= sector10.getCapacidadMaxima()) {
            throw new IllegalStateException(
                    "El sector 10 de " + zona.getNombre() + " está lleno"
            );
        }

        vehiculo.setSectorActual(sector10);
        vehiculo.setEstado(EstadoVehiculo.ESPERANDO);
        vehiculo.setEntradaIngreso(null);

        vehiculoRepo.save(vehiculo);
    }

    @Transactional
    public void liberarLugar(String matricula) {

        Vehiculo vehiculo = vehiculoRepo.findById(matricula)
                .orElseThrow();

        Optional<Estadia> estadiaOptional =
                estadiaRepo.findFirstByVehiculoAndFechaSalidaIsNullOrderByFechaEntradaDesc(
                        vehiculo
                );

        if (estadiaOptional.isEmpty()) {
            return;
        }

        Estadia estadia = estadiaOptional.get();

        Lugar lugar = estadia.getLugar();

        lugar.setEstado(EstadoLugar.LIBRE);
        estadia.setFechaSalida(LocalDateTime.now());

        vehiculo.setEstado(EstadoVehiculo.SALIENDO);

        lugarRepo.save(lugar);
        estadiaRepo.save(estadia);
        vehiculoRepo.save(vehiculo);
    }
}