# Laboratorio 05 - Lifecycle y Repositorios Maven

Objetivo: relacionar las fases principales del lifecycle con los repositorios Maven.

## Fases y comandos

```powershell
mvn validate
mvn compile
mvn test
mvn package
mvn verify
mvn install
mvn deploy
```

Cada fase incluye las anteriores del mismo lifecycle.

Ejemplos:

- `package` genera el JAR o WAR;
- `verify` ejecuta verificaciones posteriores al empaquetado;
- `install` copia el artefacto al repositorio local;
- `deploy` publica el artefacto en un repositorio remoto configurado.

## Repositorio local

Ruta habitual:

```text
~/.m2/repository
```

## Deploy seguro para el laboratorio

Este laboratorio configura `distributionManagement` con un repositorio de archivos
dentro de `target/`. Funciona como un repositorio remoto simulado y no publica nada
en Internet.

Ejecutar:

```powershell
mvn clean deploy
```

La salida de deploy queda bajo:

```text
target/repositorio-remoto-demo/
```

Como `target/` es generado por Maven, Git no versiona ese repositorio de practica.

## Diferencia entre install y deploy

```text
install -> publica para otros proyectos de la misma computadora
deploy  -> publica hacia el repositorio configurado en distributionManagement
```

En un proyecto real, `deploy` normalmente apunta a Nexus, Artifactory u otro
repositorio remoto y puede requerir credenciales en `settings.xml`.

## Maven Central

Maven Central es un repositorio remoto publico usado principalmente para resolver
dependencias de terceros. No debe confundirse con el repositorio local `.m2`.