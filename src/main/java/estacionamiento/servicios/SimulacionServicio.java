
package estacionamiento.servicios;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SimulacionServicio {

    private final CirculacionServicio circulacionServicio;
    private final ProgramadorSalidasServicio programadorSalidasServicio;
    private final IngresoServicio ingresoServicio;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private final AtomicBoolean ejecutando = new AtomicBoolean(false);
    private final AtomicBoolean cerrando = new AtomicBoolean(false);

    public SimulacionServicio(
            CirculacionServicio circulacionServicio,
            ProgramadorSalidasServicio programadorSalidasServicio,
            IngresoServicio ingresoServicio
    ) {
        this.circulacionServicio = circulacionServicio;
        this.programadorSalidasServicio = programadorSalidasServicio;
        this.ingresoServicio = ingresoServicio;
    }

    public synchronized void iniciar() {

        if (ejecutando.get() || cerrando.get()) {
            return;
        }

        programadorSalidasServicio.reprogramarSalidasPendientes();

        ejecutando.set(true);

        System.out.println("========== SIMULACIÓN INICIADA ==========");

        scheduler.scheduleWithFixedDelay(
                () -> {

                    if (!ejecutando.get()) {
                        return;
                    }

                    try {

                        circulacionServicio.avanzarVehiculos();

                    } catch (Exception e) {

                        if (ejecutando.get()) {
                            System.err.println(
                                    "Error en el ciclo de circulación: "
                                            + e.getMessage()
                            );
                        }
                    }

                },
                0,
                1,
                TimeUnit.SECONDS
        );
    }

    public synchronized void detener() {

        if (!cerrando.compareAndSet(false, true)) {
            return;
        }

        ejecutando.set(false);

        System.out.println(
                "========== INICIANDO CIERRE DE SIMULACIÓN =========="
        );

        // 1. Deja de iniciar ciclos nuevos y espera el ciclo actual.
        scheduler.shutdown();

        try {

            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {

                System.err.println(
                        "La circulación demoró demasiado en terminar."
                );

                scheduler.shutdownNow();
                scheduler.awaitTermination(5, TimeUnit.SECONDS);
            }

        } catch (InterruptedException e) {

            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // 2. Ya no hay ciclos enviando nuevos ingresos.
        ingresoServicio.detener();

        // 3. Detiene las salidas y espera las que estaban ejecutándose.
        programadorSalidasServicio.detener();

        System.out.println("========== SIMULACIÓN DETENIDA ==========");
    }

    public boolean estaEjecutando() {
        return ejecutando.get();
    }

    @PreDestroy
    public void cerrar() {
        detener();
    }
}
