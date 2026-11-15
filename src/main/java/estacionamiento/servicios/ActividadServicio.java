
package estacionamiento.servicios;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

    private final Path archivo = Path.of("logs", "eventos.txt");

    public void registrar(String mensaje) {

        String hora = LocalTime.now().format(formatoHora);
        String actividad = "[" + hora + "] " + mensaje;

        actividades.add(actividad);

        try {
            Files.createDirectories(archivo.getParent());

            Files.writeString(
                    archivo,
                    actividad + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            System.err.println(
                    "Error al guardar el evento en el archivo: "
                            + e.getMessage()
            );
        }
    }

    public List<String> obtenerActividades() {
        return List.copyOf(actividades);
    }

    public void limpiar() {
        actividades.clear();
    }
}

