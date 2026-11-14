package estacionamiento.servicios;

import estacionamiento.dto.EstadoVehiculoDTO;
import estacionamiento.dto.EstadiaDTO;
import estacionamiento.dto.LugaresDisponiblesDTO;

import estacionamiento.entidades.Vehiculo;
import estacionamiento.entidades.Estadia;
import estacionamiento.entidades.Lugar;

import estacionamiento.estados.EstadoVehiculo;
import estacionamiento.estados.EstadoLugar;

import estacionamiento.repositorios.VehiculoRepo;
import estacionamiento.repositorios.EstadiaRepo;
import estacionamiento.repositorios.LugarRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultasAdministradorServicio {

    private final VehiculoRepo vehiculoRepo;
    private final EstadiaRepo estadiaRepo;
    private final LugarRepo lugarRepo;

    public ConsultasAdministradorServicio(
            VehiculoRepo vehiculoRepo,
            EstadiaRepo estadiaRepo,
            LugarRepo lugarRepo
    ) {
        this.vehiculoRepo = vehiculoRepo;
        this.estadiaRepo = estadiaRepo;
        this.lugarRepo = lugarRepo;
    }

    // CONSULTA 1: ESTADO ACTUAL DE UN VEHÍCULO

    @Transactional(readOnly = true)
    public Optional<EstadoVehiculoDTO> consultarEstadoVehiculo(
            String matricula
    ) {

        String matriculaNormalizada =
                ValidadorMatricula.normalizar(matricula);

        if (!ValidadorMatricula.esValida(matriculaNormalizada)) {
            return Optional.empty();
        }

        Optional<Vehiculo> resultado =
                vehiculoRepo.findById(matriculaNormalizada);

        if (resultado.isEmpty()) {
            return Optional.empty();
        }

        Vehiculo vehiculo = resultado.get();

        Integer numeroLugar = null;

        if (vehiculo.getEstado() == EstadoVehiculo.ESTACIONADO) {

            Optional<Estadia> estadia =
                    estadiaRepo
                            .findFirstByVehiculoAndFechaSalidaIsNullOrderByFechaEntradaDesc(
                                    vehiculo
                            );

            if (estadia.isPresent()) {
                numeroLugar = estadia.get().getLugar().getNumero();
            }
        }

        return Optional.of(
                new EstadoVehiculoDTO(
                        vehiculo.getMatricula(),
                        vehiculo.getEstado(),
                        numeroLugar
                )
        );
    }

    // CONSULTA 2: HISTORIAL DE ESTADÍAS

    @Transactional(readOnly = true)
    public Optional<List<EstadiaDTO>> consultarHistorialEstadias(
            String matricula
    ) {

        String matriculaNormalizada =
                ValidadorMatricula.normalizar(matricula);

        if (!ValidadorMatricula.esValida(matriculaNormalizada)) {
            return Optional.empty();
        }

        Optional<Vehiculo> resultado =
                vehiculoRepo.findById(matriculaNormalizada);

        if (resultado.isEmpty()) {
            return Optional.empty();
        }

        List<Estadia> estadias =
                estadiaRepo
                        .findAllByVehiculo_MatriculaOrderByFechaEntradaDesc(
                                matriculaNormalizada
                        );

        List<EstadiaDTO> historial = new ArrayList<>();

        for (Estadia estadia : estadias) {

            String puertaEntrada =
                    estadia.getEntradaIngreso() != null
                            ? estadia.getEntradaIngreso().getNombre()
                            : null;

            String puertaSalida =
                    estadia.getEntradaSalida() != null
                            ? estadia.getEntradaSalida().getNombre()
                            : null;

            Integer numeroLugar =
                    estadia.getLugar() != null
                            ? estadia.getLugar().getNumero()
                            : null;

            historial.add(new EstadiaDTO(
                    estadia.getFechaEntrada(),
                    puertaEntrada,
                    numeroLugar,
                    estadia.getFechaSalida(),
                    puertaSalida
            ));
        }

        return Optional.of(historial);
    }

    // CONSULTA 3: LUGARES DISPONIBLES

    @Transactional(readOnly = true)
    public LugaresDisponiblesDTO consultarLugaresDisponibles() {

        List<Lugar> lugares =
                lugarRepo.buscarLugaresLibresNoReservados();;

        List<Integer> numerosLibres = new ArrayList<>();

        for (Lugar lugar : lugares) {
            numerosLibres.add(lugar.getNumero());
        }

        return new LugaresDisponiblesDTO(
                numerosLibres.size(),
                numerosLibres
        );
    }
}
