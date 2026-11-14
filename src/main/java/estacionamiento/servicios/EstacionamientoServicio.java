package estacionamiento.servicios;

import estacionamiento.entidades.*;
import estacionamiento.estados.EstadoLugar;
import estacionamiento.estados.EstadoVehiculo;
import estacionamiento.repositorios.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import estacionamiento.rabbit.DisponibilidadCambioEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EstacionamientoServicio {

    private final LugarRepo lugarRepo;
    private final EstadiaRepo estadiaRepo;
    private final VehiculoRepo vehiculoRepo;
    private final EntradaRepo entradaRepo;
    private final ZonaRepo zonaRepo;
    private final SectorRepo sectorRepo;
    private final ApplicationEventPublisher eventPublisher;

    public EstacionamientoServicio(
            LugarRepo lugarRepo,
            EstadiaRepo estadiaRepo,
            VehiculoRepo vehiculoRepo,
            EntradaRepo entradaRepo,
            ZonaRepo zonaRepo,
            SectorRepo sectorRepo,
            ApplicationEventPublisher eventPublisher
    ) {
        this.lugarRepo = lugarRepo;
        this.estadiaRepo = estadiaRepo;
        this.vehiculoRepo = vehiculoRepo;
        this.entradaRepo = entradaRepo;
        this.zonaRepo = zonaRepo;
        this.sectorRepo = sectorRepo;
        this.eventPublisher = eventPublisher;
    }

    public long cantidadLugaresLibres() {
        return lugarRepo.countByEstado(EstadoLugar.LIBRE);
    }

    @Transactional(readOnly = true)
    public List<Lugar> obtenerLugares() {
        return lugarRepo.findAllByOrderByNumeroAsc();
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

        // Primero bloqueamos el sector 10 de la zona de entrada.
        // Esto sincroniza los vehículos que necesitan regresar
        // a esta entrada cuando no encuentran lugar.
        Zona zona = zonaRepo.findByEntrada(entrada)
                .orElseThrow();

        Sector sector10 = sectorRepo.findByZonaAndNumeroForUpdate(
                zona,
                10
        ).orElseThrow();

        // Después bloqueamos los lugares de estacionamiento.
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
            return devolverAEntrada(vehiculo, sector10);
        }

        // Ocupa el primer lugar libre encontrado.
        lugarEncontrado.setEstado(EstadoLugar.OCUPADO);
        lugarRepo.save(lugarEncontrado);

        eventPublisher.publishEvent(new DisponibilidadCambioEvent());

        // Guarda también la entrada utilizada.
        Estadia estadia = new Estadia(
                vehiculo,
                lugarEncontrado,
                entrada
        );

        estadiaRepo.save(estadia);

        vehiculo.setEstado(EstadoVehiculo.ESTACIONADO);
        vehiculo.setSectorActual(null);
        vehiculo.setEntradaIngreso(entrada);

        vehiculoRepo.save(vehiculo);

        return true;
    }

    private boolean devolverAEntrada(
            Vehiculo vehiculo,
            Sector sector10
    ) {

        long cantidad = vehiculoRepo.countBySectorActual(sector10);

        // Si el sector está lleno, el vehículo permanece
        // en el sector 1 y podrá volver a intentar ingresar.
        if (cantidad >= sector10.getCapacidadMaxima()) {
            return false;
        }

        vehiculo.setSectorActual(sector10);
        vehiculo.setEstado(EstadoVehiculo.ESPERANDO);
        vehiculo.setEntradaIngreso(null);

        vehiculoRepo.save(vehiculo);

        return true;
    }

    @Transactional
    public void liberarLugar(String matricula, Entrada entradaSalida) {

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

        eventPublisher.publishEvent(new DisponibilidadCambioEvent());

        estadia.setFechaSalida(LocalDateTime.now());
        estadia.setEntradaSalida(entradaSalida);

        vehiculo.setEstado(EstadoVehiculo.SALIENDO);

        lugarRepo.save(lugar);
        estadiaRepo.save(estadia);
        vehiculoRepo.save(vehiculo);
    }

    @Transactional(readOnly = true)
    public Map<Integer, String> obtenerMatriculasPorLugar() {

        List<Object[]> resultados =
                estadiaRepo.buscarLugaresOcupadosConMatricula();

        Map<Integer, String> matriculasPorLugar = new HashMap<>();

        for (Object[] resultado : resultados) {

            int numeroLugar = ((Number) resultado[0]).intValue();
            String matricula = (String) resultado[1];

            matriculasPorLugar.put(numeroLugar, matricula);
        }

        return matriculasPorLugar;
    }
}
