package estacionamiento.servicios;

import estacionamiento.entidades.Vehiculo;
import estacionamiento.repositorios.VehiculoRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class VehiculoServicio {

    private final VehiculoRepo vehiculoRepo;
    private final Random random = new Random();

    public VehiculoServicio(VehiculoRepo vehiculoRepo) {
        this.vehiculoRepo = vehiculoRepo;
    }

    @Transactional
    public void completarVehiculos() {

        long cantidadActual = vehiculoRepo.count();

        if (cantidadActual >= 1000) {
            System.out.println("Ya existen 1000 vehículos registrados.");
            return;
        }

        Set<String> matriculasExistentes = new HashSet<>();

        for (Vehiculo vehiculo : vehiculoRepo.findAll()) {
            matriculasExistentes.add(vehiculo.getMatricula());
        }

        List<Vehiculo> nuevosVehiculos = new ArrayList<>();

        while (matriculasExistentes.size() + nuevosVehiculos.size() < 1000) {

            String matricula = generarMatricula();

            if (matriculasExistentes.add(matricula)) {
                nuevosVehiculos.add(new Vehiculo(matricula));
            }
        }

        vehiculoRepo.saveAll(nuevosVehiculos);

        System.out.println(
                "Se agregaron " + nuevosVehiculos.size() + " vehículos."
        );
    }

    private String generarMatricula() {

        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder matricula = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            matricula.append(letras.charAt(random.nextInt(26)));
        }

        for (int i = 0; i < 4; i++) {
            matricula.append(random.nextInt(10));
        }

        return matricula.toString();
    }
}
