
package estacionamiento.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.*;

public class PanelCierre extends JPanel {

    private static final String API =
            "http://localhost:8080/api/admin/cierre";

    private final HttpClient cliente = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final JLabel estado = new JLabel("Consultando estado...");
    private final JLabel resultado = new JLabel(" ");

    private final JButton botonCambiar =
            new JButton("Cerrar entradas");

    private final JButton botonActualizar =
            new JButton("Actualizar estado");

    private boolean entradasAbiertas = true;

    public PanelCierre() {

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titulo = new JLabel("Control de entradas");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(
                new BoxLayout(panelCentral, BoxLayout.Y_AXIS)
        );

        estado.setFont(new Font("Arial", Font.BOLD, 17));
        estado.setAlignmentX(Component.CENTER_ALIGNMENT);

        botonCambiar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonActualizar.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultado.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelCentral.add(Box.createVerticalStrut(30));
        panelCentral.add(estado);
        panelCentral.add(Box.createVerticalStrut(25));
        panelCentral.add(botonCambiar);
        panelCentral.add(Box.createVerticalStrut(10));
        panelCentral.add(botonActualizar);
        panelCentral.add(Box.createVerticalStrut(20));
        panelCentral.add(resultado);

        add(titulo, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);

        botonCambiar.addActionListener(e -> cambiarEstado());

        botonActualizar.addActionListener(e -> consultarEstado());

        consultarEstado();
    }

    private void consultarEstado() {

        ejecutarSolicitud("GET", "", false);
    }

    private void cambiarEstado() {

        String accion = entradasAbiertas ? "cerrar" : "abrir";

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                entradasAbiertas
                        ? "¿Desea cerrar las entradas del estacionamiento?"
                        : "¿Desea abrir nuevamente las entradas?",
                "Confirmar operación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        ejecutarSolicitud(
                "POST",
                "/" + accion,
                true
        );
    }

    private void ejecutarSolicitud(
            String metodo,
            String ruta,
            boolean esCambio
    ) {

        botonCambiar.setEnabled(false);
        botonActualizar.setEnabled(false);

        SwingWorker<JsonNode, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected JsonNode doInBackground() throws Exception {

                        HttpRequest.Builder builder =
                                HttpRequest.newBuilder()
                                        .uri(URI.create(API + ruta));

                        if (metodo.equals("POST")) {
                            builder.POST(
                                    HttpRequest.BodyPublishers.noBody()
                            );
                        } else {
                            builder.GET();
                        }

                        HttpResponse<String> respuesta =
                                cliente.send(
                                        builder.build(),
                                        HttpResponse.BodyHandlers.ofString()
                                );

                        if (respuesta.statusCode() < 200
                                || respuesta.statusCode() >= 300) {
                            throw new RuntimeException(
                                    "Error HTTP: " + respuesta.statusCode()
                            );
                        }

                        return mapper.readTree(respuesta.body());
                    }

                    @Override
                    protected void done() {

                        botonCambiar.setEnabled(true);
                        botonActualizar.setEnabled(true);

                        try {

                            JsonNode json = get();

                            entradasAbiertas =
                                    json.get("entradasAbiertas").asBoolean();

                            actualizarVisualizacion();

                            if (esCambio && json.has("mensaje")) {
                                resultado.setText(
                                        json.get("mensaje").asText()
                                );
                            }

                        } catch (Exception e) {

                            resultado.setText(
                                    "No se pudo comunicar con el servidor."
                            );

                            e.printStackTrace();
                        }
                    }
                };

        worker.execute();
    }

    private void actualizarVisualizacion() {

        if (entradasAbiertas) {

            estado.setText("ENTRADAS ABIERTAS");
            estado.setForeground(new Color(30, 140, 60));
            botonCambiar.setText("Cerrar entradas");

        } else {

            estado.setText("ENTRADAS CERRADAS");
            estado.setForeground(Color.RED);
            botonCambiar.setText("Abrir entradas");
        }
    }
}

