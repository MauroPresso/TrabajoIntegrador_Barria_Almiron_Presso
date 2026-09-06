# Guia de estudio y validacion

## Requisitos

- JDK 17 o superior.
- Maven disponible en `PATH`.
- Git para control de versiones.

Comprobar:

```powershell
java -version
javac -version
mvn -version
git --version
```

## Validacion Maven

```powershell
mvn clean verify
```

La compilacion correcta genera:

```text
target/classes/
target/test-classes/
target/SistemaDeAerolinea-1.0-SNAPSHOT.jar
```

## Ejecucion grafica

```powershell
java -jar target/SistemaDeAerolinea-1.0-SNAPSHOT.jar
```

## Ejecucion por consola

```powershell
java -jar target/SistemaDeAerolinea-1.0-SNAPSHOT.jar --consola
```

## Laboratorios

Despues de ejecutar `mvn clean verify`:

```powershell
java -cp "target/test-classes;target/classes" aerolinea.test.TestPolimorfismo
java -cp "target/test-classes;target/classes" aerolinea.test.JavaCollectionFrameworkTest
java -cp "target/test-classes;target/classes" aerolinea.test.TestLambdaStreams
java -cp "target/test-classes;target/classes" aerolinea.test.TestSobrecargaMetodos
java -cp "target/test-classes;target/classes" aerolinea.test.TestArchivos
java -cp "target/test-classes;target/classes" aerolinea.test.TestManejadorDeEventos
java -cp "target/test-classes;target/classes" aerolinea.test.UiTest
java -cp "target/test-classes;target/classes" aerolinea.test.UiDatosTest
```

## Preguntas que el proyecto permite explicar

### Donde hay herencia?

```text
Persona -> Pasajero / Tripulante
Vuelo -> Nacional / Internacional / Charter
```

### Donde hay polimorfismo?

Una referencia `Vuelo` puede apuntar a cualquiera de sus tres subclases.

### Donde se usa una interfaz?

`Vuelo` implementa `IOperable`.

`Servicio<T>` trabaja contra `IRepositorio<T>`.

### Donde se usan genericos?

```text
IRepositorio<T>
RepositorioArchivo<T>
IServicio<T>
Servicio<T>
```

### Donde se usa Comparable?

`Persona` y `Vuelo`.

### Donde se usa Comparator?

Comparadores externos para ordenar vuelos por criterios alternativos.

### Donde se usan Collections?

En las colecciones internas de `Aerolinea` y en los laboratorios pedagogicos.

### Donde se usan lambdas y Streams?

En busquedas, filtros, ordenamientos, sumas y eventos Swing.

### Como se persisten los datos?

Por serializacion Java a traves de `RepositorioArchivo<T>`.

### Que responsabilidad tiene Main?

Crear e interconectar repositorios, servicios, dominio e interfaz.

### Que responsabilidad tiene Aerolinea?

Reglas de negocio y estado del dominio, sin conocer persistencia.

### Que responsabilidad tiene PanelManager?

Administrar navegacion, servicios de la UI, sincronizacion y actualizacion de modelos Swing.

## Validacion de laboratorios Maven

Los laboratorios especificos de Maven se encuentran en:

```text
laboratorios-maven/
```

Validacion completa:

```powershell
cd .\laboratorios-maven\01-archetype
mvn clean test

cd ..\02-dependencias
mvn clean test
mvn dependency:tree

cd ..\03-plugins-goals-site
mvn validate
mvn clean package
mvn exec:java
mvn site

cd ..\04-multimodulo
mvn clean install

cd ..\05-ciclo-repositorios
mvn clean deploy
```

Para Jetty:

```powershell
cd .\laboratorios-maven\04-multimodulo
mvn -pl aerolinea-web jetty:run
```

Luego abrir `http://localhost:8080/aerolinea/` y detener con `Ctrl+C`.