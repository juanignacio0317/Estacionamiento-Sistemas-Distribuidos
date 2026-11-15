
package estacionamiento.servicios;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InicializadorEstadoEstacionamiento
        implements CommandLineRunner {

    private final EstadoEstacionamientoServicio estadoServicio;

    public InicializadorEstadoEstacionamiento(
            EstadoEstacionamientoServicio estadoServicio
    ) {
        this.estadoServicio = estadoServicio;
    }

    @Override
    public void run(String... args) {
        estadoServicio.inicializarEstado();
    }
}
