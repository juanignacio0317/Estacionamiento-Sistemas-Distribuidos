
package estacionamiento.servicios;

import estacionamiento.entidades.*;
import estacionamiento.rabbit.MonitorEntradaServicio;
import estacionamiento.estados.EstadoVehiculo;
import estacionamiento.repositorios.VehiculoRepo;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;

@Service
public class IngresoServicio {

    private final EstacionamientoServicio estacionamientoServicio;
    private final ProgramadorSalidasServicio programadorSalidasServicio;
    private final VehiculoRepo vehiculoRepo;
    private final MonitorEntradaServicio monitorEntradaServicio;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(9);

    public IngresoServicio(
            EstacionamientoServicio estacionamientoServicio,
            ProgramadorSalidasServicio programadorSalidasServicio,
            VehiculoRepo vehiculoRepo,
            MonitorEntradaServicio monitorEntradaServicio
    ) {
        this.estacionamientoServicio = estacionamientoServicio;
        this.programadorSalidasServicio = programadorSalidasServicio;
        this.vehiculoRepo = vehiculoRepo;
        this.monitorEntradaServicio = monitorEntradaServicio;
    }

    public void procesarIngresos(
            Map<Zona, List<Vehiculo>> candidatosPorZona
    ) {

        List<CompletableFuture<Void>> tareas = new ArrayList<>();

        for (Map.Entry<Zona, List<Vehiculo>> elemento :
                candidatosPorZona.entrySet()) {

            Zona zona = elemento.getKey();

            // Cada entrada consulta su propio monitor.
            Long entradaId = zona.getEntrada().getId();

            long disponibilidad =
                    monitorEntradaServicio.obtenerDisponibilidad(entradaId);

            if (disponibilidad == 0) {
                continue;
            }

            List<Vehiculo> candidatos =
                    new ArrayList<>(elemento.getValue());

            Collections.shuffle(candidatos);

            int cantidadSeleccionada =
                    (int) Math.min(disponibilidad, candidatos.size());

            System.out.println(
                    "ENTRADA " + entradaId
                            + ": disponibilidad recibida = " + disponibilidad
                            + ", vehículos seleccionados = "
                            + cantidadSeleccionada
            );

            for (int i = 0; i < cantidadSeleccionada; i++) {

                Vehiculo vehiculo = candidatos.get(i);
                String matricula = vehiculo.getMatricula();

                CompletableFuture<Void> tarea =
                        CompletableFuture.runAsync(() -> {

                            try {
                                boolean ingresoExitoso =
                                        estacionamientoServicio.buscarLugar(
                                                matricula,
                                                entradaId
                                        );

                                if (ingresoExitoso) {
                                    programadorSalidasServicio
                                            .programarSalida(matricula);
                                }

                            } catch (Exception e) {
                                System.err.println(
                                        "Error al intentar ingresar el vehículo "
                                                + matricula + ": "
                                                + e.getMessage()
                                );
                            }

                        }, executor);

                tareas.add(tarea);
            }
        }

        CompletableFuture.allOf(
                tareas.toArray(new CompletableFuture[0])
        ).join();
    }

    @PreDestroy
    public void detener() {

        executor.shutdown();

        try {

            if (!executor.awaitTermination(20, TimeUnit.SECONDS)) {

                System.err.println(
                        "El ejecutor de ingresos no terminó a tiempo."
                );

                executor.shutdownNow();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }

        } catch (InterruptedException e) {

            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("========== EJECUTOR DE INGRESOS DETENIDO ==========");
    }
}
