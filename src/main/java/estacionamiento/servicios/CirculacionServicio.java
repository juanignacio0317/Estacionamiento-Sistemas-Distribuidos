package estacionamiento.servicios;

import estacionamiento.entidades.Entrada;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Service
public class CirculacionServicio {

    private final ConcurrentHashMap<Long, Semaphore> permisos =
            new ConcurrentHashMap<>();

    private Semaphore obtenerSemaforo(Entrada entrada) {

        return permisos.computeIfAbsent(
                entrada.getId(),
                id -> new Semaphore(3, false)
        );
    }

    public boolean intentarIngresar(Entrada entrada) {

        return obtenerSemaforo(entrada).tryAcquire();
    }

    public void finalizarIngreso(Entrada entrada) {

        obtenerSemaforo(entrada).release();
    }
}