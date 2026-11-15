
package estacionamiento.controladores;

import estacionamiento.dto.*;
import estacionamiento.servicios.AnaliticaServicio;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/admin/analitica")
public class AnaliticaController {

    private static final Logger logger =
            LoggerFactory.getLogger(AnaliticaController.class);

    private final AnaliticaServicio analiticaServicio;

    public AnaliticaController(AnaliticaServicio analiticaServicio) {
        this.analiticaServicio = analiticaServicio;
    }

    @GetMapping("/ingresos")
    public List<UsoEntradaDTO> obtenerIngresos(
            @RequestParam String periodo
    ) {
        return analiticaServicio.obtenerIngresos(periodo);
    }

    @GetMapping("/salidas")
    public List<UsoEntradaDTO> obtenerSalidas(
            @RequestParam String periodo
    ) {
        return analiticaServicio.obtenerSalidas(periodo);
    }

    @GetMapping("/vehiculos-diferentes")
    public VehiculosDiferentesDTO obtenerVehiculosDiferentes(
            @RequestParam String periodo
    ) {
        return analiticaServicio.obtenerVehiculosDiferentes(periodo);
    }

    @GetMapping("/top-estadias")
    public List<VehiculoTopEstadiasDTO> obtenerTopEstadias(
            @RequestParam String periodo
    ) {
        return analiticaServicio.obtenerTopEstadias(periodo);
    }

    @GetMapping("/tiempo-promedio")
    public TiempoPromedioDTO obtenerTiempoPromedio(
            @RequestParam String periodo
    ) {
        return analiticaServicio.obtenerTiempoPromedio(periodo);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarError(
            IllegalArgumentException ex
    ) {

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarErrorGeneral(Exception ex) {

        logger.error("ERROR AL EJECUTAR UNA CONSULTA DE ANALITICA:", ex);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Error interno al ejecutar la consulta de analítica.");
        respuesta.put("detalle", ex.getMessage() == null
                ? "Sin detalle"
                : ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(respuesta);
    }
}
