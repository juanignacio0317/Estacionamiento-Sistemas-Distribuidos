
package estacionamiento.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PanelReservas extends JPanel {

    private static final String API =
            "http://localhost:8080/api/admin/reservas";

    private final HttpClient clienteHttp = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final JTextField campoLugar = new JTextField(8);

    private final DefaultListModel<Integer> modeloReservas =
            new DefaultListModel<>();

    private final JList<Integer> listaReservas =
            new JList<>(modeloReservas);

    private final JLabel resultado =
            new JLabel("Ingrese un número para reservar un lugar.");

    private final JButton botonReservar =
            new JButton("Reservar");

    public PanelReservas() {

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JPanel contenido = new JPanel(new BorderLayout(10, 15));

        contenido.setBorder(
                BorderFactory.createTitledBorder(
                        "Administración de reservas"
                )
        );

        // -------------------------------------------------
        // SECCIÓN PARA CREAR UNA RESERVA
        // -------------------------------------------------

        JPanel panelCrear = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        panelCrear.add(new JLabel("Lugar a reservar (1-100):"));
        panelCrear.add(campoLugar);
        panelCrear.add(botonReservar);

        botonReservar.addActionListener(
                e -> reservarLugar()
        );

        campoLugar.addActionListener(
                e -> reservarLugar()
        );

        // -------------------------------------------------
        // SECCIÓN DE RESERVAS ACTIVAS
        // -------------------------------------------------

        JPanel panelLista = new JPanel(new BorderLayout(5, 8));

        panelLista.setBorder(
                BorderFactory.createTitledBorder(
                        "Lugares reservados"
                )
        );

        JLabel indicacion = new JLabel(
                "Haga clic sobre un número para cancelar su reserva."
        );

        listaReservas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        listaReservas.setVisibleRowCount(8);

        listaReservas.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        listaReservas.setLayoutOrientation(
                JList.HORIZONTAL_WRAP
        );

        listaReservas.setFixedCellWidth(65);
        listaReservas.setFixedCellHeight(35);

        listaReservas.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (e.getClickCount() == 1 &&
                                SwingUtilities.isLeftMouseButton(e)) {

                            int indice =
                                    listaReservas.locationToIndex(
                                            e.getPoint()
                                    );

                            if (indice >= 0) {

                                Rectangle limites =
                                        listaReservas.getCellBounds(
                                                indice,
                                                indice
                                        );

                                if (limites != null &&
                                        limites.contains(e.getPoint())) {

                                    Integer numero =
                                            modeloReservas.getElementAt(indice);

                                    cancelarReserva(numero);
                                }
                            }
                        }
                    }
                }
        );

        panelLista.add(
                indicacion,
                BorderLayout.NORTH
        );

        panelLista.add(
                new JScrollPane(listaReservas),
                BorderLayout.CENTER
        );

        // -------------------------------------------------
        // MENSAJE DE RESULTADO
        // -------------------------------------------------

        resultado.setBorder(
                BorderFactory.createEmptyBorder(8, 5, 8, 5)
        );

        contenido.add(panelCrear, BorderLayout.NORTH);
        contenido.add(panelLista, BorderLayout.CENTER);
        contenido.add(resultado, BorderLayout.SOUTH);

        add(contenido, BorderLayout.CENTER);

        // Consultar las reservas existentes al abrir la pestaña.
        actualizarReservas();
    }

    // =====================================================
    // RESERVAR UN LUGAR
    // =====================================================

    private void reservarLugar() {

        String texto = campoLugar.getText().trim();

        int numero;

        try {

            numero = Integer.parseInt(texto);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese un número válido.",
                    "Reserva",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (numero < 1 || numero > 100) {

            JOptionPane.showMessageDialog(
                    this,
                    "El número de lugar debe estar entre 1 y 100.",
                    "Reserva",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        botonReservar.setEnabled(false);
        resultado.setText("Registrando reserva...");

        ejecutarPeticion(
                API + "/" + numero,
                "POST",
                response -> {

                    campoLugar.setText("");

                    resultado.setText(
                            "Lugar " + numero + " reservado correctamente."
                    );

                    actualizarReservas();

                },
                error -> {

                    resultado.setText(error);

                    JOptionPane.showMessageDialog(
                            this,
                            error,
                            "Error al reservar",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
        );
    }

    // =====================================================
    // CANCELAR UNA RESERVA
    // =====================================================

    private void cancelarReserva(int numero) {

        ejecutarPeticion(
                API + "/" + numero,
                "DELETE",
                response -> {

                    JOptionPane.showMessageDialog(
                            this,
                            "Reserva de lugar " + numero + " cancelada.",
                            "Reserva cancelada",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    resultado.setText(
                            "La reserva del lugar " + numero + " fue cancelada."
                    );

                    actualizarReservas();

                },
                error -> {

                    JOptionPane.showMessageDialog(
                            this,
                            error,
                            "Error al cancelar",
                            JOptionPane.WARNING_MESSAGE
                    );

                    actualizarReservas();
                }
        );
    }

    // =====================================================
    // CONSULTAR RESERVAS ACTIVAS
    // =====================================================

    private void actualizarReservas() {

        resultado.setText("Actualizando reservas...");

        SwingWorker<JsonNode, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected JsonNode doInBackground()
                            throws Exception {

                        HttpRequest request =
                                HttpRequest.newBuilder()
                                        .uri(URI.create(API))
                                        .GET()
                                        .build();

                        HttpResponse<String> response =
                                clienteHttp.send(
                                        request,
                                        HttpResponse.BodyHandlers.ofString()
                                );

                        if (response.statusCode() != 200) {

                            throw new RuntimeException(
                                    "Error al consultar reservas. Código HTTP: "
                                            + response.statusCode()
                            );
                        }

                        return mapper.readTree(response.body());
                    }

                    @Override
                    protected void done() {

                        try {

                            JsonNode json = get();

                            modeloReservas.clear();

                            if (json.isArray()) {

                                for (JsonNode reserva : json) {

                                    modeloReservas.addElement(
                                            reserva.asInt()
                                    );
                                }
                            }

                            if (modeloReservas.isEmpty()) {

                                resultado.setText(
                                        "No hay lugares reservados."
                                );

                            } else {

                                resultado.setText(
                                        "Cantidad de lugares reservados: "
                                                + modeloReservas.size()
                                );
                            }

                        } catch (Exception e) {

                            resultado.setText(
                                    "No se pudieron consultar las reservas."
                            );
                        }
                    }
                };

        worker.execute();
    }

    // =====================================================
    // MÉTODO GENERAL PARA POST Y DELETE
    // =====================================================

    private void ejecutarPeticion(
            String url,
            String metodo,
            java.util.function.Consumer<String> alCompletar,
            java.util.function.Consumer<String> alError
    ) {

        SwingWorker<String, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected String doInBackground()
                            throws Exception {

                        HttpRequest.Builder builder =
                                HttpRequest.newBuilder()
                                        .uri(URI.create(url));

                        if (metodo.equals("POST")) {

                            builder.POST(
                                    HttpRequest.BodyPublishers.noBody()
                            );

                        } else if (metodo.equals("DELETE")) {

                            builder.DELETE();
                        }

                        HttpResponse<String> response =
                                clienteHttp.send(
                                        builder.build(),
                                        HttpResponse.BodyHandlers.ofString()
                                );

                        if (response.statusCode() < 200 ||
                                response.statusCode() >= 300) {

                            throw new RuntimeException(
                                    response.body().isBlank()
                                            ? "Error HTTP: " + response.statusCode()
                                            : response.body()
                            );
                        }

                        return response.body();
                    }

                    @Override
                    protected void done() {

                        botonReservar.setEnabled(true);

                        try {

                            alCompletar.accept(get());

                        } catch (Exception e) {

                            Throwable causa = e.getCause();

                            if (causa != null &&
                                    causa.getMessage() != null) {

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
}

