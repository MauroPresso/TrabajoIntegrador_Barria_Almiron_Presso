# Laboratorio 03 - Plugins, Goals y Site

Objetivo: distinguir una fase del lifecycle de una meta o goal de un plugin.

## Goal ligado a una fase

`maven-antrun-plugin` tiene una ejecucion llamada `mensaje-validacion`.
Su goal `run` esta ligado a la fase `validate`.

Por eso:

```powershell
mvn validate
```

ejecuta automaticamente esa meta.

## Goal invocado de forma explicita

`exec-maven-plugin` permite ejecutar la aplicacion:

```powershell
mvn compile exec:java
```

La forma general es:

```text
mvn plugin:goal
```

## Maven Site

Este laboratorio tambien configura `maven-site-plugin` y
`maven-project-info-reports-plugin`.

Generar el sitio:

```powershell
mvn site
```

Salida:

```text
target/site/
```

El sitio Maven es distinto de Doxygen. Doxygen documenta principalmente el codigo;
Maven Site genera reportes del proyecto Maven.

## Validacion completa

```powershell
mvn validate
mvn clean package
mvn exec:java
mvn site
```