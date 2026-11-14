
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

        // 1. Detener circulación y esperar el ciclo actual.
        scheduler.shutdown();
        esperarFinalizacion(scheduler, "circulación");

        // 2. Ya no hay ciclos que puedan enviar nuevos ingresos.
        ingresoServicio.detener();

        // 3. Esperar las salidas que ya estaban ejecutándose.
        programadorSalidasServicio.detener();

        System.out.println(
                "========== SIMULACIÓN DETENIDA =========="
        );
    }

    private void esperarFinalizacion(
            ExecutorService executor,
            String nombre
    ) {
        try {
            while (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println(
                        "Esperando que termine: " + nombre
                );
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Se interrumpió la espera de finalización de " + nombre,
                    e
            );
        }
    }

    public boolean estaEjecutando() {
        return ejecutando.get();
    }

    @PreDestroy
    public void cerrar() {
        detener();
    }
}
