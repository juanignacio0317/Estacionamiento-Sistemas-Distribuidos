
package estacionamiento.controladores;

import estacionamiento.servicios.EstadoEstacionamientoServicio;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/cierre")
public class CierreEstacionamientoController {

    private final EstadoEstacionamientoServicio estadoServicio;

    public CierreEstacionamientoController(
            EstadoEstacionamientoServicio estadoServicio
    ) {
        this.estadoServicio = estadoServicio;
    }

    @GetMapping
    public ResponseEntity<Map<String, Boolean>> consultarEstado() {

        boolean abiertas =
                estadoServicio.estanEntradasAbiertas();

        return ResponseEntity.ok(
                Map.of("entradasAbiertas", abiertas)
        );
    }

    @PostMapping("/cerrar")
    public ResponseEntity<Map<String, Object>> cerrarEntradas() {

        boolean cambio = estadoServicio.cerrarEntradas();

        return ResponseEntity.ok(
                Map.of(
                        "entradasAbiertas", false,
                        "mensaje", cambio
                                ? "Las entradas fueron cerradas."
                                : "Las entradas ya estaban cerradas."
                )
        );
    }

    @PostMapping("/abrir")
    public ResponseEntity<Map<String, Object>> abrirEntradas() {

        boolean cambio = estadoServicio.abrirEntradas();

        return ResponseEntity.ok(
                Map.of(
                        "entradasAbiertas", true,
                        "mensaje", cambio
                                ? "Las entradas fueron abiertas."
                                : "Las entradas ya estaban abiertas."
                )
        );
    }
}

