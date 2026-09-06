# Arquitectura - Sistema de Aerolinea

## Objetivo

El proyecto busca reproducir de forma conceptual la organizacion y los contenidos del repositorio Biblioteca de la catedra, utilizando un dominio diferente.

## Capas

```text
+------------------------------+
|        Swing / Consola       |
|              ui              |
+--------------+---------------+
               |

+------------------------------+
|           Dominio            |
| Aerolinea / Vuelo / Persona  |
+--------------+---------------+
               | sincronizacion

+------------------------------+
|         Servicio<T>          |
|       IServicio<T>           |
+--------------+---------------+
               |

+------------------------------+
|      IRepositorio<T>         |
|   RepositorioArchivo<T>      |
+--------------+---------------+
               |

        archivos .dat
```

## Dominio

### Vuelo

`Vuelo` es una clase abstracta que implementa:

- `IOperable`
- `Comparable<Vuelo>`
- `Serializable`

Subclases:

```text
Vuelo
+-- VueloNacional
+-- VueloInternacional
+-- VueloCharter
```

Esto permite almacenar distintos tipos concretos en colecciones de tipo `Vuelo` y ejecutar comportamiento polimorfico.

### Persona

`Persona` es abstracta e implementa:

- `Comparable<Persona>`
- `Serializable`
- `equals`
- `hashCode`

Subclases:

```text
Persona
+-- Pasajero
+-- Tripulante
```

El DNI actua como identidad logica de una persona.

### Aerolinea

`Aerolinea` concentra reglas y colecciones del dominio.

Utiliza:

```text
List<Vuelo>
HashMap<Integer, Persona>
HashSet<Persona>
```

No conoce archivos ni repositorios.

## Persistencia generica

La interfaz:

```java
IRepositorio<T>
```

define el contrato de persistencia.

La implementacion:

```java
RepositorioArchivo<T>
```

utiliza serializacion Java y puede trabajar con diferentes tipos:

```java
RepositorioArchivo<Vuelo>
RepositorioArchivo<Persona>
```

## Servicio generico

```java
IServicio<T>
Servicio<T>
```

permiten trabajar contra `IRepositorio<T>` sin depender de una implementacion concreta.

## Interfaz grafica

`Ventana` extiende `JFrame`.

`PanelManager` usa `CardLayout` para administrar las vistas.

```text
PanelManager
+-- PanelPrincipal
+-- PanelVuelosFormulario
+-- PanelVuelosTabla
+-- PanelPasajerosFormulario
+-- PanelPasajerosTabla
+-- PanelReservas
```

`TableVuelosModel` y `TablePasajerosModel` extienden `AbstractTableModel` y actuan como adaptadores entre objetos del dominio y `JTable`.

## Eventos

Los controles Swing utilizan `ActionListener`, en gran parte mediante expresiones lambda:

```java
boton.addActionListener(
    evento -> manager.mostrarPanel(EnumPanel.TABLA_VUELOS)
);
```

La aplicacion grafica se inicia dentro del Event Dispatch Thread:

```java
SwingUtilities.invokeLater(...);
```

## Equivalencias conceptuales

| Biblioteca | Aerolinea |
|---|---|
| Biblioteca | Aerolinea |
| Material | Vuelo |
| Libro | VueloNacional |
| Revista | VueloInternacional |
| Cd | VueloCharter |
| Persona | Persona |
| Usuario | Pasajero |
| Bibliotecario | Tripulante |
| IPrestable | IOperable |
| Prestar | PanelReservas |
| TableMaterialesModel | TableVuelosModel |
| TableUsuarioModel | TablePasajerosModel |

Las equivalencias son pedagogicas; no implican que las entidades tengan el mismo significado de negocio.
