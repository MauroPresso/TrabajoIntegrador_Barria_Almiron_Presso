# Arquitectura â€” Sistema de AerolÃ­nea

## Objetivo

El proyecto busca reproducir de forma conceptual la organizaciÃ³n y los contenidos del repositorio Biblioteca de la cÃ¡tedra, utilizando un dominio diferente.

## Capas

```text
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚        Swing / Consola       â”‚
â”‚              ui              â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
               â”‚
               â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚           Dominio            â”‚
â”‚ Aerolinea / Vuelo / Persona  â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
               â”‚ sincronizaciÃ³n
               â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚         Servicio<T>          â”‚
â”‚       IServicio<T>           â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
               â”‚
               â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚      IRepositorio<T>         â”‚
â”‚   RepositorioArchivo<T>      â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
               â”‚
               â–¼
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
â”œâ”€â”€ VueloNacional
â”œâ”€â”€ VueloInternacional
â””â”€â”€ VueloCharter
```

Esto permite almacenar distintos tipos concretos en colecciones de tipo `Vuelo` y ejecutar comportamiento polimÃ³rfico.

### Persona

`Persona` es abstracta e implementa:

- `Comparable<Persona>`
- `Serializable`
- `equals`
- `hashCode`

Subclases:

```text
Persona
â”œâ”€â”€ Pasajero
â””â”€â”€ Tripulante
```

El DNI actÃºa como identidad lÃ³gica de una persona.

### Aerolinea

`Aerolinea` concentra reglas y colecciones del dominio.

Utiliza:

```text
List<Vuelo>
HashMap<Integer, Persona>
HashSet<Persona>
```

No conoce archivos ni repositorios.

## Persistencia genÃ©rica

La interfaz:

```java
IRepositorio<T>
```

define el contrato de persistencia.

La implementaciÃ³n:

```java
RepositorioArchivo<T>
```

utiliza serializaciÃ³n Java y puede trabajar con diferentes tipos:

```java
RepositorioArchivo<Vuelo>
RepositorioArchivo<Persona>
```

## Servicio genÃ©rico

```java
IServicio<T>
Servicio<T>
```

permiten trabajar contra `IRepositorio<T>` sin depender de una implementaciÃ³n concreta.

## Interfaz grÃ¡fica

`Ventana` extiende `JFrame`.

`PanelManager` usa `CardLayout` para administrar las vistas.

```text
PanelManager
â”œâ”€â”€ PanelPrincipal
â”œâ”€â”€ PanelVuelosFormulario
â”œâ”€â”€ PanelVuelosTabla
â”œâ”€â”€ PanelPasajerosFormulario
â”œâ”€â”€ PanelPasajerosTabla
â””â”€â”€ PanelReservas
```

`TableVuelosModel` y `TablePasajerosModel` extienden `AbstractTableModel` y actÃºan como adaptadores entre objetos del dominio y `JTable`.

## Eventos

Los controles Swing utilizan `ActionListener`, en gran parte mediante expresiones lambda:

```java
boton.addActionListener(
    evento -> manager.mostrarPanel(EnumPanel.TABLA_VUELOS)
);
```

La aplicaciÃ³n grÃ¡fica se inicia dentro del Event Dispatch Thread:

```java
SwingUtilities.invokeLater(...);
```

## Equivalencias conceptuales

| Biblioteca | AerolÃ­nea |
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

Las equivalencias son pedagÃ³gicas; no implican que las entidades tengan el mismo significado de negocio.