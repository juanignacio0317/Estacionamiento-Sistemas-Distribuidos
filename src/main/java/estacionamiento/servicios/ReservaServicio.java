
package estacionamiento.servicios;

import estacionamiento.entidades.Lugar;
import estacionamiento.entidades.Reserva;
import estacionamiento.rabbit.DisponibilidadCambioEvent;
import estacionamiento.repositorios.LugarRepo;
import estacionamiento.repositorios.ReservaRepo;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
public class ReservaServicio {

    private final LugarRepo lugarRepo;
    private final ReservaRepo reservaRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final ActividadServicio actividadServicio;

    public ReservaServicio(
            LugarRepo lugarRepo,
            ReservaRepo reservaRepo,
            ApplicationEventPublisher eventPublisher,
            ActividadServicio actividadServicio
    ) {
        this.lugarRepo = lugarRepo;
        this.reservaRepo = reservaRepo;
        this.eventPublisher = eventPublisher;
        this.actividadServicio = actividadServicio;
    }

    @Transactional
    public void reservar(int numeroLugar) {

        validarNumero(numeroLugar);

        // Utilizamos el mismo bloqueo que las búsquedas
        // de lugares de los vehículos.
        List<Lugar> lugares = lugarRepo.buscarTodosBloqueados();

        Lugar lugar = buscarLugarPorNumero(lugares, numeroLugar);

        if (reservaRepo.existsByLugarAndActivaTrue(lugar)) {
            throw new IllegalStateException(
                    "El lugar " + numeroLugar + " ya tiene una reserva activa."
            );
        }

        Reserva reserva = new Reserva(lugar);
        reservaRepo.save(reserva);

        actividadServicio.registrar(
                "Se reservó el lugar " + numeroLugar
        );

        eventPublisher.publishEvent(new DisponibilidadCambioEvent());
    }

    @Transactional
    public boolean cancelarReserva(int numeroLugar) {

        validarNumero(numeroLugar);

        // Mismo mecanismo de sincronización que reservar()
        // y buscarLugar().
        List<Lugar> lugares = lugarRepo.buscarTodosBloqueados();

        Lugar lugar = buscarLugarPorNumero(lugares, numeroLugar);

        Reserva reserva = reservaRepo
                .findByLugarAndActivaTrue(lugar)
                .orElse(null);

        if (reserva == null) {
            return false;
        }

        reserva.cancelar();
        reservaRepo.save(reserva);

        actividadServicio.registrar(
                "Se canceló la reserva del lugar " + numeroLugar
        );

        eventPublisher.publishEvent(new DisponibilidadCambioEvent());

        return true;
    }

    @Transactional(readOnly = true)
    public List<Integer> obtenerLugaresReservados() {

        List<Reserva> reservas =
                reservaRepo.findAllByActivaTrueOrderByLugarNumeroAsc();

        List<Integer> numeros = new ArrayList<>();

        for (Reserva reserva : reservas) {
            numeros.add(reserva.getLugar().getNumero());
        }

        return numeros;
    }

    @Transactional(readOnly = true)
    public Set<Integer> obtenerNumerosReservados() {

        List<Reserva> reservas = reservaRepo.findAllByActivaTrue();

        Set<Integer> numeros = new HashSet<>();

        for (Reserva reserva : reservas) {
            numeros.add(reserva.getLugar().getNumero());
        }

        return numeros;
    }

    private Lugar buscarLugarPorNumero(
            List<Lugar> lugares,
            int numeroLugar
    ) {
        for (Lugar lugar : lugares) {
            if (lugar.getNumero() == numeroLugar) {
                return lugar;
            }
        }

        throw new IllegalArgumentException(
                "No existe el lugar " + numeroLugar
        );
    }

    private void validarNumero(int numeroLugar) {
        if (numeroLugar < 1 || numeroLugar > 100) {
            throw new IllegalArgumentException(
                    "El número de lugar debe estar entre 1 y 100."
            );
        }
    }
}
