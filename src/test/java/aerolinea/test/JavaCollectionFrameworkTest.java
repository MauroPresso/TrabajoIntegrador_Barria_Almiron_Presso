package aerolinea.test;

import aerolinea.dominio.Pasajero;
import aerolinea.dominio.Persona;
import aerolinea.dominio.Tripulante;
import aerolinea.dominio.Vuelo;
import aerolinea.dominio.VueloNacional;
import aerolinea.util.ComparadorVueloPorDestino;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Laboratorio del Java Collections Framework aplicado al dominio aeronautico.
 */
public class JavaCollectionFrameworkTest {

    public static void main(String[] args) {

        Pasajero ana = new Pasajero(30111222, "Ana", "Ruiz", "AR30111222");
        Pasajero luis = new Pasajero(28444555, "Luis", "Perez", "");
        Tripulante juan = new Tripulante(25666777, "Juan", "Gonzalez", 1001, "Comandante");

        System.out.println("=== LIST ===");

        List<Persona> personas = new ArrayList<>();
        personas.add(ana);
        personas.add(juan);
        personas.add(luis);

        System.out.println("Cantidad: " + personas.size());

        for (int i = 0; i < personas.size(); i++) {
            System.out.println("Posicion " + i + ": " + personas.get(i).getNombreCompleto());
        }

        System.out.println();
        System.out.println("=== SET + equals/hashCode ===");

        Set<Persona> personasUnicas = new HashSet<>();
        personasUnicas.add(ana);
        personasUnicas.add(luis);
        personasUnicas.add(juan);

        // Mismo DNI que Ana: Persona.equals/hashCode lo considera la misma identidad logica.
        personasUnicas.add(new Pasajero(30111222, "Ana Maria", "Ruiz", "OTRO"));

        System.out.println("Elementos intentados: 4");
        System.out.println("Elementos unicos: " + personasUnicas.size());

        System.out.println();
        System.out.println("=== MAP ===");

        Map<Integer, Persona> personasPorDni = new HashMap<>();

        for (Persona persona : personas) {
            personasPorDni.put(persona.getDni(), persona);
        }

        System.out.println("DNI 28444555 -> "
                + personasPorDni.get(28444555).getNombreCompleto());

        System.out.println();
        System.out.println("=== ITERATOR ===");

        Iterator<Persona> iterator = personas.iterator();

        while (iterator.hasNext()) {
            Persona persona = iterator.next();
            System.out.println("Iterator: " + persona.getNombreCompleto());
        }

        System.out.println();
        System.out.println("=== COMPARABLE ===");

        Collections.sort(personas);

        for (Persona persona : personas) {
            System.out.println(persona.getNombreCompleto());
        }

        System.out.println();
        System.out.println("=== COMPARATOR + VUELOS ===");

        List<Vuelo> vuelos = new ArrayList<>();

        vuelos.add(new VueloNacional(
                "AR300", "Neuquen", "Cordoba", "2026-11-01", 150, "Cordoba"));

        vuelos.add(new VueloNacional(
                "AR100", "Neuquen", "Buenos Aires", "2026-11-01", 180, "Buenos Aires"));

        vuelos.add(new VueloNacional(
                "AR200", "Neuquen", "Bariloche", "2026-11-01", 120, "Rio Negro"));

        // Orden natural: compareTo() de Vuelo -> numero.
        Collections.sort(vuelos);

        System.out.println("Orden natural por numero:");
        vuelos.forEach(v -> System.out.println(v.getNumero()));

        // Otro criterio: Comparator externo -> destino.
        Collections.sort(vuelos, new ComparadorVueloPorDestino());

        System.out.println("Orden por destino:");
        vuelos.forEach(v -> System.out.println(v.getDestino()));

        // Comparator mediante lambda.
        personas.sort((p1, p2) -> Integer.compare(p1.getDni(), p2.getDni()));

        System.out.println("Personas por DNI mediante lambda:");
        personas.forEach(p -> System.out.println(p.getDni()));

        System.out.println("JavaCollectionFrameworkTest finalizado correctamente.");
    }
}
