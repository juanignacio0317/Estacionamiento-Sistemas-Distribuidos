
package estacionamiento.controladores;

import estacionamiento.servicios.ReservaServicio;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservas")
public class ReservaController {

    private final ReservaServicio reservaServicio;

    public ReservaController(ReservaServicio reservaServicio) {
        this.reservaServicio = reservaServicio;
    }

    @GetMapping
    public ResponseEntity<List<Integer>> obtenerReservas() {

        return ResponseEntity.ok(
                reservaServicio.obtenerLugaresReservados()
        );
    }

    @PostMapping("/{numeroLugar}")
    public ResponseEntity<String> reservar(
            @PathVariable int numeroLugar
    ) {
        try {
            reservaServicio.reservar(numeroLugar);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Lugar " + numeroLugar + " reservado.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{numeroLugar}")
    public ResponseEntity<String> cancelar(
            @PathVariable int numeroLugar
    ) {
        try {
            boolean cancelada =
                    reservaServicio.cancelarReserva(numeroLugar);

            if (!cancelada) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("El lugar no tiene una reserva activa.");
            }

            return ResponseEntity.ok(
                    "Reserva de lugar " + numeroLugar + " cancelada."
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

