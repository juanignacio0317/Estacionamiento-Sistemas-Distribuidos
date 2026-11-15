
package estacionamiento.servicios;

import estacionamiento.entidades.EstadoEstacionamiento;
import estacionamiento.repositorios.EstadoEstacionamientoRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstadoEstacionamientoServicio {

    private static final Long ID_ESTADO = 1L;

    private final EstadoEstacionamientoRepo estadoRepo;

    public EstadoEstacionamientoServicio(
            EstadoEstacionamientoRepo estadoRepo
    ) {
        this.estadoRepo = estadoRepo;
    }

    @Transactional
    public void inicializarEstado() {

        if (!estadoRepo.existsById(ID_ESTADO)) {
            estadoRepo.save(
                    new EstadoEstacionamiento(ID_ESTADO)
            );
        }
    }

    @Transactional(readOnly = true)
    public boolean estanEntradasAbiertas() {

        return estadoRepo.findById(ID_ESTADO)
                .map(EstadoEstacionamiento::isEntradasAbiertas)
                .orElse(true);
    }

    // Se llama desde la transacción de buscarLugar().
    // El bloqueo se mantiene hasta que termine esa transacción.
    @Transactional
    public boolean bloquearParaIngreso() {

        EstadoEstacionamiento estado =
                estadoRepo.bloquearParaLectura(ID_ESTADO)
                        .orElseThrow();

        return estado.isEntradasAbiertas();
    }

    @Transactional
    public boolean cerrarEntradas() {

        EstadoEstacionamiento estado =
                estadoRepo.bloquearParaEscritura(ID_ESTADO)
                        .orElseThrow();

        if (!estado.isEntradasAbiertas()) {
            return false;
        }

        estado.setEntradasAbiertas(false);
        estadoRepo.save(estado);

        return true;
    }

    @Transactional
    public boolean abrirEntradas() {

        EstadoEstacionamiento estado =
                estadoRepo.bloquearParaEscritura(ID_ESTADO)
                        .orElseThrow();

        if (estado.isEntradasAbiertas()) {
            return false;
        }

        estado.setEntradasAbiertas(true);
        estadoRepo.save(estado);

        return true;
    }
}
