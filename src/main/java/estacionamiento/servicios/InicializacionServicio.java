package estacionamiento.servicios;

import estacionamiento.entidades.*;
import estacionamiento.estados.EstadoLugar;
import estacionamiento.estados.EstadoVehiculo;
import estacionamiento.repositorios.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
            VehiculoRepo vehiculoRepo
    ) {
        this.entradaRepo = entradaRepo;
        this.zonaRepo = zonaRepo;
        this.sectorRepo = sectorRepo;
        this.lugarRepo = lugarRepo;
        this.vehiculoRepo = vehiculoRepo;
    }

    @Transactional
    public void inicializar() {

        System.out.println("Verificando entradas, zonas y sectores...");

        crearZona("Zona 1", "Entrada 1", 1, 334);
        crearZona("Zona 2", "Entrada 2", 34, 333);
        crearZona("Zona 3", "Entrada 3", 67, 333);

        crearLugares();

        asignarVehiculosPendientes();

        System.out.println("Inicialización finalizada.");
    }


    private void crearZona(
            String nombreZona,
            String nombreEntrada,
            int posicionEntrada,
            int capacidad
    ) {

        Entrada entrada = entradaRepo.findByNombre(nombreEntrada).orElse(null);

        if (entrada == null) {
            entrada = new Entrada(nombreEntrada, posicionEntrada);
        }

        entrada.setPosicionRecorrido(posicionEntrada);
        entrada = entradaRepo.save(entrada);

        Zona zona = zonaRepo.findByNombre(nombreZona).orElse(null);

        if (zona == null) {
            zona = new Zona(nombreZona, capacidad, entrada);
        }

        zona.setEntrada(entrada);

        zonaRepo.save(zona);

        for (int numero = 1; numero <= 10; numero++) {

            Sector sector = sectorRepo
                    .findByZonaAndNumero(zona, numero)
                    .orElse(null);

            if (sector == null) {
                sector = new Sector(numero, zona);
            }

            int capacidadSector = numero == 10 ? 40 : 34;

            sector.setCapacidadMaxima(capacidadSector);

            sectorRepo.save(sector);
        }
    }


    private void crearLugares() {

        for (int numero = 1; numero <= 100; numero++) {

            if (!lugarRepo.existsByNumero(numero)) {
                lugarRepo.save(new Lugar(numero));
            }
        }
    }

    private void asignarVehiculosPendientes() {

        List<Vehiculo> todos = vehiculoRepo.findAll();
        List<Vehiculo> pendientes = new ArrayList<>();

        for (Vehiculo vehiculo : todos) {

            if (vehiculo.getSectorActual() == null &&
                    vehiculo.getEstado() == EstadoVehiculo.ESPERANDO) {

                pendientes.add(vehiculo);
            }
        }

        List<Zona> zonas = zonaRepo.findAllByOrderByIdAsc();

        int indiceVehiculo = 0;

        for (Zona zona : zonas) {

            long cantidadEnZona = 0;

            List<Sector> sectores =
                    sectorRepo.findByZonaOrderByNumeroAsc(zona);

            for (Sector sector : sectores) {
                cantidadEnZona +=
                        vehiculoRepo.countBySectorActual(sector);
            }

            int capacidadDisponibleZona =
                    (int) (zona.getCapacidadMaxima() - cantidadEnZona);

            // No se supera la capacidad total de la zona.
            if (capacidadDisponibleZona <= 0) {
                continue;
            }

            for (Sector sector : sectores) {

                long cantidadEnSector =
                        vehiculoRepo.countBySectorActual(sector);

                while (cantidadEnSector < sector.getCapacidadMaxima()
                        && capacidadDisponibleZona > 0
                        && indiceVehiculo < pendientes.size()) {

                    Vehiculo vehiculo = pendientes.get(indiceVehiculo);

                    vehiculo.setSectorActual(sector);
                    vehiculo.setEstado(EstadoVehiculo.ESPERANDO);

                    cantidadEnSector++;
                    capacidadDisponibleZona--;
                    indiceVehiculo++;
                }
            }
        }

        if (indiceVehiculo > 0) {
            vehiculoRepo.saveAll(
                    pendientes.subList(0, indiceVehiculo)
            );
        }

        if (indiceVehiculo < pendientes.size()) {
            System.out.println(
                    "ATENCIÓN: quedaron "
                            + (pendientes.size() - indiceVehiculo)
                            + " vehículos sin sector asignado."
            );
        }

        System.out.println(
                "Vehículos pendientes asignados: " + indiceVehiculo
        );
    }
}