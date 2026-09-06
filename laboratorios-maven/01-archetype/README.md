# Laboratorio 01 - Maven Archetype

Objetivo: reproducir el tipo de estructura que genera un archetype Maven Quickstart.

Comando equivalente:

```powershell
mvn archetype:generate `
  -DgroupId=ar.edu.ifes.aerolinea `
  -DartifactId=archetype-demo `
  -Dversion=1.0-SNAPSHOT `
  -DarchetypeArtifactId=maven-archetype-quickstart `
  -DinteractiveMode=false
```

El resultado sigue la estructura estandar:

```text
archetype-demo/
+-- pom.xml
+-- src/
    +-- main/java/
    +-- test/java/
```

Conceptos demostrados:

- archetype;
- Quickstart;
- `groupId`, `artifactId`, `version`;
- `packaging` JAR;
- `src/main/java`;
- `src/test/java`;
- ciclo `compile`, `test`, `package`.

Validar:

```powershell
mvn clean test
mvn package
```