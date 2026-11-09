package estacionamiento.servicios;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SimulacionServicio {

    private final CirculacionServicio circulacionServicio;
    private final ProgramadorSalidasServicio programadorSalidasServicio;

    private ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private final AtomicBoolean ejecutando = new AtomicBoolean(false);

    public SimulacionServicio(
            CirculacionServicio circulacionServicio,
            ProgramadorSalidasServicio programadorSalidasServicio
    ) {
        this.circulacionServicio = circulacionServicio;
        this.programadorSalidasServicio = programadorSalidasServicio;
    }

    public synchronized void iniciar() {

        if (ejecutando.get()) {
            return;
        }

        if (scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        programadorSalidasServicio.reprogramarSalidasPendientes();

        ejecutando.set(true);

        System.out.println("========== SIMULACIÓN INICIADA ==========");

        scheduler.scheduleWithFixedDelay(
                () -> {
                    try {
                        circulacionServicio.avanzarVehiculos();
                    } catch (Exception e) {
                        System.err.println(
                                "Error en el ciclo de circulación: "
                                        + e.getMessage()
                        );
                    }
                },
                0,
                1,
                TimeUnit.SECONDS
        );
    }

    public synchronized void detener() {

        if (!ejecutando.get()) {
            return;
        }

        ejecutando.set(false);
        scheduler.shutdownNow();

        System.out.println("========== SIMULACIÓN DETENIDA ==========");
    }

    public boolean estaEjecutando() {
        return ejecutando.get();
    }

    @PreDestroy
    public void cerrar() {
        scheduler.shutdownNow();
    }
}