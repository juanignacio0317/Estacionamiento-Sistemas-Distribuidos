package estacionamiento.servicios;

import estacionamiento.entidades.*;
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

    private final ExecutorService executor =
            Executors.newFixedThreadPool(9);

    public IngresoServicio(
            EstacionamientoServicio estacionamientoServicio,
            ProgramadorSalidasServicio programadorSalidasServicio,
            VehiculoRepo vehiculoRepo
    ) {
        this.estacionamientoServicio = estacionamientoServicio;
        this.programadorSalidasServicio = programadorSalidasServicio;
        this.vehiculoRepo = vehiculoRepo;
    }

    public void procesarIngresos(Map<Zona, List<Vehiculo>> candidatosPorZona) {

        long lugaresLibres =
                estacionamientoServicio.cantidadLugaresLibres();

        if (lugaresLibres == 0) {
            return;
        }

        int cantidadPorEntrada = (int) Math.min(3, lugaresLibres);

        List<CompletableFuture<Void>> tareas = new ArrayList<>();

        for (Map.Entry<Zona, List<Vehiculo>> elemento :
                candidatosPorZona.entrySet()) {

            Zona zona = elemento.getKey();
            List<Vehiculo> candidatos =
                    new ArrayList<>(elemento.getValue());

            // Se mezclan para no establecer una prioridad FIFO.
            Collections.shuffle(candidatos);

            int cantidadSeleccionada =
                    Math.min(cantidadPorEntrada, candidatos.size());

            for (int i = 0; i < cantidadSeleccionada; i++) {

                Vehiculo vehiculo = candidatos.get(i);

                String matricula = vehiculo.getMatricula();
                Long entradaId = zona.getEntrada().getId();

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

        // Esperamos a que terminen los intentos de las tres entradas.
        CompletableFuture.allOf(
                tareas.toArray(new CompletableFuture[0])
        ).join();
    }

    @PreDestroy
    public void detener() {
        executor.shutdown();
    }
}