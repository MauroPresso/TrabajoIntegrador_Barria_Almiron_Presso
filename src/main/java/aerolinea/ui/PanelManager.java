package aerolinea.ui;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.EnumPanel;
import aerolinea.dominio.Vuelo;
import aerolinea.servicio.Servicio;

import javax.swing.JPanel;
import java.awt.CardLayout;

/**
 * Administra las distintas vistas de la interfaz grÃ¡fica.
 *
 * <p>CardLayout mantiene varios JPanel dentro de un Ãºnico contenedor y
 * permite seleccionar cuÃ¡l se encuentra visible.</p>
 */
public class PanelManager extends JPanel {

    private final CardLayout cardLayout;
    private final Aerolinea aerolinea;
    private final Servicio<Vuelo> servicioVuelos;

    private final PanelPrincipal panelPrincipal;
    private final PanelSeccion panelVuelos;
    private final PanelSeccion panelPasajeros;
    private final PanelSeccion panelReservas;

    private EnumPanel panelActual;

    public PanelManager(Aerolinea aerolinea, Servicio<Vuelo> servicioVuelos) {
        if (aerolinea == null) {
            throw new IllegalArgumentException("La aerolÃ­nea no puede ser nula.");
        }

        if (servicioVuelos == null) {
            throw new IllegalArgumentException("El servicio de vuelos no puede ser nulo.");
        }

        this.aerolinea = aerolinea;
        this.servicioVuelos = servicioVuelos;

        this.cardLayout = new CardLayout();
        setLayout(cardLayout);

        this.panelPrincipal = new PanelPrincipal(this);

        this.panelVuelos = new PanelSeccion(
                "Vuelos",
                "AdministraciÃ³n de vuelos de la aerolÃ­nea.",
                this);

        this.panelPasajeros = new PanelSeccion(
                "Pasajeros",
                "AdministraciÃ³n de pasajeros registrados.",
                this);

        this.panelReservas = new PanelSeccion(
                "Reservas",
                "AdministraciÃ³n de reservas de vuelos.",
                this);

        add(panelPrincipal, EnumPanel.PRINCIPAL.name());
        add(panelVuelos, EnumPanel.VUELOS.name());
        add(panelPasajeros, EnumPanel.PASAJEROS.name());
        add(panelReservas, EnumPanel.RESERVAS.name());

        mostrarPanel(EnumPanel.PRINCIPAL);
    }

    public void mostrarPanel(EnumPanel panel) {
        if (panel == null) {
            throw new IllegalArgumentException("El panel no puede ser nulo.");
        }

        cardLayout.show(this, panel.name());
        panelActual = panel;
    }

    public EnumPanel getPanelActual() {
        return panelActual;
    }

    public PanelPrincipal getPanelPrincipal() {
        return panelPrincipal;
    }

    public Aerolinea getAerolinea() {
        return aerolinea;
    }

    public Servicio<Vuelo> getServicioVuelos() {
        return servicioVuelos;
    }
}