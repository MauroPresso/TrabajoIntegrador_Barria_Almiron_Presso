# Laboratorio 02 - Dependencias Maven

Objetivo: demostrar gestion de dependencias sin descargar JAR manualmente.

Este laboratorio usa:

- `org.apache.commons:commons-lang3` con scope `compile` por defecto;
- un BOM de JUnit con scope `import`;
- `org.junit.jupiter:junit-jupiter` con scope `test`;
- `maven-surefire-plugin` para ejecutar JUnit 5.

`NormalizadorCodigoVuelo` importa `StringUtils` desde Commons Lang.

## Maven Central y repositorio local

Al ejecutar Maven por primera vez, los artefactos se resuelven desde repositorios
remotos como Maven Central y se almacenan en el repositorio local del usuario:

```text
~/.m2/repository
```

En Windows normalmente corresponde a:

```text
C:\Users\<usuario>\.m2\repository
```

## Dependencias transitivas

JUnit Jupiter agrega otras bibliotecas necesarias. Para ver el arbol completo:

```powershell
mvn dependency:tree
```

Esto permite distinguir dependencias directas de dependencias transitivas.

## Scopes

Scopes demostrados realmente en los laboratorios:

- `compile`: Commons Lang en este laboratorio;
- `test`: JUnit Jupiter en este laboratorio;
- `import`: JUnit BOM en `dependencyManagement`;
- `provided`: Servlet API en el laboratorio multimodulo.

Scopes documentados, pero no forzados sin necesidad:

- `runtime`: necesario al ejecutar pero no al compilar;
- `system`: ruta local explicita; se desaconseja para proyectos portables.

## Validacion

```powershell
mvn clean test
mvn dependency:tree
```