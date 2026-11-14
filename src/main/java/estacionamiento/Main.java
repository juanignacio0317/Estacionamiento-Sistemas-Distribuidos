package estacionamiento;

import estacionamiento.servicios.InicializacionServicio;
import estacionamiento.servicios.SimulacionServicio;
import estacionamiento.servicios.VehiculoServicio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import estacionamiento.rabbit.DisponibilidadProductor;
import estacionamiento.rabbit.MonitorEntradaServicio;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    @Bean
    public CommandLineRunner inicializar(
            VehiculoServicio vehiculoServicio,
            InicializacionServicio inicializacionServicio,
            SimulacionServicio simulacionServicio,
            DisponibilidadProductor disponibilidadProductor
    ) {
        return args -> {

            System.out.println("========== INICIO ==========");

            vehiculoServicio.completarVehiculos();

            inicializacionServicio.inicializar();

            System.out.println("========== INICIALIZACION COMPLETA ==========");

            // Publica la disponibilidad inicial para las tres entradas.
            disponibilidadProductor.publicarDisponibilidadActual();

            simulacionServicio.iniciar();

            System.out.println("========== SIMULACION INICIADA ==========");
        };
    }

}