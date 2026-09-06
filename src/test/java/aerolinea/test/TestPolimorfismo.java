package aerolinea.test;

import aerolinea.dominio.IOperable;
import aerolinea.dominio.Vuelo;
import aerolinea.dominio.VueloCharter;
import aerolinea.dominio.VueloInternacional;
import aerolinea.dominio.VueloNacional;

/**
 * Laboratorio de polimorfismo anÃ¡logo a TestPolimorfismo de Biblioteca.
 */
public class TestPolimorfismo {

    public static void main(String[] args) {

        System.out.println("=== POLIMORFISMO CON CLASE ABSTRACTA ===");

        Vuelo[] vuelos = new Vuelo[3];

        vuelos[0] = new VueloNacional(
                "AR100", "NeuquÃ©n", "Buenos Aires", "2026-10-10", 180, "Buenos Aires");

        vuelos[1] = new VueloInternacional(
                "AR200", "Buenos Aires", "Santiago", "2026-10-11", 220, "Chile", true);

        vuelos[2] = new VueloCharter(
                "CH300", "NeuquÃ©n", "Mendoza", "2026-10-12", 80, "Empresa Andina", 9500000.0);

        // Misma referencia base (Vuelo), distinto comportamiento segÃºn el objeto real.
        for (Vuelo vuelo : vuelos) {
            vuelo.mostrarInfo();
        }

        System.out.println();
        System.out.println("=== INTERFAZ + POLIMORFISMO ===");

        IOperable[] operaciones = new IOperable[3];
        operaciones[0] = vuelos[0];
        operaciones[1] = vuelos[1];
        operaciones[2] = vuelos[2];

        // Distintos subtipos, mismo contrato de comportamiento.
        for (IOperable operacion : operaciones) {
            operacion.embarcar();
        }

        System.out.println();
        System.out.println("=== INSTANCEOF + CASTING ===");

        Vuelo posibleInternacional = vuelos[1];

        if (posibleInternacional instanceof VueloInternacional) {
            VueloInternacional internacional = (VueloInternacional) posibleInternacional;

            System.out.println("PaÃ­s destino: " + internacional.getPaisDestino());
            System.out.println("Requiere pasaporte: " + internacional.isRequierePasaporte());
        }

        System.out.println("TestPolimorfismo finalizado correctamente.");
    }
}