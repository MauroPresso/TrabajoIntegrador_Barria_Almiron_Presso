package aerolinea.ui;

import aerolinea.dominio.EnumPanel;
import aerolinea.dominio.Pasajero;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;

/**
 * Formulario Swing para registrar pasajeros.
 */
public class PanelPasajerosFormulario extends JPanel {

    private final PanelManager manager;

    private final JTextField campoDni;
    private final JTextField campoNombre;
    private final JTextField campoApellido;
    private final JTextField campoPasaporte;

    public PanelPasajerosFormulario(PanelManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException(
                    "El PanelManager no puede ser nulo.");
        }

        this.manager = manager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(
                30, 30, 30, 30));

        JPanel formulario =
                new JPanel(new GridLayout(4, 2, 10, 10));

        campoDni = new JTextField();
        campoNombre = new JTextField();
        campoApellido = new JTextField();
        campoPasaporte = new JTextField();

        formulario.add(new JLabel("DNI:"));
        formulario.add(campoDni);

        formulario.add(new JLabel("Nombre:"));
        formulario.add(campoNombre);

        formulario.add(new JLabel("Apellido:"));
        formulario.add(campoApellido);

        formulario.add(new JLabel("Pasaporte (opcional):"));
        formulario.add(campoPasaporte);

        JPanel botones =
                new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton botonGuardar = new JButton("Guardar");
        JButton botonVerLista = new JButton("Ver pasajeros");
        JButton botonVolver = new JButton("Principal");

        botones.add(botonGuardar);
        botones.add(botonVerLista);
        botones.add(botonVolver);

        botonGuardar.addActionListener(
                evento -> guardarPasajero());

        botonVerLista.addActionListener(
                evento -> manager.mostrarPanel(
                        EnumPanel.TABLA_PASAJEROS));

        botonVolver.addActionListener(
                evento -> manager.mostrarPanel(
                        EnumPanel.PRINCIPAL));

        add(formulario, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void guardarPasajero() {
        try {
            int dni = Integer.parseInt(
                    textoObligatorio(campoDni, "DNI"));

            Pasajero pasajero = new Pasajero(
                    dni,
                    textoObligatorio(campoNombre, "nombre"),
                    textoObligatorio(campoApellido, "apellido"),
                    campoPasaporte.getText().trim());

            boolean agregado =
                    manager.getAerolinea().registrarPersona(pasajero);

            if (!agregado) {
                throw new IllegalArgumentException(
                        "Ya existe una persona con ese DNI.");
            }

            manager.guardarEstado();
            manager.refrescarModelos();

            JOptionPane.showMessageDialog(
                    this,
                    "Pasajero registrado correctamente.");

            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "El DNI debe ser numerico.",
                    "Dato invalido",
                    JOptionPane.ERROR_MESSAGE);

        } catch (IllegalArgumentException | IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "No se pudo registrar el pasajero",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String textoObligatorio(
            JTextField campo,
            String nombreCampo) {

        String valor = campo.getText().trim();

        if (valor.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe completar el campo " + nombreCampo + ".");
        }

        return valor;
    }

    private void limpiarCampos() {
        campoDni.setText("");
        campoNombre.setText("");
        campoApellido.setText("");
        campoPasaporte.setText("");
    }
}
