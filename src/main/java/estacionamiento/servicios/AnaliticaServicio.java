
package estacionamiento.servicios;

import estacionamiento.dto.*;
import estacionamiento.repositorios.EstadiaRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnaliticaServicio {

    private final EstadiaRepo estadiaRepo;

    public AnaliticaServicio(EstadiaRepo estadiaRepo) {
        this.estadiaRepo = estadiaRepo;
    }

    // INGRESOS POR ENTRADA

    public List<UsoEntradaDTO> obtenerIngresos(String periodo) {

        LocalDateTime desde = calcularFechaDesde(periodo, true);

        List<Object[]> resultados;

        if (desde == null) {
            resultados = estadiaRepo.contarIngresosPorEntradaHistorico();
        } else {
            resultados = estadiaRepo.contarIngresosPorEntradaDesde(desde);
        }

        List<UsoEntradaDTO> respuesta = new ArrayList<>();

        for (Object[] fila : resultados) {

            respuesta.add(
                    new UsoEntradaDTO(
                            (String) fila[0],
                            ((Number) fila[1]).longValue()
                    )
            );
        }

        return respuesta;
    }

    // SALIDAS POR ENTRADA

    public List<UsoEntradaDTO> obtenerSalidas(String periodo) {

        LocalDateTime desde = calcularFechaDesde(periodo, true);

        List<Object[]> resultados;

        if (desde == null) {
            resultados = estadiaRepo.contarSalidasPorEntradaHistorico();
        } else {
            resultados = estadiaRepo.contarSalidasPorEntradaDesde(desde);
        }

        List<UsoEntradaDTO> respuesta = new ArrayList<>();

        for (Object[] fila : resultados) {

            respuesta.add(
                    new UsoEntradaDTO(
                            (String) fila[0],
                            ((Number) fila[1]).longValue()
                    )
            );
        }

        return respuesta;
    }

    // VEHÍCULOS DIFERENTES

    public VehiculosDiferentesDTO obtenerVehiculosDiferentes(
            String periodo
    ) {

        LocalDateTime desde = calcularFechaDesde(periodo, true);

        Long cantidad;

        if (desde == null) {
            cantidad = estadiaRepo.contarVehiculosDiferentesHistorico();
        } else {
            cantidad = estadiaRepo.contarVehiculosDiferentesDesde(desde);
        }

        return new VehiculosDiferentesDTO(
                periodo,
                cantidad == null ? 0 : cantidad
        );
    }

    // TOP 10 DE VEHÍCULOS

    public List<VehiculoTopEstadiasDTO> obtenerTopEstadias(
            String periodo
    ) {

        LocalDateTime desde = calcularFechaDesde(periodo, false);

        List<Object[]> resultados;

        if (desde == null) {
            resultados = estadiaRepo.buscarTopVehiculosConMasEstadiasHistorico();
        } else {
            resultados = estadiaRepo.buscarTopVehiculosConMasEstadiasDesde(desde);
        }

        List<VehiculoTopEstadiasDTO> respuesta = new ArrayList<>();

        int limite = Math.min(10, resultados.size());

        for (int i = 0; i < limite; i++) {

            Object[] fila = resultados.get(i);

            respuesta.add(
                    new VehiculoTopEstadiasDTO(
                            (String) fila[0],
                            ((Number) fila[1]).longValue()
                    )
            );
        }

        return respuesta;
    }

    // TIEMPO PROMEDIO DE ESTADÍA

    public TiempoPromedioDTO obtenerTiempoPromedio(String periodo) {

        LocalDateTime desde = calcularFechaDesde(periodo, false);

        List<Object[]> resultados;

        if (desde == null) {
            resultados = estadiaRepo.buscarDuracionesEstadiasHistorico();
        } else {
            resultados = estadiaRepo.buscarDuracionesEstadiasDesde(desde);
        }

        long cantidad = 0;
        long segundosTotales = 0;

        for (Object[] fila : resultados) {

            LocalDateTime entrada = (LocalDateTime) fila[0];
            LocalDateTime salida = (LocalDateTime) fila[1];

            long segundos = Duration.between(
                    entrada,
                    salida
            ).getSeconds();

            if (segundos >= 0) {
                segundosTotales += segundos;
                cantidad++;
            }
        }

        double promedioMinutos = 0;

        if (cantidad > 0) {

            promedioMinutos =
                    (double) segundosTotales / cantidad / 60.0;

            promedioMinutos =
                    Math.round(promedioMinutos * 100.0) / 100.0;
        }

        return new TiempoPromedioDTO(
                periodo,
                cantidad,
                promedioMinutos
        );
    }

    // CÁLCULO DE PERÍODOS

    private LocalDateTime calcularFechaDesde(
            String periodo,
            boolean permiteCincoMinutos
    ) {

        if (periodo == null) {
            throw new IllegalArgumentException(
                    "Debe especificar un período."
            );
        }

        switch (periodo.toUpperCase()) {

            case "HISTORICO":
                return null;

            case "24_HORAS":
                return LocalDateTime.now().minusHours(24);

            case "5_MINUTOS":

                if (!permiteCincoMinutos) {
                    throw new IllegalArgumentException(
                            "Esta consulta no admite el período de 5 minutos."
                    );
                }

                return LocalDateTime.now().minusMinutes(5);

            default:
                throw new IllegalArgumentException(
                        "Período inválido: " + periodo
                );
        }
    }
}

