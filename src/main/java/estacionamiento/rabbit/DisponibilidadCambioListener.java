
package estacionamiento.rabbit;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class DisponibilidadCambioListener {

    private final DisponibilidadProductor disponibilidadProductor;

    public DisponibilidadCambioListener(
            DisponibilidadProductor disponibilidadProductor
    ) {
        this.disponibilidadProductor = disponibilidadProductor;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void alCambiarDisponibilidad(DisponibilidadCambioEvent evento) {

        disponibilidadProductor.publicarDisponibilidadActual();
    }
}
