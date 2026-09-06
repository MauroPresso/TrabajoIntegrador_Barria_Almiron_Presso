package aerolinea.ui;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.EnumPanel;
import aerolinea.dominio.Persona;
import aerolinea.dominio.Vuelo;
import aerolinea.servicio.Servicio;

import javax.swing.JPanel;
import java.awt.CardLayout;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Coordina las vistas Swing y los servicios persistentes.
 */
public class PanelManager extends JPanel {

    private final CardLayout cardLayout;

    private final Aerolinea aerolinea;
    private final Servicio<Vuelo> servicioVuelos;
    private final Servicio<Persona> servicioPersonas;

    private final TableVuelosModel tableVuelosModel;
    private final TablePasajerosModel tablePasajerosModel;

    private final PanelPrincipal panelPrincipal;
    private final PanelVuelosFormulario panelVuelosFormulario;
    private final PanelVuelosTabla panelVuelosTabla;
    private final PanelPasajerosFormulario panelPasajerosFormulario;
    private final PanelPasajerosTabla panelPasajerosTabla;
    private final PanelReservas panelReservas;

    private EnumPanel panelActual;

    public PanelManager(
            Aerolinea aerolinea,
            Servicio<Vuelo> servicioVuelos,
            Servicio<Persona> servicioPersonas) {

        if (aerolinea == null
                || servicioVuelos == null
                || servicioPersonas == null) {

            throw new IllegalArgumentException(
                    "Dominio y servicios no pueden ser nulos.");
        }

        this.aerolinea = aerolinea;
        this.servicioVuelos = servicioVuelos;
        this.servicioPersonas = servicioPersonas;

        this.cardLayout = new CardLayout();
        setLayout(cardLayout);

        this.tableVuelosModel =
                new TableVuelosModel(aerolinea);

        this.tablePasajerosModel =
                new TablePasajerosModel(aerolinea);

        this.panelPrincipal =
                new PanelPrincipal(this);

        this.panelVuelosFormulario =
                new PanelVuelosFormulario(this);

        this.panelVuelosTabla =
                new PanelVuelosTabla(
                        tableVuelosModel,
                        this);

        this.panelPasajerosFormulario =
                new PanelPasajerosFormulario(this);

        this.panelPasajerosTabla =
                new PanelPasajerosTabla(
                        tablePasajerosModel,
                        this);

        this.panelReservas =
                new PanelReservas(this);

        add(
                panelPrincipal,
                EnumPanel.PRINCIPAL.name());

        add(
                panelVuelosFormulario,
                EnumPanel.FORMULARIO_VUELO.name());

        add(
                panelVuelosTabla,
                EnumPanel.TABLA_VUELOS.name());

        add(
                panelPasajerosFormulario,
                EnumPanel.FORMULARIO_PASAJERO.name());

        add(
                panelPasajerosTabla,
                EnumPanel.TABLA_PASAJEROS.name());

        add(
                panelReservas,
                EnumPanel.RESERVAS.name());

        mostrarPanel(EnumPanel.PRINCIPAL);
    }

    public void mostrarPanel(EnumPanel panel) {
        if (panel == null) {
            throw new IllegalArgumentException(
                    "El panel no puede ser nulo.");
        }

        refrescarModelos();
        cardLayout.show(this, panel.name());
        panelActual = panel;
    }

    /**
     * Sincroniza el estado del dominio con ambos servicios.
     */
    public void guardarEstado() throws IOException {
        servicioVuelos.reemplazarTodos(
                aerolinea.getVuelos());

        servicioVuelos.guardar();

        servicioPersonas.reemplazarTodos(
                new ArrayList<>(
                        aerolinea
                                .getPersonasPorDni()
                                .values()));

        servicioPersonas.guardar();
    }

    public void refrescarModelos() {
        tableVuelosModel.refrescar();
        tablePasajerosModel.refrescar();
    }

    public EnumPanel getPanelActual() {
        return panelActual;
    }

    public Aerolinea getAerolinea() {
        return aerolinea;
    }

    public Servicio<Vuelo> getServicioVuelos() {
        return servicioVuelos;
    }

    public Servicio<Persona> getServicioPersonas() {
        return servicioPersonas;
    }

    public TableVuelosModel getTableVuelosModel() {
        return tableVuelosModel;
    }

    public TablePasajerosModel getTablePasajerosModel() {
        return tablePasajerosModel;
    }

    public PanelPrincipal getPanelPrincipal() {
        return panelPrincipal;
    }
}