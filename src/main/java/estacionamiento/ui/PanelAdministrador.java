
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
import java.util.Locale;
import java.util.regex.Pattern;

public class PanelAdministrador extends JPanel {

    private static final String API = "http://localhost:8080/api/admin";

    private static final Pattern FORMATO_MATRICULA =
            Pattern.compile("[A-Za-z]{3}\\d{4}");

    private final HttpClient clienteHttp = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // CONSULTA DE ESTADO

    private final JTextField campoEstado = new JTextField(12);

    private final JLabel resultadoEstado =
            new JLabel("Ingrese una matrícula.");

    // CONSULTA DE HISTORIAL

    private final JTextField campoHistorial = new JTextField(12);

    private final DefaultTableModel modeloHistorial =
            new DefaultTableModel(
                    new String[]{
                            "Fecha entrada",
                            "Puerta entrada",
                            "Lugar",
                            "Fecha salida",
                            "Puerta salida"
                    }, 0) {

                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };

    private final JTable tablaHistorial =
            new JTable(modeloHistorial);

    // CONSULTA DE DISPONIBILIDAD

    private final JLabel resultadoLugares =
            new JLabel("Presione actualizar para consultar.");

    private final JTextArea listaLugares =
            new JTextArea(3, 40);

    public PanelAdministrador() {

        setLayout(new BorderLayout());

        JTabbedPane pestañas = new JTabbedPane();

        pestañas.addTab(
                "Estadías",
                crearPanelEstadias()
        );

        pestañas.addTab(
                "Disponibilidad",
                crearPanelDisponibilidad()
        );

        add(pestañas, BorderLayout.CENTER);
    }

    // =========================================================
    // PESTAÑA 1: ESTADÍAS
    // =========================================================

    private JPanel crearPanelEstadias() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );

        JPanel contenido = new JPanel();

        contenido.setLayout(
                new BoxLayout(contenido, BoxLayout.Y_AXIS)
        );

        contenido.add(crearPanelEstado());

        contenido.add(Box.createVerticalStrut(12));

        contenido.add(crearPanelHistorial());

        panel.add(
                new JScrollPane(contenido),
                BorderLayout.CENTER
        );

        return panel;
    }

    private JPanel crearPanelEstado() {

        JPanel panel = new JPanel(new BorderLayout(8, 8));

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Consultar estado de vehículo"
                )
        );

        JPanel controles = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        JButton botonConsultar = new JButton("Consultar");

        controles.add(new JLabel("Matrícula:"));
        controles.add(campoEstado);
        controles.add(botonConsultar);

        resultadoEstado.setBorder(
                BorderFactory.createEmptyBorder(4, 8, 4, 4)
        );

        botonConsultar.addActionListener(
                e -> consultarEstado()
        );

        panel.add(controles, BorderLayout.NORTH);
        panel.add(resultadoEstado, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelHistorial() {

        JPanel panel = new JPanel(new BorderLayout(8, 8));

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Historial de estadías"
                )
        );

        JPanel controles = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        JButton botonConsultar = new JButton("Consultar");

        controles.add(new JLabel("Matrícula:"));
        controles.add(campoHistorial);
        controles.add(botonConsultar);

        tablaHistorial.setFillsViewportHeight(true);

        tablaHistorial.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        botonConsultar.addActionListener(
                e -> consultarHistorial()
        );

        panel.add(controles, BorderLayout.NORTH);

        panel.add(
                new JScrollPane(tablaHistorial),
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // PESTAÑA 2: DISPONIBILIDAD
    // =========================================================

    private JPanel crearPanelDisponibilidad() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JPanel contenido = new JPanel(
                new BorderLayout(10, 15)
        );

        contenido.setBorder(
                BorderFactory.createTitledBorder(
                        "Disponibilidad del estacionamiento"
                )
        );

        JPanel controles = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        JButton botonActualizar = new JButton(
                "Actualizar disponibilidad"
        );

        botonActualizar.addActionListener(
                e -> consultarLugares()
        );

        controles.add(botonActualizar);

        resultadoLugares.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        resultadoLugares.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        listaLugares.setEditable(false);
        listaLugares.setLineWrap(true);
        listaLugares.setWrapStyleWord(true);

        listaLugares.setFont(
                new Font("Arial", Font.PLAIN, 15)
        );

        listaLugares.setBackground(
                panel.getBackground()
        );

        JScrollPane desplazamiento = new JScrollPane(listaLugares);

        desplazamiento.setPreferredSize(
                new Dimension(600, 120)
        );

        JPanel informacion = new JPanel(
                new BorderLayout(5, 10)
        );

        informacion.add(
                resultadoLugares,
                BorderLayout.NORTH
        );

        informacion.add(
                desplazamiento,
                BorderLayout.CENTER
        );

        contenido.add(controles, BorderLayout.NORTH);
        contenido.add(informacion, BorderLayout.CENTER);

        panel.add(contenido, BorderLayout.NORTH);

        return panel;
    }

    // =========================================================
    // CONSULTA 1: ESTADO ACTUAL DEL VEHÍCULO
    // =========================================================

    private void consultarEstado() {

        String matricula =
                normalizarMatricula(campoEstado.getText());

        if (!matriculaValida(matricula)) {

            resultadoEstado.setText("Matrícula inválida.");

            return;
        }

        resultadoEstado.setText("Consultando...");

        ejecutarConsulta(
                API + "/vehiculos/" + matricula,

                json -> {

                    String estado = json.get("estado").asText();

                    String texto =
                            "Matrícula: " + json.get("matricula").asText()
                                    + " | Estado: " + estado;

                    JsonNode numeroLugar =
                            json.get("numeroLugar");

                    if (numeroLugar != null && !numeroLugar.isNull()) {

                        texto += " | Lugar: " + numeroLugar.asInt();
                    }

                    resultadoEstado.setText(texto);
                },

                error -> resultadoEstado.setText(error)
        );
    }

    // =========================================================
    // CONSULTA 2: HISTORIAL DE ESTADÍAS
    // =========================================================

    private void consultarHistorial() {

        String matricula =
                normalizarMatricula(campoHistorial.getText());

        if (!matriculaValida(matricula)) {

            limpiarHistorial();

            JOptionPane.showMessageDialog(
                    this,
                    "Matrícula inválida.",
                    "Consulta de historial",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        limpiarHistorial();

        ejecutarConsulta(
                API + "/vehiculos/" + matricula + "/estadias",

                json -> {

                    if (!json.isArray()) {
                        return;
                    }

                    for (JsonNode estadia : json) {

                        modeloHistorial.addRow(new Object[]{

                                formatearFecha(
                                        estadia.get("fechaEntrada")
                                ),

                                obtenerTexto(
                                        estadia.get("puertaEntrada")
                                ),

                                obtenerTexto(
                                        estadia.get("numeroLugar")
                                ),

                                formatearFecha(
                                        estadia.get("fechaSalida")
                                ),

                                obtenerTexto(
                                        estadia.get("puertaSalida")
                                )
                        });
                    }

                    if (json.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                this,
                                "El vehículo no tiene estadías registradas."
                        );
                    }
                },

                error -> {

                    limpiarHistorial();

                    JOptionPane.showMessageDialog(
                            this,
                            error,
                            "Consulta de historial",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
        );
    }

    // =========================================================
    // CONSULTA 3: LUGARES DISPONIBLES
    // =========================================================

    private void consultarLugares() {

        resultadoLugares.setText("Consultando...");

        listaLugares.setText("");

        ejecutarConsulta(
                API + "/lugares/disponibles",

                json -> {

                    int cantidad =
                            json.get("cantidadLibres").asInt();

                    JsonNode lugares =
                            json.get("lugaresLibres");

                    resultadoLugares.setText(
                            "Cantidad de lugares libres: " + cantidad
                    );

                    StringBuilder texto = new StringBuilder();

                    if (lugares != null && lugares.isArray()) {

                        for (JsonNode lugar : lugares) {

                            if (texto.length() > 0) {
                                texto.append(", ");
                            }

                            texto.append(lugar.asInt());
                        }
                    }

                    if (texto.length() == 0) {

                        listaLugares.setText(
                                "No hay lugares disponibles."
                        );

                    } else {

                        listaLugares.setText(
                                "Números de lugares libres: " + texto
                        );
                    }
                },

                error -> {

                    resultadoLugares.setText(error);

                    listaLugares.setText("");
                }
        );
    }

    // =========================================================
    // MÉTODO GENERAL PARA CONSULTAS REST
    // =========================================================

    private void ejecutarConsulta(
            String url,
            java.util.function.Consumer<JsonNode> alCompletar,
            java.util.function.Consumer<String> alError) {

        SwingWorker<JsonNode, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected JsonNode doInBackground()
                            throws Exception {

                        HttpRequest request =
                                HttpRequest.newBuilder()
                                        .uri(URI.create(url))
                                        .GET()
                                        .build();

                        HttpResponse<String> response =
                                clienteHttp.send(
                                        request,
                                        HttpResponse.BodyHandlers.ofString()
                                );

                        if (response.statusCode() == 404) {

                            throw new RuntimeException(
                                    "Matrícula inválida."
                            );
                        }

                        if (response.statusCode() != 200) {

                            throw new RuntimeException(
                                    "Error al consultar. Código HTTP: "
                                            + response.statusCode()
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

    // =========================================================
    // MÉTODOS AUXILIARES
    // =========================================================

    private String normalizarMatricula(String matricula) {

        if (matricula == null) {
            return "";
        }

        return matricula.replaceAll("\\s+", "")
                .toUpperCase(Locale.ROOT);
    }

    private boolean matriculaValida(String matricula) {

        return FORMATO_MATRICULA.matcher(matricula).matches();
    }

    private String obtenerTexto(JsonNode nodo) {

        if (nodo == null || nodo.isNull()) {
            return "-";
        }

        return nodo.asText();
    }

    private String formatearFecha(JsonNode nodo) {

        if (nodo == null || nodo.isNull()) {
            return "-";
        }

        return nodo.asText().replace("T", " ");
    }

    private void limpiarHistorial() {

        modeloHistorial.setRowCount(0);
    }
}


