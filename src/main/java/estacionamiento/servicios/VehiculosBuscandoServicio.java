
package estacionamiento.servicios;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VehiculosBuscandoServicio {

    private final Set<String> matriculasBuscando =
            ConcurrentHashMap.newKeySet();

    public void iniciarBusqueda(String matricula) {
        matriculasBuscando.add(matricula);
    }

    public void finalizarBusqueda(String matricula) {
        matriculasBuscando.remove(matricula);
    }

    public Set<String> obtenerVehiculosBuscando() {
        return Set.copyOf(matriculasBuscando);
    }

    public void limpiar() {
        matriculasBuscando.clear();
    }
}
