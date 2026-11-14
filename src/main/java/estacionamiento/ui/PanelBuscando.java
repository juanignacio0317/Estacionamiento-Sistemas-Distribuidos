
package estacionamiento.ui;

import estacionamiento.servicios.VehiculosBuscandoServicio;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PanelBuscando extends JPanel {

    private final VehiculosBuscandoServicio vehiculosBuscandoServicio;

    private final JPanel panelVehiculos;
    private final Timer actualizador;

    private List<String> ultimaLista = new ArrayList<>();

    public PanelBuscando(
            VehiculosBuscandoServicio vehiculosBuscandoServicio
    ) {
        this.vehiculosBuscandoServicio = vehiculosBuscandoServicio;

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        setBackground(new Color(235, 238, 242));

        JLabel titulo = new JLabel("VEHÍCULOS BUSCANDO LUGAR");
        titulo.setFont(new Font("Arial", Font.BOLD, 15));

        panelVehiculos = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 5)
        );

        panelVehiculos.setBackground(new Color(235, 238, 242));

        JScrollPane scroll = new JScrollPane(panelVehiculos);

        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        add(titulo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        actualizador = new Timer(500, e -> actualizarVehiculos());
    }

    private void actualizarVehiculos() {

        List<String> matriculas = new ArrayList<>(
                vehiculosBuscandoServicio.obtenerVehiculosBuscando()
        );

        Collections.sort(matriculas);

        if (matriculas.equals(ultimaLista)) {
            return;
        }

        ultimaLista = new ArrayList<>(matriculas);

        panelVehiculos.removeAll();

        for (String matricula : matriculas) {

            JLabel etiqueta = new JLabel(
                    matricula,
                    new IconoAuto(),
                    SwingConstants.CENTER
            );

            etiqueta.setHorizontalTextPosition(
                    SwingConstants.CENTER
            );

            etiqueta.setVerticalTextPosition(
                    SwingConstants.BOTTOM
            );

            etiqueta.setFont(
                    new Font("Arial", Font.BOLD, 11)
            );

            etiqueta.setToolTipText(
                    "Vehículo buscando lugar: " + matricula
            );

            panelVehiculos.add(etiqueta);
        }

        panelVehiculos.revalidate();
        panelVehiculos.repaint();
    }

    public void iniciarActualizador() {
        actualizarVehiculos();
        actualizador.start();
    }

    public void detenerActualizador() {
        actualizador.stop();
    }

    public void reiniciar() {
        ultimaLista.clear();
        panelVehiculos.removeAll();
        panelVehiculos.revalidate();
        panelVehiculos.repaint();
    }
}
