# Sistema de AerolÃ­nea â€” ProgramaciÃ³n II

Proyecto acadÃ©mico desarrollado en Java para la asignatura **ProgramaciÃ³n II** de la carrera AnÃ¡lisis de Sistemas â€” IFES.

El sistema toma como referencia conceptual el proyecto **Biblioteca** utilizado por la cÃ¡tedra, pero traslada los mismos principios de diseÃ±o, ProgramaciÃ³n Orientada a Objetos, persistencia, genÃ©ricos, colecciones, programaciÃ³n funcional y Swing al dominio de una aerolÃ­nea.

> El objetivo no es copiar el cÃ³digo de Biblioteca, sino construir un proyecto anÃ¡logo usando entidades y reglas propias del dominio aeronÃ¡utico.

## Origen del proyecto

La primera versiÃ³n del Sistema de AerolÃ­nea fue desarrollada como Trabajo PrÃ¡ctico Grupal de ProgramaciÃ³n I por:

- AgustÃ­n AlmirÃ³n
- Juan Cruz Barria
- Mauro Presso

En ProgramaciÃ³n II el proyecto fue reorganizado y ampliado para incorporar la arquitectura y los contenidos trabajados en la nueva asignatura.

## Funcionalidades

La aplicaciÃ³n permite:

- Registrar vuelos nacionales, internacionales y charter.
- Registrar pasajeros.
- Persistir vuelos y personas mediante serializaciÃ³n.
- Listar vuelos y pasajeros en tablas Swing.
- Reservar vuelos.
- Cancelar reservas.
- Validar disponibilidad de asientos.
- Validar pasaporte en vuelos internacionales que lo requieren.
- Consultar vuelos programados.
- Ordenar vuelos mediante Comparable y Comparator.
- Procesar colecciones mediante Stream API.
- Ejecutar tanto interfaz grÃ¡fica Swing como menÃº de consola.

## Arquitectura

El cÃ³digo principal se organiza bajo el paquete raÃ­z `aerolinea`:

```text
src/main/java/aerolinea/
â”œâ”€â”€ dominio/
â”œâ”€â”€ excepcion/
â”œâ”€â”€ main/
â”œâ”€â”€ repositorio/
â”œâ”€â”€ servicio/
â”œâ”€â”€ ui/
â””â”€â”€ util/
```

### Responsabilidades

- `dominio`: entidades, reglas de negocio, herencia, polimorfismo y colecciones del sistema.
- `excepcion`: excepciones especÃ­ficas del dominio.
- `main`: punto de entrada y composiciÃ³n de dependencias.
- `repositorio`: abstracciÃ³n y persistencia genÃ©rica.
- `servicio`: servicios genÃ©ricos sobre repositorios.
- `ui`: interfaz grÃ¡fica Swing y menÃº de consola.
- `util`: comparadores y utilidades didÃ¡cticas.

La clase `Aerolinea` pertenece al dominio y no conoce archivos ni repositorios.

```text
RepositorioArchivo<T>
        â†“
   Servicio<T>
        â†“
    Aerolinea
        â†“
 Swing / Consola
```

## AnalogÃ­a con el proyecto Biblioteca

| Biblioteca | Sistema de AerolÃ­nea | Concepto |
|---|---|---|
| `Material` | `Vuelo` | clase abstracta |
| `Libro`, `Revista`, `Cd` | `VueloNacional`, `VueloInternacional`, `VueloCharter` | herencia y polimorfismo |
| `Persona` | `Persona` | clase abstracta + Comparable |
| `Usuario`, `Bibliotecario` | `Pasajero`, `Tripulante` | especializaciÃ³n |
| `IPrestable` | `IOperable` | interfaz |
| `Biblioteca` | `Aerolinea` | objeto principal del dominio |
| `MaterialNoDisponibleException` | `VueloNoDisponibleException` | excepciÃ³n de negocio |
| `IRepositorio<T>` | `IRepositorio<T>` | abstracciÃ³n genÃ©rica |
| `RepositorioArchivo<T>` | `RepositorioArchivo<T>` | serializaciÃ³n genÃ©rica |
| `IServicio<T>` | `IServicio<T>` | contrato de servicio |
| `Servicio<T>` | `Servicio<T>` | servicio genÃ©rico |
| `PanelMateriales` | `PanelVuelosFormulario` | formulario Swing |
| `PanelMaterialesTabla` | `PanelVuelosTabla` | JTable |
| `TableMaterialesModel` | `TableVuelosModel` | AbstractTableModel |
| `PanelUsuarios` | `PanelPasajerosFormulario` | formulario Swing |
| `PanelUsuarioTabla` | `PanelPasajerosTabla` | JTable |
| `TableUsuarioModel` | `TablePasajerosModel` | AbstractTableModel |
| `Prestar` | `PanelReservas` | relaciÃ³n entre entidades |

## Maven

El proyecto utiliza Maven.

Coordenadas principales:

```text
groupId:    ar.edu.ifes
artifactId: sistema-de-aerolinea
version:    1.0-SNAPSHOT
packaging:  jar
```

### Ciclo de vida utilizado

```powershell
mvn clean
mvn compile
mvn package
mvn verify
```

Para realizar una validaciÃ³n completa:

```powershell
mvn clean verify
```

El JAR se genera en:

```text
target/SistemaDeAerolinea-1.0-SNAPSHOT.jar
```

## EjecuciÃ³n

### Interfaz grÃ¡fica Swing

```powershell
java -jar target/SistemaDeAerolinea-1.0-SNAPSHOT.jar
```

Swing se inicializa mediante el Event Dispatch Thread.

### Modo consola

```powershell
java -jar target/SistemaDeAerolinea-1.0-SNAPSHOT.jar --consola
```

## Persistencia

Los datos se almacenan mediante serializaciÃ³n Java:

```text
data/vuelos.dat
data/personas.dat
```

Ambos archivos son generados durante la ejecuciÃ³n y estÃ¡n excluidos del repositorio mediante `.gitignore`.

La persistencia utiliza las mismas clases genÃ©ricas:

```java
RepositorioArchivo<Vuelo>
RepositorioArchivo<Persona>
```

## Interfaz grÃ¡fica

La navegaciÃ³n utiliza:

- `JFrame`
- `JPanel`
- `JMenuBar`
- `JMenu`
- `JMenuItem`
- `JButton`
- `JTextField`
- `JRadioButton`
- `JCheckBox`
- `JTable`
- `AbstractTableModel`
- `CardLayout`
- `JOptionPane`
- `ActionListener`
- expresiones lambda
- Event Dispatch Thread

Las vistas principales son:

```text
PRINCIPAL
FORMULARIO_VUELO
TABLA_VUELOS
FORMULARIO_PASAJERO
TABLA_PASAJEROS
RESERVAS
```

## Laboratorios de ProgramaciÃ³n II

Los ejemplos didÃ¡cticos estÃ¡n en:

```text
src/test/java/aerolinea/test/
```

Incluyen:

- `TestPolimorfismo`
- `JavaCollectionFrameworkTest`
- `TestLambdaStreams`
- `TestSobrecargaMetodos`
- `TestArchivos`
- `ManejadorDeEventos`
- `TestManejadorDeEventos`
- `UiTest`
- `UiDatosTest`

Estos archivos funcionan como laboratorios ejecutables para estudiar conceptos de forma aislada.

## Conceptos demostrados

El proyecto incorpora, entre otros:

- clases y objetos;
- encapsulamiento;
- clases abstractas;
- herencia;
- polimorfismo;
- interfaces;
- sobrecarga;
- sobrescritura;
- `enum`;
- excepciones;
- `Serializable`;
- genÃ©ricos;
- `List`, `ArrayList`;
- `Set`, `HashSet`;
- `Map`, `HashMap`;
- `Iterator`;
- `Comparable`;
- `Comparator`;
- `Collections`;
- lambdas;
- `Predicate`;
- `Consumer`;
- `Function`;
- `Supplier`;
- Stream API;
- lectura y escritura de archivos;
- serializaciÃ³n;
- Maven;
- Swing;
- programaciÃ³n orientada a eventos;
- `JTable` y `AbstractTableModel`.

## DocumentaciÃ³n adicional

- [Arquitectura y equivalencias con Biblioteca](docs/ARQUITECTURA.md)
- [GuÃ­a de estudio y validaciÃ³n](docs/GUIA_ESTUDIO.md)