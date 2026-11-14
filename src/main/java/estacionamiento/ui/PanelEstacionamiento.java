
package estacionamiento.ui;

import estacionamiento.entidades.Lugar;
import estacionamiento.estados.EstadoLugar;
import estacionamiento.servicios.EstacionamientoServicio;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
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
        actualizador.start();

        actualizarLugares();
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

                        return estacionamientoServicio.obtenerMatriculasPorLugar();
                    }

                    @Override
                    protected void done() {

                        try {

                            Map<Integer, String> matriculasPorLugar = get();

                            for (int numero = 1; numero <= 100; numero++) {

                                JLabel indicador = indicadores.get(numero);
                                JPanel casilla = casillas.get(numero);

                                String matricula = matriculasPorLugar.get(numero);

                                if (matricula != null) {

                                    indicador.setIcon(new AutoIcon());

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
    private static class AutoIcon implements Icon {

        @Override
        public int getIconWidth() {
            return 32;
        }

        @Override
        public int getIconHeight() {
            return 32;
        }

        @Override
        public void paintIcon(
                Component componente,
                Graphics g,
                int x,
                int y
        ) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Carrocería roja.
            g2.setColor(new Color(210, 35, 35));

            g2.fill(new RoundRectangle2D.Double(
                    x + 5, y + 9, 22, 16, 7, 7
            ));

            // Parte superior del auto.
            g2.fill(new RoundRectangle2D.Double(
                    x + 9, y + 5, 14, 13, 5, 5
            ));

            // Ventanas.
            g2.setColor(new Color(190, 225, 240));

            g2.fill(new RoundRectangle2D.Double(
                    x + 11, y + 7, 10, 8, 3, 3
            ));

            // Ruedas.
            g2.setColor(Color.DARK_GRAY);

            g2.fill(new Ellipse2D.Double(
                    x + 4, y + 11, 5, 8
            ));

            g2.fill(new Ellipse2D.Double(
                    x + 23, y + 11, 5, 8
            ));

            g2.fill(new Ellipse2D.Double(
                    x + 4, y + 20, 5, 8
            ));

            g2.fill(new Ellipse2D.Double(
                    x + 23, y + 20, 5, 8
            ));

            g2.dispose();
        }
    }
}
