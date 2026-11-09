package estacionamiento;

import estacionamiento.servicios.InicializacionServicio;
import estacionamiento.servicios.SimulacionServicio;
import estacionamiento.servicios.VehiculoServicio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner inicializar(
            VehiculoServicio vehiculoServicio,
            InicializacionServicio inicializacionServicio,
            SimulacionServicio simulacionServicio) {

        return args -> {
            System.out.println("========== INICIO ==========");

            vehiculoServicio.completarVehiculos();
            inicializacionServicio.inicializar();

            System.out.println("========== INICIALIZACION COMPLETA ==========");

            simulacionServicio.iniciar();

            System.out.println("========== SIMULACION INICIADA ==========");
        };
    }
}