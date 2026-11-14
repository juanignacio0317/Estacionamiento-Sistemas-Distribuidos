
package estacionamiento.rabbit;

import estacionamiento.servicios.EstacionamientoServicio;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class DisponibilidadProductor {

    private final RabbitTemplate rabbitTemplate;
    private final EstacionamientoServicio estacionamientoServicio;

    public DisponibilidadProductor(
            RabbitTemplate rabbitTemplate,
            EstacionamientoServicio estacionamientoServicio
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.estacionamientoServicio = estacionamientoServicio;
    }

    public void publicarDisponibilidadActual() {

        long lugaresLibres =
                estacionamientoServicio.cantidadLugaresLibres();

        publicarDisponibilidad(lugaresLibres);
    }

    public void publicarDisponibilidad(long lugaresLibres) {

        DisponibilidadMensaje mensaje =
                new DisponibilidadMensaje(lugaresLibres);

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_DISPONIBILIDAD,
                "",
                mensaje
        );

        System.out.println(
                "PRODUCTOR: disponibilidad publicada: "
                        + lugaresLibres + " lugares libres"
        );
    }
}

