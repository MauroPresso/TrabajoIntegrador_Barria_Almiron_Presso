package aerolinea.ui;

import aerolinea.excepcion.VueloNoDisponibleException;
import aerolinea.dominio.Pasajero;
import aerolinea.dominio.Persona;
import aerolinea.dominio.Vuelo;
import aerolinea.dominio.VueloCharter;
import aerolinea.dominio.VueloInternacional;
import aerolinea.dominio.VueloNacional;
import aerolinea.servicio.Aerolinea;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * @file Menu.java
 * @brief Contiene el menú interactivo por consola del sistema de aerolínea.
 */

/**
 * @class Menu
 * @brief Gestiona la interacción del usuario con el sistema mediante consola.
 *
 * Esta clase utiliza Scanner para leer datos ingresados por teclado y delega
 * las operaciones principales en la clase Aerolinea.
 *
 * Todos los ingresos por teclado se validan mediante métodos auxiliares para
 * evitar datos vacíos, números negativos, formatos inválidos, fechas incorrectas,
 * opciones fuera de rango o valores incoherentes.
 *
 * Además, dispara el guardado de vuelos luego de las operaciones que modifican
 * la lista de vuelos o sus reservas.
 */
public class Menu {

    /**
     * @brief Opción utilizada para finalizar el menú.
     */
    private static final int OPCION_SALIR = 0;

    /**
     * @brief Opción máxima disponible en el menú principal.
     */
    private static final int OPCION_MAXIMA_MENU = 7;

    /**
     * @brief Capacidad máxima aceptada para un vuelo.
     */
    private static final int CAPACIDAD_MAXIMA_VUELO = 999;

    /**
     * @brief Servicio principal de la aerolínea.
     */
    private final Aerolinea aerolinea;

    /**
     * @brief Scanner utilizado para leer datos desde consola.
     */
    private final Scanner scanner;

    /**
     * @brief Indica si existen cambios pendientes de guardado.
     *
     * Se activa cuando se agrega un vuelo, se reserva un vuelo o se cancela
     * una reserva. Si un guardado falla, queda en true para volver a intentar
     * al finalizar el sistema.
     */
    private boolean cambiosSinGuardar;

    /**
     * @brief Constructor del menú.
     *
     * @param aerolinea Servicio principal del sistema.
     */
    public Menu(Aerolinea aerolinea) {
        this.aerolinea = aerolinea;
        this.scanner = new Scanner(System.in);
        this.cambiosSinGuardar = false;
    }

    /**
     * @brief Inicia la ejecución del menú interactivo.
     *
     * Muestra las opciones disponibles hasta que el usuario decide salir.
     * Al finalizar, intenta guardar cualquier cambio que haya quedado pendiente.
     */
    public void iniciar() {
        int opcion = OPCION_SALIR;

        try {
            do {
                mostrarOpciones();
                opcion = leerEnteroEnRango("Seleccione una opción: ",
                        OPCION_SALIR, OPCION_MAXIMA_MENU);
                ejecutarOpcion(opcion);
            } while (opcion != OPCION_SALIR);
        } finally {
            guardarCambiosPendientes("al finalizar el sistema");
        }
    }

    /**
     * @brief Muestra las opciones principales del sistema.
     */
    private void mostrarOpciones() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("       SISTEMA DE AEROLINEA IFES");
        System.out.println("========================================");
        System.out.println("1. Agregar vuelo");
        System.out.println("2. Registrar pasajero");
        System.out.println("3. Reservar vuelo");
        System.out.println("4. Cancelar reserva");
        System.out.println("5. Mostrar vuelos programados");
        System.out.println("6. Mostrar vuelos ordenados por destino");
        System.out.println("7. Mostrar total de asientos ocupados");
        System.out.println("0. Salir");
        System.out.println("========================================");
    }

    /**
     * @brief Ejecuta la opción seleccionada por el usuario.
     *
     * @param opcion Número de opción ingresado.
     */
    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                agregarVuelo();
                break;
            case 2:
                registrarPasajero();
                break;
            case 3:
                reservarVuelo();
                break;
            case 4:
                cancelarReserva();
                break;
            case 5:
                mostrarVuelosProgramados();
                break;
            case 6:
                mostrarVuelosOrdenadosPorDestino();
                break;
            case 7:
                mostrarTotalAsientosOcupados();
                break;
            case OPCION_SALIR:
                System.out.println("Saliendo del sistema...");
                break;
            default:
                System.out.println("Opción inválida.");
                break;
        }
    }

    /**
     * @brief Permite agregar un vuelo nacional, internacional o charter.
     *
     * Solicita primero el tipo de vuelo y valida que sea correcto antes
     * de pedir el resto de los datos.
     */
    private void agregarVuelo() {
        System.out.println();
        System.out.println("--------- AGREGAR VUELO ---------");
        System.out.println("1. Vuelo nacional");
        System.out.println("2. Vuelo internacional");
        System.out.println("3. Vuelo charter");

        int tipoVuelo = leerEnteroEnRango("Seleccione el tipo de vuelo: ", 1, 3);

        String numero = leerCodigoVuelo("Número de vuelo: ");

        while (aerolinea.buscarVueloPorNumero(numero) != null) {
            System.out.println("Ya existe un vuelo con ese número.");
            numero = leerCodigoVuelo("Ingrese otro número de vuelo: ");
        }

        String origen = leerTextoAlfabetico("Ciudad o aeropuerto de origen: ");
        String destino = leerTextoAlfabetico("Ciudad o aeropuerto de destino: ");
        String fecha = leerFechaValida("Fecha del vuelo (yyyy-MM-dd): ");
        int capacidad = leerEnteroEnRango("Capacidad: ", 1, CAPACIDAD_MAXIMA_VUELO);

        try {
            Vuelo vuelo = crearVueloSegunTipo(tipoVuelo, numero, origen, destino, fecha, capacidad);
            aerolinea.agregarVuelo(vuelo);
            System.out.println("Vuelo agregado correctamente.");
            marcarCambiosYGuardar("agregar vuelo");
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo agregar el vuelo: " + e.getMessage());
        }
    }

    /**
     * @brief Crea un vuelo según el tipo seleccionado por el usuario.
     *
     * @param tipoVuelo Tipo de vuelo seleccionado.
     * @param numero Número del vuelo.
     * @param origen Origen del vuelo.
     * @param destino Destino del vuelo.
     * @param fecha Fecha del vuelo.
     * @param capacidad Capacidad máxima del vuelo.
     * @return Vuelo creado.
     */
    private Vuelo crearVueloSegunTipo(int tipoVuelo, String numero, String origen,
                                      String destino, String fecha, int capacidad) {
        switch (tipoVuelo) {
            case 1:
                String provinciaDestino = leerTextoAlfabetico("Provincia de destino: ");
                return new VueloNacional(numero, origen, destino, fecha, capacidad, provinciaDestino);

            case 2:
                String paisDestino = leerTextoAlfabetico("País de destino: ");
                boolean requierePasaporte = leerBooleano("¿Requiere pasaporte? (S/N): ");
                return new VueloInternacional(numero, origen, destino, fecha, capacidad,
                        paisDestino, requierePasaporte);

            case 3:
                String empresaContratante = leerTextoEmpresa("Empresa contratante: ");
                double costoTotal = leerDoubleNoNegativo("Costo total: ");
                return new VueloCharter(numero, origen, destino, fecha, capacidad,
                        empresaContratante, costoTotal);

            default:
                throw new IllegalArgumentException("Tipo de vuelo inválido.");
        }
    }

    /**
     * @brief Permite registrar un pasajero en la aerolínea.
     *
     * El requerimiento de serialización actual persiste la lista de vuelos.
     * Por eso, los pasajeros quedan persistidos cuando forman parte de una
     * reserva dentro de un vuelo guardado.
     */
    private void registrarPasajero() {
        System.out.println();
        System.out.println("--------- REGISTRAR PASAJERO ---------");

        int dni = leerDni("DNI: ");

        while (aerolinea.buscarPersonaPorDni(dni) != null) {
            System.out.println("Ya existe una persona registrada con ese DNI.");
            dni = leerDni("Ingrese otro DNI: ");
        }

        String nombre = leerTextoAlfabetico("Nombre: ");
        String apellido = leerTextoAlfabetico("Apellido: ");
        String numeroPasaporte = leerPasaporteOpcional("Número de pasaporte (opcional): ");

        try {
            Pasajero pasajero = new Pasajero(dni, nombre, apellido, numeroPasaporte);
            aerolinea.registrarPersona(pasajero);
            System.out.println("Pasajero registrado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo registrar el pasajero: " + e.getMessage());
        }
    }

    /**
     * @brief Permite reservar un vuelo para un pasajero registrado.
     *
     * Valida que existan pasajeros y vuelos cargados, que el pasajero exista,
     * que el vuelo exista, que no haya una reserva duplicada y que el usuario
     * confirme la operación antes de realizarla.
     */
    private void reservarVuelo() {
        System.out.println();
        System.out.println("--------- RESERVAR VUELO ---------");

        if (!existeAlMenosUnPasajeroRegistrado()) {
            System.out.println("No hay pasajeros registrados.");
            return;
        }

        if (aerolinea.getVuelos().isEmpty()) {
            System.out.println("No hay vuelos cargados.");
            return;
        }

        try {
            int dniPasajero = leerDniPasajeroRegistrado("DNI del pasajero: ");
            String numeroVuelo = leerCodigoVueloExistente("Número de vuelo: ");

            Pasajero pasajero = obtenerPasajeroPorDni(dniPasajero);
            Vuelo vuelo = aerolinea.buscarVueloPorNumero(numeroVuelo);

            if (pasajero.tieneVueloReservado(vuelo)) {
                System.out.println("El pasajero ya tiene una reserva en ese vuelo.");
                return;
            }

            if (!pasajeroTienePasaporteSiElVueloLoRequiere(pasajero, vuelo)) {
                System.out.println("El vuelo requiere pasaporte y el pasajero no tiene pasaporte cargado.");
                return;
            }

            System.out.println();
            System.out.println("Datos del vuelo seleccionado:");
            vuelo.mostrarInfo();

            System.out.println();
            pasajero.mostrarInfo();

            boolean confirma = leerBooleano("¿Confirma la reserva? (S/N): ");

            if (!confirma) {
                System.out.println("Reserva cancelada por el usuario.");
                return;
            }

            aerolinea.reservarVuelo(dniPasajero, numeroVuelo);
            System.out.println("Reserva realizada correctamente.");
            marcarCambiosYGuardar("reservar vuelo");

        } catch (VueloNoDisponibleException e) {
            System.out.println("No se pudo reservar el vuelo: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error en la reserva: " + e.getMessage());
        } finally {
            System.out.println("Finalizó la operación de reserva.");
        }
    }

    /**
     * @brief Permite cancelar una reserva existente.
     *
     * Valida que el pasajero y el vuelo existan, que la reserva exista y pide
     * confirmación antes de cancelarla.
     */
    private void cancelarReserva() {
        System.out.println();
        System.out.println("--------- CANCELAR RESERVA ---------");

        if (!existeAlMenosUnPasajeroRegistrado()) {
            System.out.println("No hay pasajeros registrados.");
            return;
        }

        if (aerolinea.getVuelos().isEmpty()) {
            System.out.println("No hay vuelos cargados.");
            return;
        }

        int dniPasajero = leerDniPasajeroRegistrado("DNI del pasajero: ");
        String numeroVuelo = leerCodigoVueloExistente("Número de vuelo: ");

        Pasajero pasajero = obtenerPasajeroPorDni(dniPasajero);
        Vuelo vuelo = aerolinea.buscarVueloPorNumero(numeroVuelo);

        if (!pasajero.tieneVueloReservado(vuelo)) {
            System.out.println("El pasajero no tiene una reserva en ese vuelo.");
            return;
        }

        System.out.println();
        System.out.println("Reserva encontrada:");
        vuelo.mostrarInfo();

        boolean confirma = leerBooleano("¿Confirma la cancelación de la reserva? (S/N): ");

        if (!confirma) {
            System.out.println("Cancelación abortada por el usuario.");
            return;
        }

        try {
            aerolinea.cancelarReserva(dniPasajero, numeroVuelo);
            System.out.println("Operación de cancelación procesada.");
            marcarCambiosYGuardar("cancelar reserva");
        } catch (IllegalArgumentException e) {
            System.out.println("Error al cancelar la reserva: " + e.getMessage());
        }
    }

    /**
     * @brief Marca que hubo cambios y ejecuta el guardado.
     *
     * @param operacion Nombre de la operación realizada.
     */
    private void marcarCambiosYGuardar(String operacion) {
        cambiosSinGuardar = true;
        guardarCambiosPendientes("luego de " + operacion);
    }

    /**
     * @brief Guarda los vuelos si existen cambios pendientes.
     *
     * Si el guardado se realiza correctamente, limpia la marca de cambios
     * pendientes. Si falla, la marca queda activa para intentar guardar al salir.
     *
     * @param contexto Texto que indica en qué momento se intenta guardar.
     */
    private void guardarCambiosPendientes(String contexto) {
        if (!cambiosSinGuardar) {
            return;
        }

        try {
            aerolinea.guardarVuelos();
            cambiosSinGuardar = false;
            System.out.println("Vuelos guardados correctamente " + contexto + ".");
        } catch (IOException e) {
            System.out.println("No se pudieron guardar los vuelos " + contexto + ": " + e.getMessage());
        }
    }

    /**
     * @brief Muestra los vuelos que se encuentran en estado PROGRAMADO.
     */
    private void mostrarVuelosProgramados() {
        System.out.println();
        System.out.println("--------- VUELOS PROGRAMADOS ---------");

        List<Vuelo> vuelosProgramados = aerolinea.obtenerVuelosProgramadosStream();

        if (vuelosProgramados.isEmpty()) {
            System.out.println("No hay vuelos programados.");
            return;
        }

        vuelosProgramados.forEach(Vuelo::mostrarInfo);
    }

    /**
     * @brief Muestra todos los vuelos ordenados alfabéticamente por destino.
     */
    private void mostrarVuelosOrdenadosPorDestino() {
        System.out.println();
        System.out.println("--------- VUELOS ORDENADOS POR DESTINO ---------");

        if (aerolinea.getVuelos().isEmpty()) {
            System.out.println("No hay vuelos cargados.");
            return;
        }

        aerolinea.mostrarVuelosOrdenadosPorDestinoStream();
    }

    /**
     * @brief Muestra el total de asientos ocupados en vuelos programados.
     */
    private void mostrarTotalAsientosOcupados() {
        System.out.println();
        System.out.println("--------- TOTAL DE ASIENTOS OCUPADOS ---------");

        int total = aerolinea.calcularTotalAsientosOcupadosProgramadosStream();

        System.out.println("Total de asientos ocupados en vuelos programados: " + total);
    }

    /**
     * @brief Verifica si existe al menos un pasajero registrado.
     *
     * @return true si existe al menos una persona de tipo Pasajero.
     */
    private boolean existeAlMenosUnPasajeroRegistrado() {
        return aerolinea.getPersonasPorDni()
                .values()
                .stream()
                .anyMatch(persona -> persona instanceof Pasajero);
    }

    /**
     * @brief Obtiene un pasajero registrado a partir de su DNI.
     *
     * @param dni DNI del pasajero.
     * @return Pasajero encontrado.
     */
    private Pasajero obtenerPasajeroPorDni(int dni) {
        Persona persona = aerolinea.buscarPersonaPorDni(dni);
        return (Pasajero) persona;
    }

    /**
     * @brief Verifica si el pasajero tiene pasaporte cuando el vuelo lo requiere.
     *
     * @param pasajero Pasajero que desea reservar.
     * @param vuelo Vuelo seleccionado.
     * @return true si puede reservar según el requisito de pasaporte.
     */
    private boolean pasajeroTienePasaporteSiElVueloLoRequiere(Pasajero pasajero, Vuelo vuelo) {
        if (vuelo instanceof VueloInternacional) {
            VueloInternacional vueloInternacional = (VueloInternacional) vuelo;

            if (vueloInternacional.isRequierePasaporte()) {
                return !pasajero.getNumeroPasaporte().isEmpty();
            }
        }

        return true;
    }

    /**
     * @brief Lee un número entero desde consola.
     *
     * Repite la lectura hasta que el usuario ingrese un entero válido.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Número entero ingresado.
     */
    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();

            if (!entrada.matches("-?\\d+")) {
                System.out.println("Debe ingresar un número entero válido.");
                continue;
            }

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("El número ingresado es demasiado grande.");
            }
        }
    }

    /**
     * @brief Lee un número entero dentro de un rango determinado.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @param minimo Valor mínimo permitido.
     * @param maximo Valor máximo permitido.
     * @return Número entero válido dentro del rango.
     */
    private int leerEnteroEnRango(String mensaje, int minimo, int maximo) {
        while (true) {
            int valor = leerEntero(mensaje);

            if (valor >= minimo && valor <= maximo) {
                return valor;
            }

            System.out.println("Debe ingresar un número entre " + minimo + " y " + maximo + ".");
        }
    }

    /**
     * @brief Lee un DNI válido desde consola.
     *
     * Acepta únicamente números positivos de 7 u 8 dígitos.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return DNI válido.
     */
    private int leerDni(String mensaje) {
        while (true) {
            int dni = leerEntero(mensaje);

            if (dni >= 1000000 && dni <= 99999999) {
                return dni;
            }

            System.out.println("El DNI debe ser un número positivo de 7 u 8 dígitos.");
        }
    }

    /**
     * @brief Lee el DNI de un pasajero registrado.
     *
     * Repite la lectura hasta que el DNI exista en el sistema y corresponda
     * a una persona de tipo Pasajero.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return DNI de un pasajero registrado.
     */
    private int leerDniPasajeroRegistrado(String mensaje) {
        while (true) {
            int dni = leerDni(mensaje);
            Persona persona = aerolinea.buscarPersonaPorDni(dni);

            if (persona == null) {
                System.out.println("No existe una persona registrada con ese DNI.");
            } else if (!(persona instanceof Pasajero)) {
                System.out.println("La persona registrada con ese DNI no es pasajero.");
            } else {
                return dni;
            }
        }
    }

    /**
     * @brief Lee un número decimal no negativo.
     *
     * Acepta coma o punto como separador decimal.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Número decimal mayor o igual que cero.
     */
    private double leerDoubleNoNegativo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim().replace(",", ".");

            try {
                double valor = Double.parseDouble(entrada);

                if (Double.isFinite(valor) && valor >= 0) {
                    return valor;
                }

                System.out.println("Debe ingresar un número mayor o igual que cero.");
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número decimal válido.");
            }
        }
    }

    /**
     * @brief Lee un código de vuelo válido.
     *
     * El formato aceptado es de dos a cuatro letras seguidas de uno a seis números.
     * Ejemplos válidos: AR100, CH300, IFES25.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Código de vuelo normalizado en mayúsculas.
     */
    private String leerCodigoVuelo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim().toUpperCase();

            if (entrada.matches("^[A-Z]{2,4}\\d{1,6}$")) {
                return entrada;
            }

            System.out.println("Código inválido. Ejemplos válidos: AR100, CH300, IFES25.");
        }
    }

    /**
     * @brief Lee el código de un vuelo existente.
     *
     * Repite la lectura hasta que el vuelo exista en la aerolínea.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Código de un vuelo existente.
     */
    private String leerCodigoVueloExistente(String mensaje) {
        while (true) {
            String numeroVuelo = leerCodigoVuelo(mensaje);

            if (aerolinea.buscarVueloPorNumero(numeroVuelo) != null) {
                return numeroVuelo;
            }

            System.out.println("No existe un vuelo registrado con ese número.");
        }
    }

    /**
     * @brief Lee un texto alfabético obligatorio.
     *
     * Acepta letras, espacios, tildes, puntos, apóstrofes y guiones.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Texto válido normalizado.
     */
    private String leerTextoAlfabetico(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();

            if (entrada.matches("^[\\p{L} .'-]{2,50}$")) {
                return normalizarCapitalizacion(entrada);
            }

            System.out.println("Debe ingresar texto válido. Ejemplos: Neuquén, Buenos Aires, Córdoba.");
        }
    }

    /**
     * @brief Lee un texto válido para el nombre de una empresa.
     *
     * Acepta letras, números, espacios y algunos símbolos comerciales comunes.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Texto válido para empresa.
     */
    private String leerTextoEmpresa(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();

            if (entrada.matches("^[\\p{L}\\p{N} .,'&-]{2,80}$")) {
                return normalizarCapitalizacion(entrada);
            }

            System.out.println("Debe ingresar un nombre de empresa válido.");
        }
    }

    /**
     * @brief Lee una fecha válida en formato ISO.
     *
     * El formato requerido es yyyy-MM-dd. Por ejemplo: 2026-06-20.
     * Además, la fecha no puede ser anterior al día actual.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Fecha válida como texto.
     */
    private String leerFechaValida(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();

            try {
                LocalDate fecha = LocalDate.parse(entrada);

                if (fecha.isBefore(LocalDate.now())) {
                    System.out.println("La fecha del vuelo no puede ser anterior al día actual.");
                } else {
                    return fecha.toString();
                }

            } catch (DateTimeParseException e) {
                System.out.println("Fecha inválida. Use el formato yyyy-MM-dd. Ejemplo: 2026-06-20.");
            }
        }
    }

    /**
     * @brief Lee un número de pasaporte opcional.
     *
     * Permite dejar el campo vacío. Si se completa, debe ser alfanumérico.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Pasaporte ingresado o cadena vacía.
     */
    private String leerPasaporteOpcional(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim().toUpperCase();

            if (entrada.isEmpty()) {
                return "";
            }

            if (entrada.matches("^[A-Z0-9-]{3,20}$")) {
                return entrada;
            }

            System.out.println("Pasaporte inválido. Use letras, números o guiones. Ejemplo: A123456.");
        }
    }

    /**
     * @brief Lee una respuesta booleana desde consola.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return true si el usuario ingresa S, false si ingresa N.
     */
    private boolean leerBooleano(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("S")) {
                return true;
            }

            if (entrada.equalsIgnoreCase("N")) {
                return false;
            }

            System.out.println("Debe ingresar S o N.");
        }
    }

    /**
     * @brief Normaliza un texto colocando cada palabra con inicial mayúscula.
     *
     * @param texto Texto ingresado por el usuario.
     * @return Texto normalizado.
     */
    private String normalizarCapitalizacion(String texto) {
        String[] palabras = texto.trim().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(normalizarPalabra(palabra)).append(" ");
            }
        }

        return resultado.toString().trim();
    }

    /**
     * @brief Normaliza una palabra individual.
     *
     * @param palabra Palabra a normalizar.
     * @return Palabra normalizada.
     */
    private String normalizarPalabra(String palabra) {
        String palabraMinuscula = palabra.toLowerCase();

        return Character.toUpperCase(palabraMinuscula.charAt(0))
                + palabraMinuscula.substring(1);
    }
}