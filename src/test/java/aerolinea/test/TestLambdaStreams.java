package aerolinea.test;

import aerolinea.dominio.Vuelo;
import aerolinea.dominio.VueloCharter;
import aerolinea.dominio.VueloInternacional;
import aerolinea.dominio.VueloNacional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Laboratorio de interfaces funcionales, lambdas y Stream API.
 */
public class TestLambdaStreams {

    public static void main(String[] args) {

        List<Vuelo> vuelos = new ArrayList<>();

        vuelos.add(new VueloNacional(
                "AR101", "Neuquen", "Buenos Aires", "2026-12-01", 180, "Buenos Aires"));

        vuelos.add(new VueloInternacional(
                "AR202", "Buenos Aires", "Madrid", "2026-12-02", 260, "Espana", true));

        vuelos.add(new VueloCharter(
                "CH303", "Neuquen", "Mendoza", "2026-12-03", 70, "Patagonia Energy", 7800000.0));

        System.out.println("=== PREDICATE ===");

        Predicate<Vuelo> capacidadMayorA100 =
                vuelo -> vuelo.getCapacidad() > 100;

        System.out.println("AR101 capacidad > 100: "
                + capacidadMayorA100.test(vuelos.get(0)));

        System.out.println();
        System.out.println("=== CONSUMER ===");

        Consumer<Vuelo> mostrarResumen =
                vuelo -> System.out.println(
                        vuelo.getNumero() + " -> " + vuelo.getDestino());

        mostrarResumen.accept(vuelos.get(1));

        System.out.println();
        System.out.println("=== FUNCTION ===");

        Function<Vuelo, String> obtenerDestino =
                Vuelo::getDestino;

        System.out.println("Destino transformado: "
                + obtenerDestino.apply(vuelos.get(2)));

        System.out.println();
        System.out.println("=== SUPPLIER ===");

        Supplier<VueloNacional> crearVuelo =
                () -> new VueloNacional(
                        "AR404", "Neuquen", "Ushuaia", "2026-12-04", 140, "Tierra del Fuego");

        VueloNacional vueloCreado = crearVuelo.get();
        System.out.println("Supplier creo: " + vueloCreado.getNumero());

        System.out.println();
        System.out.println("=== STREAM API ===");

        vuelos.stream()
                .filter(capacidadMayorA100)
                .map(Vuelo::getDestino)
                .forEach(System.out::println);

        long cantidadInternacionales = vuelos.stream()
                .filter(vuelo -> vuelo instanceof VueloInternacional)
                .count();

        System.out.println("Vuelos internacionales: " + cantidadInternacionales);

        List<String> destinosOrdenados = vuelos.stream()
                .map(Vuelo::getDestino)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());

        System.out.println("Destinos ordenados: " + destinosOrdenados);

        int capacidadTotal = vuelos.stream()
                .mapToInt(Vuelo::getCapacidad)
                .sum();

        System.out.println("Capacidad total: " + capacidadTotal);

        System.out.println("TestLambdaStreams finalizado correctamente.");
    }
}
