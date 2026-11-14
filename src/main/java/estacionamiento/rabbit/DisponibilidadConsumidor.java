
package estacionamiento.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DisponibilidadConsumidor {

    private final MonitorEntradaServicio monitorEntradaServicio;

    public DisponibilidadConsumidor(
            MonitorEntradaServicio monitorEntradaServicio
    ) {
        this.monitorEntradaServicio = monitorEntradaServicio;
    }

    @RabbitListener(queues = RabbitConfig.COLA_ENTRADA_1)
    public void recibirEntrada1(DisponibilidadMensaje mensaje) {

        System.out.println(
                "CONSUMIDOR 1: recibio "
                        + mensaje.getLugaresDisponibles()
                        + " lugares libres"
        );

        monitorEntradaServicio.actualizarDisponibilidad(
                7L,
                mensaje.getLugaresDisponibles()
        );
    }

    @RabbitListener(queues = RabbitConfig.COLA_ENTRADA_2)
    public void recibirEntrada2(DisponibilidadMensaje mensaje) {

        System.out.println(
                "CONSUMIDOR 2: recibio "
                        + mensaje.getLugaresDisponibles()
                        + " lugares libres"
        );

        monitorEntradaServicio.actualizarDisponibilidad(
                8L,
                mensaje.getLugaresDisponibles()
        );
    }

    @RabbitListener(queues = RabbitConfig.COLA_ENTRADA_3)
    public void recibirEntrada3(DisponibilidadMensaje mensaje) {

        System.out.println(
                "CONSUMIDOR 3: recibio "
                        + mensaje.getLugaresDisponibles()
                        + " lugares libres"
        );

        monitorEntradaServicio.actualizarDisponibilidad(
                9L,
                mensaje.getLugaresDisponibles()
        );
    }
}
