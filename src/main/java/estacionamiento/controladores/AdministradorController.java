
package estacionamiento.controladores;

import estacionamiento.dto.EstadoVehiculoDTO;
import estacionamiento.dto.EstadiaDTO;
import estacionamiento.dto.LugaresDisponiblesDTO;
import estacionamiento.servicios.ConsultasAdministradorServicio;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdministradorController {

    private final ConsultasAdministradorServicio consultasServicio;

    public AdministradorController(
            ConsultasAdministradorServicio consultasServicio
    ) {
        this.consultasServicio = consultasServicio;
    }

    // CONSULTA 1: ESTADO DE VEHÍCULO

    @GetMapping("/vehiculos/{matricula}")
    public ResponseEntity<?> consultarEstado(
            @PathVariable String matricula
    ) {

        Optional<EstadoVehiculoDTO> resultado =
                consultasServicio.consultarEstadoVehiculo(matricula);

        if (resultado.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Matrícula inválida");
        }

        return ResponseEntity.ok(resultado.get());
    }

    // CONSULTA 2: HISTORIAL DE ESTADÍAS

    @GetMapping("/vehiculos/{matricula}/estadias")
    public ResponseEntity<?> consultarHistorial(
            @PathVariable String matricula
    ) {

        Optional<List<EstadiaDTO>> resultado =
                consultasServicio.consultarHistorialEstadias(matricula);

        if (resultado.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Matrícula inválida");
        }

        return ResponseEntity.ok(resultado.get());
    }

    // CONSULTA 3: LUGARES DISPONIBLES

    @GetMapping("/lugares/disponibles")
    public ResponseEntity<LugaresDisponiblesDTO> consultarLugaresDisponibles() {

        LugaresDisponiblesDTO resultado =
                consultasServicio.consultarLugaresDisponibles();

        return ResponseEntity.ok(resultado);
    }
}
