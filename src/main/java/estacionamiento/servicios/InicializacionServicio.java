package estacionamiento.servicios;

import estacionamiento.entidades.*;
import estacionamiento.estados.EstadoVehiculo;
import estacionamiento.repositorios.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class InicializacionServicio {

    private final EntradaRepo entradaRepo;
    private final ZonaRepo zonaRepo;
    private final SectorRepo sectorRepo;
    private final LugarRepo lugarRepo;
    private final VehiculoRepo vehiculoRepo;

    public InicializacionServicio(
            EntradaRepo entradaRepo,
            ZonaRepo zonaRepo,
            SectorRepo sectorRepo,
            LugarRepo lugarRepo,
            VehiculoRepo vehiculoRepo) {

        this.entradaRepo = entradaRepo;
        this.zonaRepo = zonaRepo;
        this.sectorRepo = sectorRepo;
        this.lugarRepo = lugarRepo;
        this.vehiculoRepo = vehiculoRepo;
    }

    @Transactional
    public void inicializar() {

        System.out.println("CREANDO ZONAS Y ENTRADAS");

        crearZona("Zona 1", "Entrada 1", 334);
        crearZona("Zona 2", "Entrada 2", 333);
        crearZona("Zona 3", "Entrada 3", 333);

        System.out.println("Entradas: " + entradaRepo.count());
        System.out.println("Zonas: " + zonaRepo.count());
        System.out.println("Sectores: " + sectorRepo.count());

        crearLugares();

        System.out.println("Lugares: " + lugarRepo.count());

        asignarVehiculos();

        System.out.println("Vehículos: " + vehiculoRepo.count());
    }
    private void crearZona(
            String nombreZona,
            String nombreEntrada,
            int capacidad) {

        Entrada entrada = entradaRepo.findByNombre(nombreEntrada)
                .orElseGet(() ->
                        entradaRepo.save(new Entrada(nombreEntrada)));

        Zona zona = zonaRepo.findByNombre(nombreZona)
                .orElseGet(() ->
                        zonaRepo.save(
                                new Zona(nombreZona, capacidad, entrada)));

        for (int i = 1; i <= 10; i++) {
            final int numero = i;

            sectorRepo.findByZonaAndNumero(zona, numero)
                    .orElseGet(() ->
                            sectorRepo.save(new Sector(numero, zona)));
        }
    }

    private void crearLugares() {

        for (int i = 1; i <= 100; i++) {
            if (!lugarRepo.existsByNumero(i)) {
                lugarRepo.save(new Lugar(i));
            }
        }
    }

    private void asignarVehiculos() {

        List<Vehiculo> vehiculos = vehiculoRepo.findAll(
                org.springframework.data.domain.Sort.by("matricula")
        );

        List<Zona> zonas = zonaRepo.findAll();
        zonas.sort(Comparator.comparing(Zona::getNombre));

        if (vehiculos.size() != 1000) {
            throw new IllegalStateException(
                    "Se esperaban 1000 vehículos, pero existen "
                            + vehiculos.size()
            );
        }

        int indiceVehiculo = 0;

        for (Zona zona : zonas) {

            List<Sector> sectores =
                    sectorRepo.findByZonaOrderByNumeroAsc(zona);

            int cantidadVehiculosZona = zona.getCapacidadMaxima();

            int cantidadBase = cantidadVehiculosZona / 10;
            int resto = cantidadVehiculosZona % 10;

            for (int i = 0; i < sectores.size(); i++) {

                Sector sector = sectores.get(i);

                // Distribución equilibrada: 33 o 34 vehículos.
                int cantidadSector =
                        cantidadBase + (i < resto ? 1 : 0);

                for (int j = 0; j < cantidadSector; j++) {

                    Vehiculo vehiculo = vehiculos.get(indiceVehiculo);

                    vehiculo.setSectorActual(sector);
                    vehiculo.setEstado(EstadoVehiculo.ESPERANDO);

                    indiceVehiculo++;
                }
            }
        }

        vehiculoRepo.saveAll(vehiculos);

        System.out.println(
                "Vehículos distribuidos: " + indiceVehiculo
        );
    }
}