
package estacionamiento.servicios;

import estacionamiento.entidades.Estadia;
import estacionamiento.repositorios.EstadiaRepo;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ProgramadorSalidasServicio {

    private final SalidaServicio salidaServicio;
    private final EstadiaRepo estadiaRepo;

    private final ScheduledThreadPoolExecutor scheduler =
            new ScheduledThreadPoolExecutor(10);

    private final AtomicBoolean cerrando = new AtomicBoolean(false);

    public ProgramadorSalidasServicio(
            SalidaServicio salidaServicio,
            EstadiaRepo estadiaRepo
    ) {
        this.salidaServicio = salidaServicio;
        this.estadiaRepo = estadiaRepo;

        // No ejecutar tareas que todavía estaban pendientes al cerrar.
        scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        scheduler.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        scheduler.setRemoveOnCancelPolicy(true);
    }

    public void programarSalida(String matricula) {

        if (cerrando.get()) {
            return;
        }

        int segundos = ThreadLocalRandom.current().nextInt(30, 61);

        try {
            scheduler.schedule(
                    () -> intentarSalida(matricula),
                    segundos,
                    TimeUnit.SECONDS
            );
        } catch (RejectedExecutionException e) {
            if (!cerrando.get()) {
                System.err.println(
                        "No se pudo programar la salida de "
                                + matricula + ": " + e.getMessage()
                );
            }
        }
    }

    private void intentarSalida(String matricula) {

        if (cerrando.get()) {
            return;
        }

        try {

            boolean finalizada =
                    salidaServicio.finalizarEstadia(matricula);

            if (!finalizada) {
                programarReintento(matricula);
            }

        } catch (Exception e) {

            if (!cerrando.get()) {
                System.err.println(
                        "Error al procesar la salida de "
                                + matricula + ": " + e.getMessage()
                );

                programarReintento(matricula);
            }
        }
    }

    private void programarReintento(String matricula) {

        if (cerrando.get()) {
            return;
        }

        try {
            scheduler.schedule(
                    () -> intentarSalida(matricula),
                    1,
                    TimeUnit.SECONDS
            );
        } catch (RejectedExecutionException e) {
            if (!cerrando.get()) {
                System.err.println(
                        "No se pudo programar el reintento de "
                                + matricula
                );
            }
        }
    }

    public void reprogramarSalidasPendientes() {

        if (cerrando.get()) {
            return;
        }

        List<Estadia> estadias =
                estadiaRepo.findAllByFechaSalidaIsNull();

        for (Estadia estadia : estadias) {

            if (cerrando.get()) {
                break;
            }

            programarSalida(
                    estadia.getVehiculo().getMatricula()
            );
        }

        System.out.println(
                "Salidas pendientes reprogramadas: " + estadias.size()
        );
    }

    public void detener() {

        if (!cerrando.compareAndSet(false, true)) {
            return;
        }

        // Deja terminar las tareas activas, pero cancela las demoradas.
        scheduler.shutdown();

        try {

            if (!scheduler.awaitTermination(20, TimeUnit.SECONDS)) {

                System.err.println(
                        "El programador de salidas no terminó a tiempo."
                );

                scheduler.shutdownNow();
                scheduler.awaitTermination(5, TimeUnit.SECONDS);
            }

        } catch (InterruptedException e) {

            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("========== PROGRAMADOR DE SALIDAS DETENIDO ==========");
    }

    @PreDestroy
    public void cerrar() {
        detener();
    }
}
