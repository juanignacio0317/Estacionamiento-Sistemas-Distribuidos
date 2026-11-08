package estacionamiento;

import estacionamiento.servicios.VehiculoServicio;
import estacionamiento.servicios.InicializacionServicio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner inicializar(
            VehiculoServicio vehiculoServicio,
            InicializacionServicio inicializacionServicio) {

        return args -> {

            System.out.println("========== INICIO ==========");

            vehiculoServicio.completarVehiculos();

            System.out.println("Iniciando configuración del estacionamiento...");

            inicializacionServicio.inicializar();

            System.out.println("========== FIN ==========");
        };
    }
}