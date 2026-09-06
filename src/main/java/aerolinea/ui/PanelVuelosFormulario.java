package aerolinea.ui;

import aerolinea.dominio.EnumPanel;
import aerolinea.dominio.Vuelo;
import aerolinea.dominio.VueloCharter;
import aerolinea.dominio.VueloInternacional;
import aerolinea.dominio.VueloNacional;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Formulario Swing para crear vuelos nacionales, internacionales o charter.
 */
public class PanelVuelosFormulario extends JPanel {

    private final PanelManager manager;

    private final JRadioButton radioNacional;
    private final JRadioButton radioInternacional;
    private final JRadioButton radioCharter;

    private final JTextField campoNumero;
    private final JTextField campoOrigen;
    private final JTextField campoDestino;
    private final JTextField campoFecha;
    private final JTextField campoCapacidad;

    private final JTextField campoProvincia;
    private final JTextField campoPais;
    private final JCheckBox checkPasaporte;
    private final JTextField campoEmpresa;
    private final JTextField campoCosto;

    public PanelVuelosFormulario(PanelManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("El PanelManager no puede ser nulo.");
        }

        this.manager = manager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelTipos = new JPanel(new FlowLayout(FlowLayout.LEFT));

        radioNacional = new JRadioButton("Nacional", true);
        radioInternacional = new JRadioButton("Internacional");
        radioCharter = new JRadioButton("Charter");

        ButtonGroup grupoTipos = new ButtonGroup();
        grupoTipos.add(radioNacional);
        grupoTipos.add(radioInternacional);
        grupoTipos.add(radioCharter);

        panelTipos.add(new JLabel("Tipo:"));
        panelTipos.add(radioNacional);
        panelTipos.add(radioInternacional);
        panelTipos.add(radioCharter);

        JPanel formulario = new JPanel(new GridLayout(10, 2, 8, 8));

        campoNumero = new JTextField();
        campoOrigen = new JTextField();
        campoDestino = new JTextField();
        campoFecha = new JTextField();
        campoCapacidad = new JTextField();

        campoProvincia = new JTextField();
        campoPais = new JTextField();
        checkPasaporte = new JCheckBox("Requiere pasaporte");
        campoEmpresa = new JTextField();
        campoCosto = new JTextField();

        formulario.add(new JLabel("NÃºmero de vuelo:"));
        formulario.add(campoNumero);

        formulario.add(new JLabel("Origen:"));
        formulario.add(campoOrigen);

        formulario.add(new JLabel("Destino:"));
        formulario.add(campoDestino);

        formulario.add(new JLabel("Fecha (yyyy-MM-dd):"));
        formulario.add(campoFecha);

        formulario.add(new JLabel("Capacidad:"));
        formulario.add(campoCapacidad);

        formulario.add(new JLabel("Provincia (Nacional):"));
        formulario.add(campoProvincia);

        formulario.add(new JLabel("PaÃ­s (Internacional):"));
        formulario.add(campoPais);

        formulario.add(new JLabel("Pasaporte (Internacional):"));
        formulario.add(checkPasaporte);

        formulario.add(new JLabel("Empresa (Charter):"));
        formulario.add(campoEmpresa);

        formulario.add(new JLabel("Costo total (Charter):"));
        formulario.add(campoCosto);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botonGuardar = new JButton("Guardar");
        JButton botonVerLista = new JButton("Ver vuelos");
        JButton botonVolver = new JButton("Principal");

        botones.add(botonGuardar);
        botones.add(botonVerLista);
        botones.add(botonVolver);

        botonGuardar.addActionListener(evento -> guardarVuelo());

        botonVerLista.addActionListener(
                evento -> manager.mostrarPanel(EnumPanel.TABLA_VUELOS));

        botonVolver.addActionListener(
                evento -> manager.mostrarPanel(EnumPanel.PRINCIPAL));

        add(panelTipos, BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void guardarVuelo() {
        try {
            String numero = textoObligatorio(campoNumero, "nÃºmero");
            String origen = textoObligatorio(campoOrigen, "origen");
            String destino = textoObligatorio(campoDestino, "destino");
            String fecha = textoObligatorio(campoFecha, "fecha");

            // Valida que el texto corresponda a una fecha ISO real.
            LocalDate.parse(fecha);

            int capacidad = Integer.parseInt(
                    textoObligatorio(campoCapacidad, "capacidad"));

            if (capacidad <= 0) {
                throw new IllegalArgumentException(
                        "La capacidad debe ser mayor que cero.");
            }

            Vuelo vuelo = crearVuelo(
                    numero, origen, destino, fecha, capacidad);

            manager.getAerolinea().agregarVuelo(vuelo);
            manager.guardarEstado();
            manager.refrescarModelos();

            JOptionPane.showMessageDialog(
                    this,
                    "Vuelo guardado correctamente.");

            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Capacidad y costo deben contener valores numÃ©ricos vÃ¡lidos.",
                    "Dato invÃ¡lido",
                    JOptionPane.ERROR_MESSAGE);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "La fecha debe tener formato yyyy-MM-dd y ser una fecha vÃ¡lida.",
                    "Fecha invÃ¡lida",
                    JOptionPane.ERROR_MESSAGE);

        } catch (IllegalArgumentException | IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "No se pudo guardar el vuelo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Vuelo crearVuelo(String numero,
                             String origen,
                             String destino,
                             String fecha,
                             int capacidad) {

        if (radioInternacional.isSelected()) {
            String pais = textoObligatorio(campoPais, "paÃ­s");

            return new VueloInternacional(
                    numero,
                    origen,
                    destino,
                    fecha,
                    capacidad,
                    pais,
                    checkPasaporte.isSelected());
        }

        if (radioCharter.isSelected()) {
            String empresa = textoObligatorio(campoEmpresa, "empresa");
            double costo = Double.parseDouble(
                    textoObligatorio(campoCosto, "costo total"));

            return new VueloCharter(
                    numero,
                    origen,
                    destino,
                    fecha,
                    capacidad,
                    empresa,
                    costo);
        }

        String provincia = textoObligatorio(
                campoProvincia,
                "provincia");

        return new VueloNacional(
                numero,
                origen,
                destino,
                fecha,
                capacidad,
                provincia);
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
        campoNumero.setText("");
        campoOrigen.setText("");
        campoDestino.setText("");
        campoFecha.setText("");
        campoCapacidad.setText("");
        campoProvincia.setText("");
        campoPais.setText("");
        checkPasaporte.setSelected(false);
        campoEmpresa.setText("");
        campoCosto.setText("");
        radioNacional.setSelected(true);
    }
}