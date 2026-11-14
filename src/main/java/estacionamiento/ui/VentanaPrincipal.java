
package estacionamiento.ui;

import estacionamiento.servicios.EstacionamientoServicio;
import estacionamiento.servicios.SimulacionServicio;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class VentanaPrincipal {

    private final EstacionamientoServicio estacionamientoServicio;
    private final SimulacionServicio simulacionServicio;

    private JFrame ventana;
    private JButton botonIniciar;
    private JButton botonFinalizar;
    private JLabel estadoSimulacion;

    public VentanaPrincipal(
            EstacionamientoServicio estacionamientoServicio,
            SimulacionServicio simulacionServicio
    ) {
        this.estacionamientoServicio = estacionamientoServicio;
        this.simulacionServicio = simulacionServicio;
    }

    public void mostrar() {

        ventana = new JFrame("Sistema de Estacionamiento");

        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(900, 750);
        ventana.setMinimumSize(new Dimension(750, 650));
        ventana.setLocationRelativeTo(null);
        ventana.setLayout(new BorderLayout());

        // Panel superior.
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));

        panelSuperior.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        JLabel titulo = new JLabel(
                "ESTACIONAMIENTO",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 24));

        estadoSimulacion = new JLabel(
                "Simulación detenida",
                SwingConstants.CENTER
        );

        JPanel panelBotones = new JPanel(new FlowLayout());

        botonIniciar = new JButton("Iniciar simulación");
        botonFinalizar = new JButton("Finalizar simulación");

        botonIniciar.setFont(new Font("Arial", Font.BOLD, 14));
        botonFinalizar.setFont(new Font("Arial", Font.BOLD, 14));

        botonFinalizar.setEnabled(false);

        botonIniciar.addActionListener(e -> iniciarSimulacion());
        botonFinalizar.addActionListener(e -> finalizarSimulacion());

        panelBotones.add(botonIniciar);
        panelBotones.add(botonFinalizar);

        panelSuperior.add(titulo, BorderLayout.NORTH);
        panelSuperior.add(estadoSimulacion, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        // Panel central.
        PanelEstacionamiento panelEstacionamiento =
                new PanelEstacionamiento(estacionamientoServicio);

        ventana.add(panelSuperior, BorderLayout.NORTH);
        ventana.add(panelEstacionamiento, BorderLayout.CENTER);

        ventana.setVisible(true);
    }

    private void iniciarSimulacion() {

        botonIniciar.setEnabled(false);
        estadoSimulacion.setText("Iniciando simulación...");

        SwingWorker<Void, Void> tarea = new SwingWorker<>() {

            @Override
            protected Void doInBackground() {
                simulacionServicio.iniciar();
                return null;
            }

            @Override
            protected void done() {

                try {
                    get();

                    estadoSimulacion.setText("Simulación en ejecución");
                    botonFinalizar.setEnabled(true);

                } catch (Exception e) {

                    estadoSimulacion.setText(
                            "Error al iniciar la simulación"
                    );

                    botonIniciar.setEnabled(true);
                    e.printStackTrace();
                }
            }
        };

        tarea.execute();
    }

    private void finalizarSimulacion() {

        int confirmacion = JOptionPane.showConfirmDialog(
                ventana,
                "¿Estás seguro de que querés finalizar la simulación?",
                "Finalizar simulación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        botonFinalizar.setEnabled(false);
        estadoSimulacion.setText("Finalizando simulación...");

        SwingWorker<Void, Void> tarea = new SwingWorker<>() {

            @Override
            protected Void doInBackground() {
                simulacionServicio.detener();
                return null;
            }

            @Override
            protected void done() {

                try {
                    get();

                    estadoSimulacion.setText("Simulación finalizada");

                    botonIniciar.setEnabled(false);

                    JOptionPane.showMessageDialog(
                            ventana,
                            "La simulación finalizó correctamente.",
                            "Simulación finalizada",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (Exception e) {

                    estadoSimulacion.setText(
                            "Error al finalizar la simulación"
                    );

                    e.printStackTrace();

                    JOptionPane.showMessageDialog(
                            ventana,
                            "Ocurrió un error al finalizar la simulación.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        tarea.execute();
    }
}
