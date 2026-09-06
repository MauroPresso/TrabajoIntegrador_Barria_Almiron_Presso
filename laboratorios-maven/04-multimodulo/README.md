# Laboratorio 04 - Maven Multimodulo, WAR, JSP y Jetty

Objetivo: demostrar un proyecto padre con `packaging` POM y dos modulos que dependen
entre si.

Estructura:

```text
04-multimodulo/
+-- pom.xml                    <- padre/agregador, packaging pom
+-- aerolinea-core/
|   +-- pom.xml                <- modulo JAR
|   +-- src/main/java/
+-- aerolinea-web/
    +-- pom.xml                <- modulo WAR
    +-- src/main/webapp/
        +-- index.jsp
        +-- WEB-INF/web.xml
```

## Reactor

El POM padre declara:

```xml
<modules>
    <module>aerolinea-core</module>
    <module>aerolinea-web</module>
</modules>
```

Cuando se ejecuta Maven desde esta carpeta, el Reactor determina el orden correcto.
`aerolinea-web` depende de `aerolinea-core`, por lo que `core` se construye primero.

## Packaging

- padre: `pom`;
- `aerolinea-core`: `jar`;
- `aerolinea-web`: `war`.

## Dependencia entre modulos

El modulo web declara `aerolinea-core` como dependencia Maven. El JSP importa una
clase de ese JAR para demostrar que los modulos pueden depender entre si.

## Scope provided

La API Servlet se declara con scope `provided` porque el contenedor web la aporta
durante la ejecucion.

## Compilar e instalar todo

```powershell
mvn clean install
```

`install` coloca los artefactos construidos en el repositorio local `~/.m2/repository`.

## Ejecutar la aplicacion web con Jetty

```powershell
mvn -pl aerolinea-web jetty:run
```

- `-pl aerolinea-web`: selecciona el modulo web.
- El paso previo `mvn clean install` deja `aerolinea-core` disponible en `.m2`.

Abrir:

```text
http://localhost:8080/aerolinea/
```

Detener Jetty con `Ctrl+C`.