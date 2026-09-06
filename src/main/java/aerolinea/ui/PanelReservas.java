/**
 * @file PanelReservas.java
 * @brief Declares PanelReservas as part of the Swing or console user interface.
 * @details This source file belongs to the Programacion II academic project.
 */

package aerolinea.ui;

import aerolinea.dominio.EnumPanel;
import aerolinea.dominio.Pasajero;
import aerolinea.dominio.Persona;
import aerolinea.dominio.Vuelo;
import aerolinea.dominio.VueloInternacional;
import aerolinea.excepcion.VueloNoDisponibleException;

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
 * Panel analogo a la operacion "Prestar" de Biblioteca:
 * vincula un Pasajero con un Vuelo mediante una reserva.
 */
public class PanelReservas extends JPanel {

    private final PanelManager manager;
    private final JTextField campoDni;
    private final JTextField campoVuelo;

    public PanelReservas(PanelManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException(
                    "El PanelManager no puede ser nulo.");
        }

        this.manager = manager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(
                40, 40, 40, 40));

        JPanel formulario =
                new JPanel(new GridLayout(2, 2, 10, 10));

        campoDni = new JTextField();
        campoVuelo = new JTextField();

        formulario.add(new JLabel("DNI del pasajero:"));
        formulario.add(campoDni);

        formulario.add(new JLabel("Numero de vuelo:"));
        formulario.add(campoVuelo);

        JPanel botones =
                new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton botonReservar = new JButton("Reservar");
        JButton botonCancelar = new JButton("Cancelar reserva");
        JButton botonVolver = new JButton("Principal");

        botones.add(botonReservar);
        botones.add(botonCancelar);
        botones.add(botonVolver);

        botonReservar.addActionListener(
                evento -> reservar());

        botonCancelar.addActionListener(
                evento -> cancelar());

        botonVolver.addActionListener(
                evento -> manager.mostrarPanel(
                        EnumPanel.PRINCIPAL));

        add(formulario, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void reservar() {
        try {
            DatosReserva datos = obtenerDatos();

            if (datos.pasajero.tieneVueloReservado(datos.vuelo)) {
                throw new IllegalArgumentException(
                        "El pasajero ya tiene una reserva en ese vuelo.");
            }

            if (datos.vuelo instanceof VueloInternacional) {
                VueloInternacional internacional =
                        (VueloInternacional) datos.vuelo;

                if (internacional.isRequierePasaporte()
                        && datos.pasajero.getNumeroPasaporte().isEmpty()) {

                    throw new IllegalArgumentException(
                            "El vuelo requiere pasaporte y el pasajero no tiene uno registrado.");
                }
            }

            manager.getAerolinea().reservarVuelo(
                    datos.pasajero.getDni(),
                    datos.vuelo.getNumero());

            manager.guardarEstado();
            manager.refrescarModelos();

            JOptionPane.showMessageDialog(
                    this,
                    "Reserva realizada correctamente.");

        } catch (NumberFormatException e) {
            mostrarError("El DNI debe ser numerico.");

        } catch (VueloNoDisponibleException
                 | IllegalArgumentException
                 | IOException e) {

            mostrarError(e.getMessage());
        }
    }

    private void cancelar() {
        try {
            DatosReserva datos = obtenerDatos();

            if (!datos.pasajero.tieneVueloReservado(datos.vuelo)) {
                throw new IllegalArgumentException(
                        "El pasajero no tiene una reserva en ese vuelo.");
            }

            manager.getAerolinea().cancelarReserva(
                    datos.pasajero.getDni(),
                    datos.vuelo.getNumero());

            manager.guardarEstado();
            manager.refrescarModelos();

            JOptionPane.showMessageDialog(
                    this,
                    "Reserva cancelada correctamente.");

        } catch (NumberFormatException e) {
            mostrarError("El DNI debe ser numerico.");

        } catch (IllegalArgumentException | IOException e) {
            mostrarError(e.getMessage());
        }
    }

    private DatosReserva obtenerDatos() {
        int dni = Integer.parseInt(campoDni.getText().trim());
        String numeroVuelo = campoVuelo.getText().trim();

        if (numeroVuelo.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe ingresar el numero de vuelo.");
        }

        Persona persona =
                manager.getAerolinea().buscarPersonaPorDni(dni);

        if (!(persona instanceof Pasajero)) {
            throw new IllegalArgumentException(
                    "No existe un pasajero con ese DNI.");
        }

        Vuelo vuelo =
                manager.getAerolinea().buscarVueloPorNumero(
                        numeroVuelo);

        if (vuelo == null) {
            throw new IllegalArgumentException(
                    "No existe el vuelo " + numeroVuelo + ".");
        }

        return new DatosReserva(
                (Pasajero) persona,
                vuelo);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Operacion no realizada",
                JOptionPane.ERROR_MESSAGE);
    }

    private static class DatosReserva {
        private final Pasajero pasajero;
        private final Vuelo vuelo;

        private DatosReserva(
                Pasajero pasajero,
                Vuelo vuelo) {

            this.pasajero = pasajero;
            this.vuelo = vuelo;
        }
    }
}
