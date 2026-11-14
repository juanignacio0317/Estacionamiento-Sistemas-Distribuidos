
package estacionamiento.rabbit;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class MonitorEntradaServicio {

    private final ConcurrentHashMap<Long, Long> disponibilidadPorEntrada =
            new ConcurrentHashMap<>();

    public void actualizarDisponibilidad(Long entradaId, long lugaresDisponibles) {

        disponibilidadPorEntrada.put(entradaId, lugaresDisponibles);

        System.out.println(
                "MONITOR ENTRADA " + entradaId
                        + ": disponibilidad actualizada a "
                        + lugaresDisponibles
        );
    }

    public long obtenerDisponibilidad(Long entradaId) {

        return disponibilidadPorEntrada.getOrDefault(entradaId, 0L);
    }
}
