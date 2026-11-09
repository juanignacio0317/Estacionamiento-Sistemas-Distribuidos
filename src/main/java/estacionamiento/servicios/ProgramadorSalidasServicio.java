package estacionamiento.servicios;

import estacionamiento.entidades.Estadia;
import estacionamiento.repositorios.EstadiaRepo;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Service
public class ProgramadorSalidasServicio {

    private final SalidaServicio salidaServicio;
    private final EstadiaRepo estadiaRepo;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(10);

    public ProgramadorSalidasServicio(
            SalidaServicio salidaServicio,
            EstadiaRepo estadiaRepo
    ) {
        this.salidaServicio = salidaServicio;
        this.estadiaRepo = estadiaRepo;
    }

    public void programarSalida(String matricula) {

        int segundos = ThreadLocalRandom.current().nextInt(30, 61);

        scheduler.schedule(
                () -> intentarSalida(matricula),
                segundos,
                TimeUnit.SECONDS
        );
    }

    private void intentarSalida(String matricula) {

        try {
            boolean finalizada =
                    salidaServicio.finalizarEstadia(matricula);

            if (!finalizada) {
                scheduler.schedule(
                        () -> intentarSalida(matricula),
                        1,
                        TimeUnit.SECONDS
                );
            }

        } catch (Exception e) {
            System.err.println(
                    "Error al procesar la salida de "
                            + matricula + ": " + e.getMessage()
            );

            scheduler.schedule(
                    () -> intentarSalida(matricula),
                    1,
                    TimeUnit.SECONDS
            );
        }
    }

    public void reprogramarSalidasPendientes() {

        List<Estadia> estadias =
                estadiaRepo.findAllByFechaSalidaIsNull();

        for (Estadia estadia : estadias) {
            programarSalida(estadia.getVehiculo().getMatricula());
        }

        System.out.println(
                "Salidas pendientes reprogramadas: " + estadias.size()
        );
    }

    @PreDestroy
    public void detener() {
        scheduler.shutdown();
    }
}