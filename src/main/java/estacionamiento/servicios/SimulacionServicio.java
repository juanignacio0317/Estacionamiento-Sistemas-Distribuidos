package estacionamiento.servicios;

import estacionamiento.entidades.Vehiculo;

import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.Random;

@Service
public class SimulacionServicio {

    private final EstacionamientoServicio estacionamientoServicio;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(10);

    private final Random random = new Random();

    public SimulacionServicio(
            EstacionamientoServicio estacionamientoServicio) {

        this.estacionamientoServicio = estacionamientoServicio;
    }

    public void programarSalida(Vehiculo vehiculo) {

        // Tiempo aleatorio entre 30 y 120 segundos
        int segundos = random.nextInt(91) + 30;

        scheduler.schedule(
                () -> estacionamientoServicio.liberarLugar(vehiculo),
                segundos,
                TimeUnit.SECONDS
        );
    }
}