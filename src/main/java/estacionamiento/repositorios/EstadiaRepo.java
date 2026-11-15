
package estacionamiento.repositorios;

import estacionamiento.entidades.Estadia;
import estacionamiento.entidades.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EstadiaRepo extends JpaRepository<Estadia, Long> {

    Optional<Estadia> findFirstByVehiculoAndFechaSalidaIsNullOrderByFechaEntradaDesc(
            Vehiculo vehiculo
    );

    List<Estadia> findAllByFechaSalidaIsNull();

    @Query("""
        SELECT e.lugar.numero, e.vehiculo.matricula
        FROM Estadia e
        WHERE e.fechaSalida IS NULL
    """)
    List<Object[]> buscarLugaresOcupadosConMatricula();

    List<Estadia> findAllByVehiculo_MatriculaOrderByFechaEntradaDesc(
            String matricula
    );


    // INGRESOS POR ENTRADA - HISTORICO
    @Query("""
    SELECT en.nombre, COUNT(e.id)
    FROM Entrada en
    LEFT JOIN Estadia e ON e.entradaIngreso = en
    GROUP BY en.id, en.nombre
    ORDER BY en.id
""")
    List<Object[]> contarIngresosPorEntradaHistorico();

    // INGRESOS POR ENTRADA - DESDE UNA FECHA
    @Query("""
    SELECT en.nombre, COUNT(e.id)
    FROM Entrada en
    LEFT JOIN Estadia e ON e.entradaIngreso = en
        AND e.fechaEntrada >= :desde
    GROUP BY en.id, en.nombre
    ORDER BY en.id
""")
    List<Object[]> contarIngresosPorEntradaDesde(
            @Param("desde") LocalDateTime desde
    );

    // SALIDAS POR ENTRADA - HISTORICO
    @Query("""
    SELECT en.nombre, COUNT(e.id)
    FROM Entrada en
    LEFT JOIN Estadia e ON e.entradaSalida = en
    GROUP BY en.id, en.nombre
    ORDER BY en.id
""")
    List<Object[]> contarSalidasPorEntradaHistorico();

    // SALIDAS POR ENTRADA - DESDE UNA FECHA
    @Query("""
    SELECT en.nombre, COUNT(e.id)
    FROM Entrada en
    LEFT JOIN Estadia e ON e.entradaSalida = en
        AND e.fechaSalida >= :desde
    GROUP BY en.id, en.nombre
    ORDER BY en.id
""")
    List<Object[]> contarSalidasPorEntradaDesde(
            @Param("desde") LocalDateTime desde
    );

    // VEHICULOS DIFERENTES - HISTORICO
    @Query("""
    SELECT COUNT(DISTINCT e.vehiculo.matricula)
    FROM Estadia e
""")
    Long contarVehiculosDiferentesHistorico();

    // VEHICULOS DIFERENTES - DESDE UNA FECHA
    @Query("""
    SELECT COUNT(DISTINCT e.vehiculo.matricula)
    FROM Estadia e
    WHERE e.fechaEntrada >= :desde
""")
    Long contarVehiculosDiferentesDesde(
            @Param("desde") LocalDateTime desde
    );

    // TOP VEHICULOS - HISTORICO
    @Query("""
    SELECT e.vehiculo.matricula, COUNT(e.id)
    FROM Estadia e
    GROUP BY e.vehiculo.matricula
    ORDER BY COUNT(e.id) DESC
""")
    List<Object[]> buscarTopVehiculosConMasEstadiasHistorico();

    // TOP VEHICULOS - DESDE UNA FECHA
    @Query("""
    SELECT e.vehiculo.matricula, COUNT(e.id)
    FROM Estadia e
    WHERE e.fechaEntrada >= :desde
    GROUP BY e.vehiculo.matricula
    ORDER BY COUNT(e.id) DESC
""")
    List<Object[]> buscarTopVehiculosConMasEstadiasDesde(
            @Param("desde") LocalDateTime desde
    );

    // DURACIONES - HISTORICO
    @Query("""
    SELECT e.fechaEntrada, e.fechaSalida
    FROM Estadia e
    WHERE e.fechaSalida IS NOT NULL
""")
    List<Object[]> buscarDuracionesEstadiasHistorico();

    // DURACIONES - DESDE UNA FECHA
    @Query("""
    SELECT e.fechaEntrada, e.fechaSalida
    FROM Estadia e
    WHERE e.fechaSalida IS NOT NULL
      AND e.fechaEntrada >= :desde
""")
    List<Object[]> buscarDuracionesEstadiasDesde(
            @Param("desde") LocalDateTime desde
    );

}
