# Laboratorios Maven - Programacion II

Esta carpeta complementa la aplicacion principal `SistemaDeAerolinea` con ejercicios
aislados para demostrar los contenidos Maven trabajados en las clases.

La aplicacion principal sigue siendo un proyecto Maven `jar` independiente. Estos
laboratorios no alteran su arquitectura Swing ni su modelo de dominio.

## Mapa de contenidos

| Laboratorio | Conceptos |
|---|---|
| `01-archetype` | archetype, Quickstart, GAV, estructura estandar |
| `02-dependencias` | dependencias, Maven Central, repositorio local, scopes, transitivas |
| `03-plugins-goals-site` | plugins, goals, ejecuciones ligadas a fases, `mvn site` |
| `04-multimodulo` | POM padre, modulos, Reactor, dependencia entre modulos, JAR, WAR, JSP, Jetty |
| `05-ciclo-repositorios` | lifecycle, `install`, `deploy`, repositorio local y remoto |

## Regla de trabajo

Cada laboratorio se ejecuta desde su propia carpeta. Los `target/` generados por
Maven estan ignorados por Git.

## Validacion sugerida

Desde la raiz del repositorio:

```powershell
mvn clean verify

cd .\laboratorios-maven\01-archetype
mvn clean test

cd ..\02-dependencias
mvn clean test
mvn dependency:tree

cd ..\03-plugins-goals-site
mvn clean package
mvn exec:java
mvn site

cd ..\04-multimodulo
mvn clean install

cd ..\05-ciclo-repositorios
mvn clean deploy
```

Para ejecutar el laboratorio web:

```powershell
cd .\laboratorios-maven\04-multimodulo
mvn -pl aerolinea-web jetty:run
```

Abrir:

```text
http://localhost:8080/aerolinea/
```

Detener Jetty con `Ctrl+C`.