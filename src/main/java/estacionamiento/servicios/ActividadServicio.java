
package estacionamiento.servicios;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ActividadServicio {

    private final List<String> actividades =
            new CopyOnWriteArrayList<>();

    private final DateTimeFormatter formatoHora =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public void registrar(String mensaje) {

        String hora = LocalTime.now().format(formatoHora);

        actividades.add("[" + hora + "] " + mensaje);
    }

    public List<String> obtenerActividades() {
        return List.copyOf(actividades);
    }

    public void limpiar() {
        actividades.clear();
    }
}

