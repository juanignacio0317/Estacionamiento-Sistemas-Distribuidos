

package estacionamiento.ui;

import estacionamiento.servicios.EstacionamientoServicio;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PanelEstacionamiento extends JPanel {

    private final EstacionamientoServicio estacionamientoServicio;

    private final Map<Integer, JLabel> indicadores = new HashMap<>();
    private final Map<Integer, JPanel> casillas = new HashMap<>();

    private final Timer actualizador;
    private boolean actualizando = false;

    public PanelEstacionamiento(
            EstacionamientoServicio estacionamientoServicio
    ) {
        this.estacionamientoServicio = estacionamientoServicio;

        setLayout(new GridLayout(10, 10, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(235, 238, 242));

        crearCasillas();

        actualizador = new Timer(1000, e -> actualizarLugares());
    }

    private void crearCasillas() {

        for (int numero = 1; numero <= 100; numero++) {

            JPanel casilla = new JPanel(new BorderLayout());

            casilla.setBackground(Color.WHITE);
            casilla.setBorder(new LineBorder(
                    new Color(180, 185, 190), 1
            ));

            JLabel etiquetaNumero = new JLabel(
                    String.valueOf(numero),
                    SwingConstants.CENTER
            );

            etiquetaNumero.setFont(
                    new Font("Arial", Font.PLAIN, 12)
            );

            JLabel indicador = new JLabel(
                    "",
                    SwingConstants.CENTER
            );

            indicador.setHorizontalAlignment(SwingConstants.CENTER);

            casilla.add(etiquetaNumero, BorderLayout.NORTH);
            casilla.add(indicador, BorderLayout.CENTER);

            indicadores.put(numero, indicador);
            casillas.put(numero, casilla);

            add(casilla);
        }
    }

    private void actualizarLugares() {

        if (actualizando) {
            return;
        }

        actualizando = true;

        SwingWorker<Map<Integer, String>, Void> tarea =
                new SwingWorker<>() {

                    @Override
                    protected Map<Integer, String> doInBackground() {

                        return estacionamientoServicio
                                .obtenerMatriculasPorLugar();
                    }

                    @Override
                    protected void done() {

                        try {

                            Map<Integer, String> matriculasPorLugar = get();

                            for (int numero = 1; numero <= 100; numero++) {

                                JLabel indicador = indicadores.get(numero);
                                JPanel casilla = casillas.get(numero);

                                String matricula =
                                        matriculasPorLugar.get(numero);

                                if (matricula != null) {

                                    indicador.setIcon(new IconoAuto());

                                    casilla.setToolTipText(
                                            "Matrícula: " + matricula
                                    );

                                } else {

                                    indicador.setIcon(null);

                                    casilla.setToolTipText(
                                            "Lugar " + numero + " - Libre"
                                    );
                                }
                            }

                        } catch (Exception e) {

                            System.err.println(
                                    "Error al actualizar los lugares: "
                                            + e.getMessage()
                            );

                        } finally {

                            actualizando = false;
                        }
                    }
                };

        tarea.execute();
    }

    public void iniciarActualizador() {
        actualizarLugares();
        actualizador.start();
    }

    public void detenerActualizador() {
        actualizador.stop();
    }
}
