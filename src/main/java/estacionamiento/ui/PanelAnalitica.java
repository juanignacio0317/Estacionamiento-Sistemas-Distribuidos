
package estacionamiento.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class PanelAnalitica extends JPanel {

    private static final String API =
            "http://localhost:8080/api/admin/analitica";

    private final HttpClient clienteHttp = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public PanelAnalitica() {

        setLayout(new BorderLayout());

        JPanel contenido = new JPanel();

        contenido.setLayout(
                new BoxLayout(contenido, BoxLayout.Y_AXIS)
        );

        contenido.setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );

        contenido.add(crearPanelTabla(
                "Ingresos por entrada",
                "/ingresos",
                new String[]{"Entrada", "Ingresos"},
                new String[]{"entrada", "cantidad"},
                new String[]{"HISTORICO", "24_HORAS", "5_MINUTOS"}
        ));

        contenido.add(Box.createVerticalStrut(12));

        contenido.add(crearPanelTabla(
                "Salidas por entrada",
                "/salidas",
                new String[]{"Entrada", "Salidas"},
                new String[]{"entrada", "cantidad"},
                new String[]{"HISTORICO", "24_HORAS", "5_MINUTOS"}
        ));

        contenido.add(Box.createVerticalStrut(12));

        contenido.add(crearPanelResumen(
                "Vehículos diferentes que ingresaron",
                "/vehiculos-diferentes",
                new String[]{"HISTORICO", "24_HORAS", "5_MINUTOS"},
                new String[]{"cantidad"},
                new String[]{"Vehículos diferentes"}
        ));

        contenido.add(Box.createVerticalStrut(12));

        contenido.add(crearPanelTabla(
                "Top 10 vehículos con más estadías",
                "/top-estadias",
                new String[]{"Matrícula", "Cantidad de estadías"},
                new String[]{"matricula", "cantidadEstadias"},
                new String[]{"HISTORICO", "24_HORAS"}
        ));

        contenido.add(Box.createVerticalStrut(12));

        contenido.add(crearPanelResumen(
                "Tiempo promedio de estadía",
                "/tiempo-promedio",
                new String[]{"HISTORICO", "24_HORAS"},
                new String[]{"promedioMinutos", "cantidadEstadias"},
                new String[]{"Promedio (minutos)", "Estadías consideradas"}
        ));

        contenido.add(Box.createVerticalStrut(12));

        add(
                new JScrollPane(contenido),
                BorderLayout.CENTER
        );
    }

    // PANEL PARA CONSULTAS QUE DEVUELVEN LISTAS

    private JPanel crearPanelTabla(
            String titulo,
            String endpoint,
            String[] columnas,
            String[] camposJson,
            String[] periodos
    ) {

        JPanel panel = new JPanel(new BorderLayout(8, 8));

        panel.setBorder(
                BorderFactory.createTitledBorder(titulo)
        );

        JPanel controles = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        JComboBox<String> selectorPeriodo =
                new JComboBox<>(periodos);

        JButton botonConsultar =
                new JButton("Consultar");

        DefaultTableModel modelo = new DefaultTableModel(
                columnas,
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        JTable tabla = new JTable(modelo);

        tabla.setFillsViewportHeight(true);

        controles.add(new JLabel("Período:"));
        controles.add(selectorPeriodo);
        controles.add(botonConsultar);

        botonConsultar.addActionListener(e -> {

            modelo.setRowCount(0);

            String periodo =
                    (String) selectorPeriodo.getSelectedItem();

            botonConsultar.setEnabled(false);

            consultarTabla(
                    endpoint,
                    periodo,
                    modelo,
                    camposJson,
                    botonConsultar
            );
        });

        panel.add(controles, BorderLayout.NORTH);

        panel.add(
                new JScrollPane(tabla),
                BorderLayout.CENTER
        );

        panel.setPreferredSize(new Dimension(700, 180));
        panel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 180)
        );

        return panel;
    }

    // PANEL PARA CONSULTAS QUE DEVUELVEN UN OBJETO

    private JPanel crearPanelResumen(
            String titulo,
            String endpoint,
            String[] periodos,
            String[] camposJson,
            String[] nombresCampos
    ) {

        JPanel panel = new JPanel(new BorderLayout(8, 8));

        panel.setBorder(
                BorderFactory.createTitledBorder(titulo)
        );

        JPanel controles = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        JComboBox<String> selectorPeriodo =
                new JComboBox<>(periodos);

        JButton botonConsultar =
                new JButton("Consultar");

        JLabel resultado = new JLabel(
                "Presione consultar para obtener los resultados."
        );

        resultado.setBorder(
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );

        resultado.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        controles.add(new JLabel("Período:"));
        controles.add(selectorPeriodo);
        controles.add(botonConsultar);

        botonConsultar.addActionListener(e -> {

            resultado.setText("Consultando...");

            String periodo =
                    (String) selectorPeriodo.getSelectedItem();

            botonConsultar.setEnabled(false);

            consultarObjeto(
                    endpoint,
                    periodo,
                    camposJson,
                    nombresCampos,
                    resultado,
                    botonConsultar
            );
        });

        panel.add(controles, BorderLayout.NORTH);
        panel.add(resultado, BorderLayout.CENTER);

        panel.setPreferredSize(new Dimension(700, 110));
        panel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 110)
        );

        return panel;
    }

    // CONSULTA REST PARA TABLAS

    private void consultarTabla(
            String endpoint,
            String periodo,
            DefaultTableModel modelo,
            String[] camposJson,
            JButton boton
    ) {

        ejecutarConsulta(
                endpoint + "?periodo=" + periodo,

                json -> {

                    if (!json.isArray()) {
                        return;
                    }

                    for (JsonNode elemento : json) {

                        Object[] fila =
                                new Object[camposJson.length];

                        for (int i = 0; i < camposJson.length; i++) {

                            JsonNode valor =
                                    elemento.get(camposJson[i]);

                            fila[i] = obtenerTexto(valor);
                        }

                        modelo.addRow(fila);
                    }

                    if (json.isEmpty()) {

                        modelo.addRow(
                                new Object[]{"Sin resultados"}
                        );
                    }

                    boton.setEnabled(true);
                },

                error -> {

                    boton.setEnabled(true);

                    JOptionPane.showMessageDialog(
                            this,
                            error,
                            "Error de analítica",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
        );
    }

    // CONSULTA REST PARA RESULTADOS SIMPLES

    private void consultarObjeto(
            String endpoint,
            String periodo,
            String[] camposJson,
            String[] nombresCampos,
            JLabel resultado,
            JButton boton
    ) {

        ejecutarConsulta(
                endpoint + "?periodo=" + periodo,

                json -> {

                    StringBuilder texto =
                            new StringBuilder("<html>");

                    for (int i = 0; i < camposJson.length; i++) {

                        JsonNode valor =
                                json.get(camposJson[i]);

                        texto.append(nombresCampos[i])
                                .append(": <b>")
                                .append(obtenerTexto(valor))
                                .append("</b>");

                        if (i < camposJson.length - 1) {
                            texto.append("<br>");
                        }
                    }

                    texto.append("</html>");

                    resultado.setText(texto.toString());

                    boton.setEnabled(true);
                },

                error -> {

                    boton.setEnabled(true);
                    resultado.setText(error);
                }
        );
    }

    // MÉTODO GENERAL PARA CONSULTAS REST

    private void ejecutarConsulta(
            String endpoint,
            Consumer<JsonNode> alCompletar,
            Consumer<String> alError
    ) {

        SwingWorker<JsonNode, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected JsonNode doInBackground()
                            throws Exception {

                        HttpRequest request =
                                HttpRequest.newBuilder()
                                        .uri(URI.create(API + endpoint))
                                        .GET()
                                        .build();

                        HttpResponse<String> response =
                                clienteHttp.send(
                                        request,
                                        HttpResponse.BodyHandlers.ofString()
                                );

                        if (response.statusCode() != 200) {

                            try {

                                JsonNode error =
                                        mapper.readTree(response.body());

                                if (error.has("error")) {
                                    throw new RuntimeException(
                                            error.get("error").asText()
                                    );
                                }

                            } catch (RuntimeException ex) {
                                throw ex;
                            } catch (Exception ignored) {
                            }

                            throw new RuntimeException(
                                    "Error HTTP: " + response.statusCode()
                            );
                        }

                        return mapper.readTree(response.body());
                    }

                    @Override
                    protected void done() {

                        try {

                            alCompletar.accept(get());

                        } catch (Exception e) {

                            Throwable causa = e.getCause();

                            if (causa != null
                                    && causa.getMessage() != null) {

                                alError.accept(causa.getMessage());

                            } else {

                                alError.accept(
                                        "No se pudo conectar con el servidor."
                                );
                            }
                        }
                    }
                };

        worker.execute();
    }

    private String obtenerTexto(JsonNode nodo) {

        if (nodo == null || nodo.isNull()) {
            return "-";
        }

        return nodo.asText();
    }
}
