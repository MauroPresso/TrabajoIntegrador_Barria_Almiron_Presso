package aerolinea.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import aerolinea.excepcion.VueloNoDisponibleException;
import aerolinea.util.ComparadorVueloPorDestino;
import aerolinea.util.ComparadorVueloPorNumero;

/**
 * @file Aerolinea.java
 * @brief Objeto principal del dominio del sistema de aerolÃ­nea.
 */

/**
 * @class Aerolinea
 * @brief Gestiona vuelos, pasajeros y tripulantes como reglas del dominio.
 *
 * <p>Esta clase no conoce archivos, repositorios ni mecanismos de
 * persistencia. Su responsabilidad es representar y operar el estado de una
 * aerolÃ­nea mediante objetos y colecciones Java.</p>
 *
 * <p>Conceptos demostrados:</p>
 * <ul>
 *   <li>List y ArrayList para vuelos.</li>
 *   <li>HashMap para indexar personas por DNI.</li>
 *   <li>HashSet para evitar duplicados de pasajeros con reserva activa.</li>
 *   <li>Comparable, Comparator y Collections.sort().</li>
 *   <li>Streams, lambdas y referencias a mÃ©todos.</li>
 *   <li>Herencia, polimorfismo y excepciones de negocio.</li>
 * </ul>
 */
public class Aerolinea {

    /** Nombre comercial de la aerolÃ­nea. */
    private final String nombre;

    /** Vuelos administrados por el dominio. */
    private final List<Vuelo> vuelos;

    /** Personas indexadas por DNI. */
    private final HashMap<Integer, Persona> personasPorDni;

    /** Pasajeros que poseen al menos una reserva activa. */
    private final HashSet<Persona> pasajerosConReservaActiva;

    /**
     * Crea una aerolÃ­nea vacÃ­a, completamente en memoria.
     *
     * @param nombre nombre comercial de la aerolÃ­nea
     */
    public Aerolinea(String nombre) {
        this(nombre, new ArrayList<>());
    }

    /**
     * Crea una aerolÃ­nea a partir de una colecciÃ³n inicial de vuelos.
     *
     * <p>La colecciÃ³n recibida se copia. De este modo el objeto de dominio
     * conserva su propio estado y no queda acoplado a la colecciÃ³n interna de
     * un servicio o repositorio.</p>
     *
     * @param nombre nombre comercial de la aerolÃ­nea
     * @param vuelosIniciales vuelos con los que se inicializa el dominio
     */
    public Aerolinea(String nombre, List<Vuelo> vuelosIniciales) {
        this.nombre = validarTextoObligatorio(nombre, "nombre de la aerolÃ­nea");

        if (vuelosIniciales == null) {
            throw new IllegalArgumentException("La lista inicial de vuelos no puede ser nula.");
        }

        this.vuelos = new ArrayList<>(vuelosIniciales);
        this.personasPorDni = new HashMap<>();
        this.pasajerosConReservaActiva = new HashSet<>();

        reconstruirPersonasDesdeVuelos();
    }
    /**
     * @brief Reconstruye las colecciones auxiliares de personas a partir de los vuelos del dominio.
     *
     * Los pasajeros y tripulantes asociados a cada vuelo forman parte del mismo
     * grafo de objetos del dominio. A partir de ellos se reconstruyen los Ã­ndices
     * auxiliares utilizados por la aerolÃ­nea.
     */
    private void reconstruirPersonasDesdeVuelos() {
        personasPorDni.clear();
        pasajerosConReservaActiva.clear();

        for (Vuelo vuelo : vuelos) {
            for (Pasajero pasajero : vuelo.getPasajeros()) {
                registrarPersonaAsociada(pasajero);

                if (pasajero.tieneReservaActiva()) {
                    pasajerosConReservaActiva.add(pasajero);
                }
            }

            for (Tripulante tripulante : vuelo.getTripulacion()) {
                registrarPersonaAsociada(tripulante);
            }
        }
    }

    /**
     * @brief Registra una persona asociada a los vuelos iniciales.
     *
     * Si ya existe una persona con el mismo DNI, se conserva la primera
     * encontrada para evitar duplicados.
     *
     * @param persona Persona asociada a alguno de los vuelos del dominio.
     */
    private void registrarPersonaAsociada(Persona persona) {
        if (persona != null && !personasPorDni.containsKey(persona.getDni())) {
            personasPorDni.put(persona.getDni(), persona);
        }
    }

    /**
     * @brief Obtiene el nombre comercial de la aerolínea.
     *
     * @return Nombre de la aerolínea.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @brief Agrega un vuelo a la aerolínea.
     *
     * @param vuelo Vuelo a agregar.
     * @throws IllegalArgumentException Si el vuelo es nulo o ya existe otro vuelo con el mismo número.
     */
    public void agregarVuelo(Vuelo vuelo) {
        if (vuelo == null) {
            throw new IllegalArgumentException("El vuelo no puede ser nulo.");
        }

        if (buscarVueloPorNumero(vuelo.getNumero()) != null) {
            throw new IllegalArgumentException("Ya existe un vuelo con el número " + vuelo.getNumero() + ".");
        }

        vuelos.add(vuelo);
    }

    /**
     * @brief Registra una persona en el sistema usando su DNI como clave.
     *
     * Se utiliza HashMap<Integer, Persona> para acceder rápidamente a pasajeros
     * o tripulantes a partir de su DNI.
     *
     * @param persona Persona a registrar.
     * @return true si se registró correctamente, false si el DNI ya existía.
     * @throws IllegalArgumentException Si la persona es nula.
     */
    public boolean registrarPersona(Persona persona) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula.");
        }

        if (personasPorDni.containsKey(persona.getDni())) {
            return false;
        }

        personasPorDni.put(persona.getDni(), persona);
        return true;
    }

    /**
     * @brief Busca una persona por DNI.
     *
     * @param dni DNI buscado.
     * @return Persona encontrada o null si no existe.
     */
    public Persona buscarPersonaPorDni(int dni) {
        return personasPorDni.get(dni);
    }

    /**
     * @brief Busca un vuelo por número usando Stream.
     *
     * El número de vuelo se maneja como String porque en el modelo actual
     * existen códigos como AR100, AR200 o CH300.
     *
     * Este método utiliza programación funcional mediante:
     * - stream()
     * - filter()
     * - findFirst()
     *
     * @param numeroVuelo Número o código del vuelo.
     * @return Vuelo encontrado o null si no existe.
     */
    public Vuelo buscarVueloPorNumero(String numeroVuelo) {
        if (numeroVuelo == null) {
            return null;
        }

        return vuelos.stream()
                .filter(vuelo -> vuelo.getNumero().equalsIgnoreCase(numeroVuelo.trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * @brief Reserva un vuelo para un pasajero.
     *
     * Si la reserva se realiza correctamente, el pasajero se agrega al HashSet
     * de pasajeros con reserva activa. Al ser HashSet, se evitan duplicados
     * automáticamente gracias a equals() y hashCode() de Persona.
     *
     * @param dniPasajero DNI del pasajero.
     * @param numeroVuelo Número o código del vuelo.
     * @throws VueloNoDisponibleException Si el vuelo no está disponible.
     */
    public void reservarVuelo(int dniPasajero, String numeroVuelo) throws VueloNoDisponibleException {
        Persona persona = personasPorDni.get(dniPasajero);

        if (persona == null) {
            System.out.println("No existe una persona registrada con DNI " + dniPasajero);
            return;
        }

        if (!(persona instanceof Pasajero)) {
            System.out.println("La persona con DNI " + dniPasajero + " no es pasajero.");
            return;
        }

        Vuelo vuelo = buscarVueloPorNumero(numeroVuelo);

        if (vuelo == null) {
            System.out.println("No existe el vuelo número " + numeroVuelo);
            return;
        }

        Pasajero pasajero = (Pasajero) persona;
        pasajero.reservarVuelo(vuelo);

        if (pasajero.tieneReservaActiva()) {
            pasajerosConReservaActiva.add(pasajero);
        }
    }

    /**
     * @brief Cancela una reserva de un pasajero.
     *
     * Si luego de cancelar el pasajero no posee más reservas activas, se lo
     * elimina del HashSet correspondiente.
     *
     * @param dniPasajero DNI del pasajero.
     * @param numeroVuelo Número o código del vuelo.
     */
    public void cancelarReserva(int dniPasajero, String numeroVuelo) {
        Persona persona = personasPorDni.get(dniPasajero);

        if (!(persona instanceof Pasajero)) {
            System.out.println("No existe un pasajero con DNI " + dniPasajero);
            return;
        }

        Vuelo vuelo = buscarVueloPorNumero(numeroVuelo);

        if (vuelo == null) {
            System.out.println("No existe el vuelo número " + numeroVuelo);
            return;
        }

        Pasajero pasajero = (Pasajero) persona;
        boolean cancelada = vuelo.cancelarReserva(pasajero);

        if (cancelada) {
            System.out.println("Reserva cancelada correctamente.");

            if (!pasajero.tieneReservaActiva()) {
                pasajerosConReservaActiva.remove(pasajero);
            }
        } else {
            System.out.println("El pasajero no tenía reservado ese vuelo.");
        }
    }

    /**
     * @brief Obtiene una copia de la lista de vuelos.
     *
     * @return Copia de los vuelos.
     */
    public ArrayList<Vuelo> getVuelos() {
        return new ArrayList<>(vuelos);
    }

    /**
     * @brief Obtiene una copia del mapa de personas.
     *
     * @return Copia del HashMap de personas.
     */
    public HashMap<Integer, Persona> getPersonasPorDni() {
        return new HashMap<>(personasPorDni);
    }

    /**
     * @brief Obtiene una copia del conjunto de pasajeros con reserva activa.
     *
     * @return Copia del HashSet de pasajeros con reserva activa.
     */
    public HashSet<Persona> getPasajerosConReservaActiva() {
        return new HashSet<>(pasajerosConReservaActiva);
    }

    /**
     * @brief Obtiene una nueva lista con los vuelos programados.
     *
     * Este método filtra la lista interna de vuelos y retorna una nueva lista
     * que contiene únicamente aquellos cuyo estado es PROGRAMADO.
     *
     * Se utiliza programación funcional mediante:
     * - stream()
     * - filter()
     * - collect(Collectors.toList())
     *
     * @return Lista nueva con los vuelos programados.
     */
    public List<Vuelo> obtenerVuelosProgramadosStream() {
        return vuelos.stream()
                .filter(vuelo -> vuelo.getEstado() == EstadoVuelo.PROGRAMADO)
                .collect(Collectors.toList());
    }

    /**
     * @brief Muestra los vuelos programados usando referencia a método.
     *
     * Este método reutiliza el filtrado de vuelos programados y muestra cada
     * vuelo por consola mediante una referencia a método.
     *
     * Se utiliza:
     * - forEach()
     * - referencia a método: Vuelo::mostrarInfo
     */
    public void mostrarVuelosProgramadosStream() {
        obtenerVuelosProgramadosStream()
                .forEach(Vuelo::mostrarInfo);
    }

    /**
     * @brief Muestra los vuelos ordenados alfabéticamente por destino.
     *
     * Este método ordena los vuelos usando Stream.sorted() junto con una
     * expresión lambda. El orden original de la lista interna no se modifica.
     *
     * Se utiliza:
     * - stream()
     * - sorted()
     * - lambda
     * - forEach()
     * - referencia a método: Vuelo::mostrarInfo
     */
    public void mostrarVuelosOrdenadosPorDestinoStream() {
        vuelos.stream()
                .sorted((vuelo1, vuelo2) -> vuelo1.getDestino().compareToIgnoreCase(vuelo2.getDestino()))
                .forEach(Vuelo::mostrarInfo);
    }

    /**
     * @brief Calcula el total de asientos ocupados en vuelos programados.
     *
     * Este método filtra los vuelos programados y luego obtiene la cantidad
     * de asientos ocupados de cada uno para calcular la suma total.
     *
     * Se utiliza:
     * - stream()
     * - filter()
     * - mapToInt()
     * - sum()
     *
     * @return Total de asientos ocupados en todos los vuelos programados.
     */
    public int calcularTotalAsientosOcupadosProgramadosStream() {
        return vuelos.stream()
                .filter(vuelo -> vuelo.getEstado() == EstadoVuelo.PROGRAMADO)
                .mapToInt(Vuelo::getAsientosOcupados)
                .sum();
    }

    /**
     * @brief Devuelve las personas ordenadas por apellido.
     *
     * Utiliza Collections.sort() y el Comparable implementado en Persona.
     *
     * @return Lista de personas ordenadas.
     */
    public ArrayList<Persona> obtenerPersonasOrdenadasPorApellido() {
        ArrayList<Persona> personas = new ArrayList<>(personasPorDni.values());
        Collections.sort(personas);
        return personas;
    }

    /**
     * @brief Devuelve los vuelos ordenados por número usando Comparable.
     *
     * Utiliza Collections.sort(lista) y el compareTo() de Vuelo.
     *
     * @return Lista de vuelos ordenados por número.
     */
    public ArrayList<Vuelo> obtenerVuelosOrdenadosPorNumeroComparable() {
        ArrayList<Vuelo> vuelosOrdenados = new ArrayList<>(vuelos);
        Collections.sort(vuelosOrdenados);
        return vuelosOrdenados;
    }

    /**
     * @brief Devuelve los vuelos ordenados por destino usando Comparator externo.
     *
     * @return Lista de vuelos ordenados por destino.
     */
    public ArrayList<Vuelo> obtenerVuelosOrdenadosPorDestino() {
        ArrayList<Vuelo> vuelosOrdenados = new ArrayList<>(vuelos);
        Collections.sort(vuelosOrdenados, new ComparadorVueloPorDestino());
        return vuelosOrdenados;
    }

    /**
     * @brief Devuelve los vuelos ordenados por número usando Comparator externo.
     *
     * @return Lista de vuelos ordenados por número.
     */
    public ArrayList<Vuelo> obtenerVuelosOrdenadosPorNumeroComparator() {
        ArrayList<Vuelo> vuelosOrdenados = new ArrayList<>(vuelos);
        Collections.sort(vuelosOrdenados, new ComparadorVueloPorNumero());
        return vuelosOrdenados;
    }

    /**
     * @brief Muestra todos los vuelos de la aerolínea.
     */
    public void mostrarVuelos() {
        System.out.println("Vuelos de " + nombre + ":");

        if (vuelos.isEmpty()) {
            System.out.println("No hay vuelos cargados.");
            return;
        }

        for (Vuelo vuelo : vuelos) {
            vuelo.mostrarInfo();
        }
    }

    /**
     * @brief Muestra los pasajeros con reserva activa.
     *
     * Usa el HashSet<Persona> pasajerosConReservaActiva.
     */
    public void mostrarPasajerosConReservaActiva() {
        System.out.println("Pasajeros con reserva activa:");

        if (pasajerosConReservaActiva.isEmpty()) {
            System.out.println("No hay pasajeros con reservas activas.");
            return;
        }

        for (Persona persona : pasajerosConReservaActiva) {
            persona.mostrarInfo();
        }
    }

    /**
     * @brief Demuestra polimorfismo recorriendo una lista de vuelos.
     *
     * Recorre ArrayList<Vuelo> y llama a embarcar() y mostrarInfo()
     * sin conocer el tipo concreto del vuelo.
     */
    public void operarVuelosPolimorficamente() {
        for (Vuelo vuelo : vuelos) {
            vuelo.embarcar();
            vuelo.mostrarInfo();
        }
    }

    /**
     * @brief Valida que un texto obligatorio no sea nulo ni vacío.
     *
     * @param valor Texto recibido.
     * @param campo Nombre del campo validado.
     * @return Texto normalizado sin espacios laterales.
     * @throws IllegalArgumentException Si el texto es nulo o vacío.
     */
    private String validarTextoObligatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede estar vacío.");
        }

        return valor.trim();
    }
}