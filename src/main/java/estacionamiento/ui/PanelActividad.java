
package estacionamiento.ui;

import estacionamiento.servicios.ActividadServicio;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelActividad extends JPanel {

    private final ActividadServicio actividadServicio;

    private final JTextArea areaActividad;
    private final Timer actualizador;

    private int cantidadMostrada = 0;

    public PanelActividad(ActividadServicio actividadServicio) {

        this.actividadServicio = actividadServicio;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("REGISTRO DE ACTIVIDAD");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));

        areaActividad = new JTextArea();

        areaActividad.setEditable(false);
        areaActividad.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaActividad.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(areaActividad);

        add(titulo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        actualizador = new Timer(500, e -> actualizarActividad());
    }

    private void actualizarActividad() {

        List<String> actividades =
                actividadServicio.obtenerActividades();

        if (actividades.size() < cantidadMostrada) {
            reiniciar();
        }

        for (int i = cantidadMostrada; i < actividades.size(); i++) {

            areaActividad.append(actividades.get(i));
            areaActividad.append("\n");
        }

        cantidadMostrada = actividades.size();

        areaActividad.setCaretPosition(
                areaActividad.getDocument().getLength()
        );
    }

    public void iniciarActualizador() {
        actualizarActividad();
        actualizador.start();
    }

    public void detenerActualizador() {
        actualizador.stop();
    }

    public void reiniciar() {
        areaActividad.setText("");
        cantidadMostrada = 0;
    }
}
