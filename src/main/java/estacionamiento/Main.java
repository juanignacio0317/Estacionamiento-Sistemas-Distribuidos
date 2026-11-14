
package estacionamiento;

import estacionamiento.servicios.InicializacionServicio;
import estacionamiento.servicios.VehiculoServicio;
import estacionamiento.rabbit.DisponibilidadProductor;
import estacionamiento.ui.VentanaPrincipal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.swing.SwingUtilities;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        SpringApplication aplicacion =
                new SpringApplication(Main.class);

        aplicacion.setHeadless(false);

        aplicacion.run(args);
    }

    @Bean
    public CommandLineRunner inicializar(
            VehiculoServicio vehiculoServicio,
            InicializacionServicio inicializacionServicio,
            DisponibilidadProductor disponibilidadProductor,
            VentanaPrincipal ventanaPrincipal
    ) {
        return args -> {

            System.out.println("========== INICIO ==========");

            vehiculoServicio.completarVehiculos();

            inicializacionServicio.inicializar();

            System.out.println(
                    "========== INICIALIZACION COMPLETA =========="
            );

            // Publica la disponibilidad inicial para las tres entradas.
            disponibilidadProductor.publicarDisponibilidadActual();

            // Abre la interfaz Swing.
            SwingUtilities.invokeLater(ventanaPrincipal::mostrar);

        };
    }
}
