
package estacionamiento.servicios;

import estacionamiento.entidades.*;
import estacionamiento.repositorios.*;
import estacionamiento.estados.*;
import org.springframework.data.domain.PageRequest;
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

    public EstacionamientoServicio(
            LugarRepo lugarRepo,
            EstadiaRepo estadiaRepo,
            VehiculoRepo vehiculoRepo) {

        this.lugarRepo = lugarRepo;
        this.estadiaRepo = estadiaRepo;
        this.vehiculoRepo = vehiculoRepo;
    }

    /**
     * Intenta asignar un lugar libre a un vehículo.
     *
     * @param vehiculo vehículo que intenta ingresar
     * @return true si consiguió un lugar, false si no pudo ingresar
     */
    @Transactional
    public boolean buscarLugar(Vehiculo vehiculo) {

        // 1. Verificar que el vehículo esté esperando en el sector 1.
        if (vehiculo.getSectorActual() == null
                || vehiculo.getSectorActual().getNumero() != 1) {
            return false;
        }

        // 2. Verificar que no tenga ya una estadía activa.
        Optional<Estadia> estadiaActiva =
                estadiaRepo
                        .findFirstByVehiculoAndFechaSalidaIsNullOrderByFechaEntradaDesc(
                                vehiculo
                        );

        if (estadiaActiva.isPresent()) {
            return false;
        }

        // 3. Buscar el primer lugar libre de todo el estacionamiento.
        List<Lugar> lugaresLibres =
                lugarRepo.buscarPrimerLibreBloqueado(
                        EstadoLugar.LIBRE,
                        PageRequest.of(0, 1)
                );

        // 4. Si no hay lugares libres, el vehículo permanece afuera.
        if (lugaresLibres.isEmpty()) {
            return false;
        }

        Lugar lugar = lugaresLibres.get(0);

        // 5. Ocupar el lugar.
        lugar.setEstado(EstadoLugar.OCUPADO);
        lugarRepo.save(lugar);

        // 6. Registrar la estadía.
        Estadia estadia = new Estadia(vehiculo, lugar);
        estadiaRepo.save(estadia);

        // 7. Actualizar el estado del vehículo.
        vehiculo.setEstado(EstadoVehiculo.ESTACIONADO);

        // Ya no pertenece a ningún sector externo.
        vehiculo.setSectorActual(null);

        vehiculoRepo.save(vehiculo);

        return true;
    }

    /**
     * Libera el lugar ocupado por un vehículo y registra su salida.
     *
     * @param vehiculo vehículo que sale del estacionamiento
     */
    @Transactional
    public void liberarLugar(Vehiculo vehiculo) {

        // 1. Buscar la estadía activa del vehículo.
        Optional<Estadia> resultado =
                estadiaRepo
                        .findFirstByVehiculoAndFechaSalidaIsNullOrderByFechaEntradaDesc(
                                vehiculo
                        );

        // Si no tiene una estadía activa, no hay nada que liberar.
        if (resultado.isEmpty()) {
            return;
        }

        Estadia estadia = resultado.get();

        // 2. Liberar el lugar.
        Lugar lugar = estadia.getLugar();
        lugar.setEstado(EstadoLugar.LIBRE);

        // 3. Registrar la fecha de salida.
        estadia.setFechaSalida(LocalDateTime.now());

        // 4. Actualizar el estado del vehículo.
        vehiculo.setEstado(EstadoVehiculo.SALIENDO);

        // 5. Guardar los cambios.
        lugarRepo.save(lugar);
        estadiaRepo.save(estadia);
        vehiculoRepo.save(vehiculo);
    }
}
