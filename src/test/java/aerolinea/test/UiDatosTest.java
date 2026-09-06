package aerolinea.test;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.Pasajero;
import aerolinea.dominio.Persona;
import aerolinea.dominio.Vuelo;
import aerolinea.dominio.VueloNacional;
import aerolinea.repositorio.RepositorioArchivo;
import aerolinea.servicio.Servicio;
import aerolinea.ui.PanelManager;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Prueba de integraciÃ³n entre dominio, TableModel y persistencia dual.
 */
public class UiDatosTest {

    public static void main(String[] args) throws Exception {

        Path vuelosPath =
                Path.of("target", "ui-datos-vuelos.dat");

        Path personasPath =
                Path.of("target", "ui-datos-personas.dat");

        Files.deleteIfExists(vuelosPath);
        Files.deleteIfExists(personasPath);

        Servicio<Vuelo> servicioVuelos =
                new Servicio<>(
                        new RepositorioArchivo<>(
                                vuelosPath.toString()));

        Servicio<Persona> servicioPersonas =
                new Servicio<>(
                        new RepositorioArchivo<>(
                                personasPath.toString()));

        Aerolinea aerolinea =
                new Aerolinea(
                        "AerolÃ­nea Test",
                        servicioVuelos.listar(),
                        servicioPersonas.listar());

        PanelManager manager =
                new PanelManager(
                        aerolinea,
                        servicioVuelos,
                        servicioPersonas);

        Vuelo vuelo =
                new VueloNacional(
                        "AR900",
                        "NeuquÃ©n",
                        "Buenos Aires",
                        "2027-01-10",
                        180,
                        "Buenos Aires");

        Pasajero pasajero =
                new Pasajero(
                        40111222,
                        "Mauro",
                        "Prueba",
                        "AR40111222");

        aerolinea.agregarVuelo(vuelo);

        if (!aerolinea.registrarPersona(pasajero)) {
            throw new IllegalStateException(
                    "No se pudo registrar el pasajero de prueba.");
        }

        manager.guardarEstado();
        manager.refrescarModelos();

        if (manager.getTableVuelosModel().getRowCount() != 1) {
            throw new IllegalStateException(
                    "TableVuelosModel no refleja el vuelo.");
        }

        if (manager.getTablePasajerosModel().getRowCount() != 1) {
            throw new IllegalStateException(
                    "TablePasajerosModel no refleja el pasajero.");
        }

        aerolinea.reservarVuelo(
                pasajero.getDni(),
                vuelo.getNumero());

        manager.guardarEstado();
        manager.refrescarModelos();

        Object ocupados =
                manager.getTableVuelosModel()
                        .getValueAt(0, 6);

        if (!Integer.valueOf(1).equals(ocupados)) {
            throw new IllegalStateException(
                    "La tabla no refleja el asiento ocupado.");
        }

        // Nueva carga desde disco: comprueba los dos repositorios.
        Servicio<Vuelo> vuelosRecargados =
                new Servicio<>(
                        new RepositorioArchivo<>(
                                vuelosPath.toString()));

        Servicio<Persona> personasRecargadas =
                new Servicio<>(
                        new RepositorioArchivo<>(
                                personasPath.toString()));

        Aerolinea recargada =
                new Aerolinea(
                        "AerolÃ­nea Recargada",
                        vuelosRecargados.listar(),
                        personasRecargadas.listar());

        Vuelo vueloRecargado =
                recargada.buscarVueloPorNumero("AR900");

        Persona personaRecargada =
                recargada.buscarPersonaPorDni(40111222);

        if (vueloRecargado == null
                || !(personaRecargada instanceof Pasajero)) {

            throw new IllegalStateException(
                    "La persistencia dual no recuperÃ³ los datos.");
        }

        Pasajero pasajeroRecargado =
                (Pasajero) personaRecargada;

        if (!pasajeroRecargado
                .tieneVueloReservado(vueloRecargado)) {

            throw new IllegalStateException(
                    "La reserva no se recuperÃ³ correctamente.");
        }

        Files.deleteIfExists(vuelosPath);
        Files.deleteIfExists(personasPath);

        System.out.println("TableVuelosModel: OK");
        System.out.println("TablePasajerosModel: OK");
        System.out.println("Servicio<Vuelo>: OK");
        System.out.println("Servicio<Persona>: OK");
        System.out.println("Reserva persistida: OK");
        System.out.println("UiDatosTest finalizado correctamente.");
    }
}