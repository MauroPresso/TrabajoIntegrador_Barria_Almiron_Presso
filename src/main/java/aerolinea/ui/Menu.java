package aerolinea.ui;

import aerolinea.excepcion.VueloNoDisponibleException;
import aerolinea.dominio.Pasajero;
import aerolinea.dominio.Persona;
import aerolinea.dominio.Vuelo;
import aerolinea.dominio.VueloCharter;
import aerolinea.dominio.VueloInternacional;
import aerolinea.dominio.VueloNacional;
import aerolinea.dominio.Aerolinea;
import aerolinea.servicio.Servicio;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @file Menu.java
 * @brief Contiene el menu interactivo por consola del sistema de aerolinea.
 */

/**
 * @class Menu
 * @brief Gestiona la interaccion del usuario con el sistema mediante consola.
 *
 * Esta clase utiliza Scanner para leer datos ingresados por teclado y delega
 * las operaciones principales en la clase Aerolinea.
 *
 * Todos los ingresos por teclado se validan mediante metodos auxiliares para
 * evitar datos vacios, numeros negativos, formatos invalidos, fechas incorrectas,
 * opciones fuera de rango o valores incoherentes.
 *
 * Ademas, dispara el guardado de vuelos luego de las operaciones que modifican
 * la lista de vuelos o sus reservas.
 */
public class Menu {

    /**
     * @brief Opcion utilizada para finalizar el menu.
     */
    private static final int OPCION_SALIR = 0;

    /**
     * @brief Opcion maxima disponible en el menu principal.
     */
    private static final int OPCION_MAXIMA_MENU = 7;

    /**
     * @brief Capacidad maxima aceptada para un vuelo.
     */
    private static final int CAPACIDAD_MAXIMA_VUELO = 999;

    /**
     * @brief Servicio principal de la aerolinea.
     */
    private final Aerolinea aerolinea;

    /**
     * Servicio genA(C)rico encargado de persistir los vuelos.
     */
    private final Servicio<Vuelo> servicioVuelos;

    /**
     * Servicio genA(C)rico encargado de persistir personas.
     */
    private final Servicio<Persona> servicioPersonas;

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
     * @brief Constructor del menu.
     *
     * @param aerolinea Objeto principal del dominio.
     */
    public Menu(Aerolinea aerolinea, Servicio<Vuelo> servicioVuelos) {
        this(aerolinea, servicioVuelos, null);
    }

    public Menu(Aerolinea aerolinea,
                Servicio<Vuelo> servicioVuelos,
                Servicio<Persona> servicioPersonas) {

        if (aerolinea == null) {
            throw new IllegalArgumentException("La aerolAnea no puede ser nula.");
        }

        if (servicioVuelos == null) {
            throw new IllegalArgumentException("El servicio de vuelos no puede ser nulo.");
        }

        this.aerolinea = aerolinea;
        this.servicioVuelos = servicioVuelos;
        this.servicioPersonas = servicioPersonas;
        this.scanner = new Scanner(System.in);
        this.cambiosSinGuardar = false;
    }

    /**
     * @brief Inicia la ejecucion del menu interactivo.
     *
     * Muestra las opciones disponibles hasta que el usuario decide salir.
     * Al finalizar, intenta guardar cualquier cambio que haya quedado pendiente.
     */
    public void iniciar() {
        int opcion = OPCION_SALIR;

        try {
            do {
                mostrarOpciones();
                opcion = leerEnteroEnRango("Seleccione una opcion: ",
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
     * @brief Ejecuta la opcion seleccionada por el usuario.
     *
     * @param opcion Numero de opcion ingresado.
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
                System.out.println("Opcion invalida.");
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

        String numero = leerCodigoVuelo("Numero de vuelo: ");

        while (aerolinea.buscarVueloPorNumero(numero) != null) {
            System.out.println("Ya existe un vuelo con ese numero.");
            numero = leerCodigoVuelo("Ingrese otro numero de vuelo: ");
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
     * @brief Crea un vuelo segun el tipo seleccionado por el usuario.
     *
     * @param tipoVuelo Tipo de vuelo seleccionado.
     * @param numero Numero del vuelo.
     * @param origen Origen del vuelo.
     * @param destino Destino del vuelo.
     * @param fecha Fecha del vuelo.
     * @param capacidad Capacidad maxima del vuelo.
     * @return Vuelo creado.
     */
    private Vuelo crearVueloSegunTipo(int tipoVuelo, String numero, String origen,
                                      String destino, String fecha, int capacidad) {
        switch (tipoVuelo) {
            case 1:
                String provinciaDestino = leerTextoAlfabetico("Provincia de destino: ");
                return new VueloNacional(numero, origen, destino, fecha, capacidad, provinciaDestino);

            case 2:
                String paisDestino = leerTextoAlfabetico("Pais de destino: ");
                boolean requierePasaporte = leerBooleano("Requiere pasaporte? (S/N): ");
                return new VueloInternacional(numero, origen, destino, fecha, capacidad,
                        paisDestino, requierePasaporte);

            case 3:
                String empresaContratante = leerTextoEmpresa("Empresa contratante: ");
                double costoTotal = leerDoubleNoNegativo("Costo total: ");
                return new VueloCharter(numero, origen, destino, fecha, capacidad,
                        empresaContratante, costoTotal);

            default:
                throw new IllegalArgumentException("Tipo de vuelo invalido.");
        }
    }

    /**
     * @brief Permite registrar un pasajero en la aerolinea.
     *
     * El requerimiento de serializacion actual persiste la lista de vuelos.
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
        String numeroPasaporte = leerPasaporteOpcional("Numero de pasaporte (opcional): ");

        try {
            Pasajero pasajero = new Pasajero(dni, nombre, apellido, numeroPasaporte);
            aerolinea.registrarPersona(pasajero);
            System.out.println("Pasajero registrado correctamente.");
            marcarCambiosYGuardar("registrar pasajero");
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo registrar el pasajero: " + e.getMessage());
        }
    }

    /**
     * @brief Permite reservar un vuelo para un pasajero registrado.
     *
     * Valida que existan pasajeros y vuelos cargados, que el pasajero exista,
     * que el vuelo exista, que no haya una reserva duplicada y que el usuario
     * confirme la operacion antes de realizarla.
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
            String numeroVuelo = leerCodigoVueloExistente("Numero de vuelo: ");

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

            boolean confirma = leerBooleano("Confirma la reserva? (S/N): ");

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
            System.out.println("Finalizo la operacion de reserva.");
        }
    }

    /**
     * @brief Permite cancelar una reserva existente.
     *
     * Valida que el pasajero y el vuelo existan, que la reserva exista y pide
     * confirmacion antes de cancelarla.
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
        String numeroVuelo = leerCodigoVueloExistente("Numero de vuelo: ");

        Pasajero pasajero = obtenerPasajeroPorDni(dniPasajero);
        Vuelo vuelo = aerolinea.buscarVueloPorNumero(numeroVuelo);

        if (!pasajero.tieneVueloReservado(vuelo)) {
            System.out.println("El pasajero no tiene una reserva en ese vuelo.");
            return;
        }

        System.out.println();
        System.out.println("Reserva encontrada:");
        vuelo.mostrarInfo();

        boolean confirma = leerBooleano("Confirma la cancelacion de la reserva? (S/N): ");

        if (!confirma) {
            System.out.println("Cancelacion abortada por el usuario.");
            return;
        }

        try {
            aerolinea.cancelarReserva(dniPasajero, numeroVuelo);
            System.out.println("Operacion de cancelacion procesada.");
            marcarCambiosYGuardar("cancelar reserva");
        } catch (IllegalArgumentException e) {
            System.out.println("Error al cancelar la reserva: " + e.getMessage());
        }
    }

    /**
     * @brief Marca que hubo cambios y ejecuta el guardado.
     *
     * @param operacion Nombre de la operacion realizada.
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
     * @param contexto Texto que indica en que momento se intenta guardar.
     */
    private void guardarCambiosPendientes(String contexto) {
        if (!cambiosSinGuardar) {
            return;
        }

        try {
            servicioVuelos.reemplazarTodos(aerolinea.getVuelos());
            servicioVuelos.guardar();

            if (servicioPersonas != null) {
                servicioPersonas.reemplazarTodos(
                        new ArrayList<>(
                                aerolinea
                                        .getPersonasPorDni()
                                        .values()));

                servicioPersonas.guardar();
            }

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
     * @brief Muestra todos los vuelos ordenados alfabeticamente por destino.
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
     * @return true si puede reservar segun el requisito de pasaporte.
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
     * @brief Lee un numero entero desde consola.
     *
     * Repite la lectura hasta que el usuario ingrese un entero valido.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Numero entero ingresado.
     */
    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();

            if (!entrada.matches("-?\\d+")) {
                System.out.println("Debe ingresar un numero entero valido.");
                continue;
            }

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("El numero ingresado es demasiado grande.");
            }
        }
    }

    /**
     * @brief Lee un numero entero dentro de un rango determinado.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @param minimo Valor minimo permitido.
     * @param maximo Valor maximo permitido.
     * @return Numero entero valido dentro del rango.
     */
    private int leerEnteroEnRango(String mensaje, int minimo, int maximo) {
        while (true) {
            int valor = leerEntero(mensaje);

            if (valor >= minimo && valor <= maximo) {
                return valor;
            }

            System.out.println("Debe ingresar un numero entre " + minimo + " y " + maximo + ".");
        }
    }

    /**
     * @brief Lee un DNI valido desde consola.
     *
     * Acepta unicamente numeros positivos de 7 u 8 digitos.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return DNI valido.
     */
    private int leerDni(String mensaje) {
        while (true) {
            int dni = leerEntero(mensaje);

            if (dni >= 1000000 && dni <= 99999999) {
                return dni;
            }

            System.out.println("El DNI debe ser un numero positivo de 7 u 8 digitos.");
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
     * @brief Lee un numero decimal no negativo.
     *
     * Acepta coma o punto como separador decimal.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Numero decimal mayor o igual que cero.
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

                System.out.println("Debe ingresar un numero mayor o igual que cero.");
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un numero decimal valido.");
            }
        }
    }

    /**
     * @brief Lee un codigo de vuelo valido.
     *
     * El formato aceptado es de dos a cuatro letras seguidas de uno a seis numeros.
     * Ejemplos validos: AR100, CH300, IFES25.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Codigo de vuelo normalizado en mayusculas.
     */
    private String leerCodigoVuelo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim().toUpperCase();

            if (entrada.matches("^[A-Z]{2,4}\\d{1,6}$")) {
                return entrada;
            }

            System.out.println("Codigo invalido. Ejemplos validos: AR100, CH300, IFES25.");
        }
    }

    /**
     * @brief Lee el codigo de un vuelo existente.
     *
     * Repite la lectura hasta que el vuelo exista en la aerolinea.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Codigo de un vuelo existente.
     */
    private String leerCodigoVueloExistente(String mensaje) {
        while (true) {
            String numeroVuelo = leerCodigoVuelo(mensaje);

            if (aerolinea.buscarVueloPorNumero(numeroVuelo) != null) {
                return numeroVuelo;
            }

            System.out.println("No existe un vuelo registrado con ese numero.");
        }
    }

    /**
     * @brief Lee un texto alfabetico obligatorio.
     *
     * Acepta letras, espacios, tildes, puntos, apostrofes y guiones.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Texto valido normalizado.
     */
    private String leerTextoAlfabetico(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();

            if (entrada.matches("^[\\p{L} .'-]{2,50}$")) {
                return normalizarCapitalizacion(entrada);
            }

            System.out.println("Debe ingresar texto valido. Ejemplos: Neuquen, Buenos Aires, Cordoba.");
        }
    }

    /**
     * @brief Lee un texto valido para el nombre de una empresa.
     *
     * Acepta letras, numeros, espacios y algunos simbolos comerciales comunes.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Texto valido para empresa.
     */
    private String leerTextoEmpresa(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();

            if (entrada.matches("^[\\p{L}\\p{N} .,'&-]{2,80}$")) {
                return normalizarCapitalizacion(entrada);
            }

            System.out.println("Debe ingresar un nombre de empresa valido.");
        }
    }

    /**
     * @brief Lee una fecha valida en formato ISO.
     *
     * El formato requerido es yyyy-MM-dd. Por ejemplo: 2026-06-20.
     * Ademas, la fecha no puede ser anterior al dia actual.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Fecha valida como texto.
     */
    private String leerFechaValida(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();

            try {
                LocalDate fecha = LocalDate.parse(entrada);

                if (fecha.isBefore(LocalDate.now())) {
                    System.out.println("La fecha del vuelo no puede ser anterior al dia actual.");
                } else {
                    return fecha.toString();
                }

            } catch (DateTimeParseException e) {
                System.out.println("Fecha invalida. Use el formato yyyy-MM-dd. Ejemplo: 2026-06-20.");
            }
        }
    }

    /**
     * @brief Lee un numero de pasaporte opcional.
     *
     * Permite dejar el campo vacio. Si se completa, debe ser alfanumerico.
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Pasaporte ingresado o cadena vacia.
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

            System.out.println("Pasaporte invalido. Use letras, numeros o guiones. Ejemplo: A123456.");
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
     * @brief Normaliza un texto colocando cada palabra con inicial mayuscula.
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
