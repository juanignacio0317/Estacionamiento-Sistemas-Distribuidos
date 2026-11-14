

package estacionamiento.ui;

import estacionamiento.rabbit.MonitorEntradaServicio;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class PanelEntradas extends JPanel {

    private final MonitorEntradaServicio monitorEntradaServicio;

    private final JLabel[] etiquetasDisponibilidad = new JLabel[3];

    private final Long[] idsEntradas = {7L, 8L, 9L};

    private final Timer actualizador;

    public PanelEntradas(MonitorEntradaServicio monitorEntradaServicio) {

        this.monitorEntradaServicio = monitorEntradaServicio;

        setLayout(new GridLayout(1, 3, 15, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setBackground(new Color(235, 238, 242));

        crearPaneles();

        actualizador = new Timer(1000, e -> actualizarDisponibilidad());

        actualizarDisponibilidad();
    }

    private void crearPaneles() {

        for (int i = 0; i < 3; i++) {

            JPanel panel = new JPanel(new BorderLayout(5, 10));

            panel.setBackground(Color.WHITE);
            panel.setBorder(new LineBorder(
                    new Color(180, 185, 190), 1
            ));

            JLabel titulo = new JLabel(
                    "ENTRADA " + (i + 1),
                    SwingConstants.CENTER
            );

            titulo.setFont(new Font("Arial", Font.BOLD, 16));

            etiquetasDisponibilidad[i] = new JLabel(
                    "Lugares disponibles: 0",
                    SwingConstants.CENTER
            );

            etiquetasDisponibilidad[i].setFont(
                    new Font("Arial", Font.PLAIN, 14)
            );

            panel.add(titulo, BorderLayout.NORTH);
            panel.add(etiquetasDisponibilidad[i], BorderLayout.CENTER);

            add(panel);
        }
    }

    private void actualizarDisponibilidad() {

        for (int i = 0; i < 3; i++) {

            long disponibles =
                    monitorEntradaServicio.obtenerDisponibilidad(idsEntradas[i]);

            etiquetasDisponibilidad[i].setText(
                    "Lugares disponibles: " + disponibles
            );
        }
    }

    public void iniciarActualizador() {
        actualizarDisponibilidad();
        actualizador.start();
    }

    public void detenerActualizador() {
        actualizador.stop();
    }
}
