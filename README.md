# Sistema de Aerolinea - Programacion II

Proyecto academico desarrollado en Java para la asignatura **Programacion II** de la carrera Analisis de Sistemas - IFES.

El sistema toma como referencia conceptual el proyecto **Biblioteca** utilizado por la catedra, pero traslada los mismos principios de diseno, Programacion Orientada a Objetos, persistencia, genericos, colecciones, programacion funcional y Swing al dominio de una aerolinea.

> El objetivo no es copiar el codigo de Biblioteca, sino construir un proyecto analogo usando entidades y reglas propias del dominio aeronautico.

## Origen del proyecto

La primera version del Sistema de Aerolinea fue desarrollada como Trabajo Practico Grupal de Programacion I por:

- Agustin Almiron
- Juan Cruz Barria
- Mauro Presso

En Programacion II el proyecto fue reorganizado y ampliado para incorporar la arquitectura y los contenidos trabajados en la nueva asignatura.

## Funcionalidades

La aplicacion permite:

- Registrar vuelos nacionales, internacionales y charter.
- Registrar pasajeros.
- Persistir vuelos y personas mediante serializacion.
- Listar vuelos y pasajeros en tablas Swing.
- Reservar vuelos.
- Cancelar reservas.
- Validar disponibilidad de asientos.
- Validar pasaporte en vuelos internacionales que lo requieren.
- Consultar vuelos programados.
- Ordenar vuelos mediante Comparable y Comparator.
- Procesar colecciones mediante Stream API.
- Ejecutar tanto interfaz grafica Swing como menu de consola.

## Arquitectura

El codigo principal se organiza bajo el paquete raiz `aerolinea`:

```text
src/main/java/aerolinea/
+-- dominio/
+-- excepcion/
+-- main/
+-- repositorio/
+-- servicio/
+-- ui/
+-- util/
```

### Responsabilidades

- `dominio`: entidades, reglas de negocio, herencia, polimorfismo y colecciones del sistema.
- `excepcion`: excepciones especificas del dominio.
- `main`: punto de entrada y composicion de dependencias.
- `repositorio`: abstraccion y persistencia generica.
- `servicio`: servicios genericos sobre repositorios.
- `ui`: interfaz grafica Swing y menu de consola.
- `util`: comparadores y utilidades didacticas.

La clase `Aerolinea` pertenece al dominio y no conoce archivos ni repositorios.

```text
RepositorioArchivo<T>
        v
   Servicio<T>
        v
    Aerolinea
        v
 Swing / Consola
```

## Analogia con el proyecto Biblioteca

| Biblioteca | Sistema de Aerolinea | Concepto |
|---|---|---|
| `Material` | `Vuelo` | clase abstracta |
| `Libro`, `Revista`, `Cd` | `VueloNacional`, `VueloInternacional`, `VueloCharter` | herencia y polimorfismo |
| `Persona` | `Persona` | clase abstracta + Comparable |
| `Usuario`, `Bibliotecario` | `Pasajero`, `Tripulante` | especializacion |
| `IPrestable` | `IOperable` | interfaz |
| `Biblioteca` | `Aerolinea` | objeto principal del dominio |
| `MaterialNoDisponibleException` | `VueloNoDisponibleException` | excepcion de negocio |
| `IRepositorio<T>` | `IRepositorio<T>` | abstraccion generica |
| `RepositorioArchivo<T>` | `RepositorioArchivo<T>` | serializacion generica |
| `IServicio<T>` | `IServicio<T>` | contrato de servicio |
| `Servicio<T>` | `Servicio<T>` | servicio generico |
| `PanelMateriales` | `PanelVuelosFormulario` | formulario Swing |
| `PanelMaterialesTabla` | `PanelVuelosTabla` | JTable |
| `TableMaterialesModel` | `TableVuelosModel` | AbstractTableModel |
| `PanelUsuarios` | `PanelPasajerosFormulario` | formulario Swing |
| `PanelUsuarioTabla` | `PanelPasajerosTabla` | JTable |
| `TableUsuarioModel` | `TablePasajerosModel` | AbstractTableModel |
| `Prestar` | `PanelReservas` | relacion entre entidades |

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

Para realizar una validacion completa:

```powershell
mvn clean verify
```

El JAR se genera en:

```text
target/SistemaDeAerolinea-1.0-SNAPSHOT.jar
```

## Ejecucion

### Interfaz grafica Swing

```powershell
java -jar target/SistemaDeAerolinea-1.0-SNAPSHOT.jar
```

Swing se inicializa mediante el Event Dispatch Thread.

### Modo consola

```powershell
java -jar target/SistemaDeAerolinea-1.0-SNAPSHOT.jar --consola
```

## Persistencia

Los datos se almacenan mediante serializacion Java:

```text
data/vuelos.dat
data/personas.dat
```

Ambos archivos son generados durante la ejecucion y estan excluidos del repositorio mediante `.gitignore`.

La persistencia utiliza las mismas clases genericas:

```java
RepositorioArchivo<Vuelo>
RepositorioArchivo<Persona>
```

## Interfaz grafica

La navegacion utiliza:

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

## Laboratorios de Programacion II

Los ejemplos didacticos estan en:

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
- genericos;
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
- serializacion;
- Maven;
- Swing;
- programacion orientada a eventos;
- `JTable` y `AbstractTableModel`.

## Documentacion adicional

- [Arquitectura y equivalencias con Biblioteca](docs/ARQUITECTURA.md)
- [Guia de estudio y validacion](docs/GUIA_ESTUDIO.md)

## Doxygen

The complete Java source tree is prepared for Doxygen.

Configuration:

```text
docs/doxygen/Doxyfile
```

Generate HTML documentation:

```powershell
doxygen .\docs\doxygen\Doxyfile
```

Open the generated documentation:

```powershell
start .\docs\doxygen\html\index.html
```

The Doxygen configuration includes all source and laboratory packages, private
members, static members, local classes, local methods, and inline source code.

## Laboratorios Maven - Clases 1 a 5

La aplicacion principal conserva su arquitectura Maven JAR + Swing. Los conceptos
Maven que no corresponde mezclar con la aplicacion principal se demuestran de forma
aislada en:

```text
laboratorios-maven/
```

Cobertura:

- archetypes y Maven Quickstart;
- GAV y estructura estandar;
- dependencias externas mediante Maven Central;
- repositorio local `.m2`;
- dependencias directas y transitivas;
- scopes `compile`, `test`, `import` y `provided`;
- plugins y goals;
- goals ligados a fases;
- `mvn site`;
- `install` y `deploy`;
- `distributionManagement`;
- proyecto padre con `packaging` POM;
- proyecto multimodulo y Maven Reactor;
- dependencia entre modulos;
- packaging JAR y WAR;
- JSP;
- Jetty y `jetty:run`.

Guia:

```text
laboratorios-maven/README.md
```

La carpeta principal `src/` sigue siendo el SistemaDeAerolinea real. Los laboratorios
son evidencia academica separada y no modifican las reglas de negocio ni la GUI Swing.