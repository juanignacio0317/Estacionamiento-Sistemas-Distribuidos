
package estacionamiento.ui;

import estacionamiento.rabbit.MonitorEntradaServicio;
import estacionamiento.servicios.*;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class VentanaPrincipal {

    private final EstacionamientoServicio estacionamientoServicio;
    private final SimulacionServicio simulacionServicio;
    private final MonitorEntradaServicio monitorEntradaServicio;
    private final ActividadServicio actividadServicio;
    private final VehiculosBuscandoServicio vehiculosBuscandoServicio;

    private JFrame ventana;

    private JButton botonIniciar;
    private JButton botonFinalizar;
    private JLabel estadoSimulacion;

    private PanelEntradas panelEntradas;
    private PanelEstacionamiento panelEstacionamiento;
    private PanelBuscando panelBuscando;
    private PanelActividad panelActividad;

    public VentanaPrincipal(
            EstacionamientoServicio estacionamientoServicio,
            SimulacionServicio simulacionServicio,
            MonitorEntradaServicio monitorEntradaServicio,
            ActividadServicio actividadServicio,
            VehiculosBuscandoServicio vehiculosBuscandoServicio
    ) {
        this.estacionamientoServicio = estacionamientoServicio;
        this.simulacionServicio = simulacionServicio;
        this.monitorEntradaServicio = monitorEntradaServicio;
        this.actividadServicio = actividadServicio;
        this.vehiculosBuscandoServicio = vehiculosBuscandoServicio;
    }

    public void mostrar() {

        ventana = new JFrame("Sistema de Estacionamiento");

        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1000, 800);
        ventana.setMinimumSize(new Dimension(850, 700));
        ventana.setLocationRelativeTo(null);
        ventana.setLayout(new BorderLayout());

        // PANEL SUPERIOR

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

        estadoSimulacion.setFont(new Font("Arial", Font.PLAIN, 14));

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

        // PANELES

        panelEntradas = new PanelEntradas(monitorEntradaServicio);

        panelEstacionamiento =
                new PanelEstacionamiento(estacionamientoServicio);

        panelBuscando =
                new PanelBuscando(vehiculosBuscandoServicio);

        panelActividad =
                new PanelActividad(actividadServicio);

        // PESTAÑA ESTACIONAMIENTO

        JPanel panelEstacionamientoTab = new JPanel(
                new BorderLayout()
        );

        JPanel panelSuperiorEstacionamiento = new JPanel(
                new BorderLayout()
        );

        panelSuperiorEstacionamiento.add(
                panelEntradas,
                BorderLayout.NORTH
        );

        panelSuperiorEstacionamiento.add(
                panelBuscando,
                BorderLayout.CENTER
        );

        panelEstacionamientoTab.add(
                panelSuperiorEstacionamiento,
                BorderLayout.NORTH
        );

        panelEstacionamientoTab.add(
                panelEstacionamiento,
                BorderLayout.CENTER
        );

        // PESTAÑAS

        JTabbedPane pestañas = new JTabbedPane();

        pestañas.addTab(
                "Estacionamiento",
                panelEstacionamientoTab
        );

        pestañas.addTab(
                "Actividad",
                panelActividad
        );

        ventana.add(panelSuperior, BorderLayout.NORTH);
        ventana.add(pestañas, BorderLayout.CENTER);

        ventana.setVisible(true);
    }

    private void iniciarSimulacion() {

        botonIniciar.setEnabled(false);
        estadoSimulacion.setText("Iniciando simulación...");

        // Comienza una nueva lista de actividades en memoria.
        actividadServicio.limpiar();
        vehiculosBuscandoServicio.limpiar();

        panelActividad.reiniciar();
        panelBuscando.reiniciar();

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

                    panelEntradas.iniciarActualizador();
                    panelEstacionamiento.iniciarActualizador();
                    panelBuscando.iniciarActualizador();
                    panelActividad.iniciarActualizador();

                    estadoSimulacion.setText(
                            "Simulación en ejecución"
                    );

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

                    // Detenemos todos los temporizadores de la interfaz.
                    panelEntradas.detenerActualizador();
                    panelEstacionamiento.detenerActualizador();
                    panelBuscando.detenerActualizador();
                    panelActividad.detenerActualizador();

                    estadoSimulacion.setText(
                            "Simulación finalizada"
                    );

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

