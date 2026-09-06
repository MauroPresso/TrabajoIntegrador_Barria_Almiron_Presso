# GuÃ­a de estudio y validaciÃ³n

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

## ValidaciÃ³n Maven

```powershell
mvn clean verify
```

La compilaciÃ³n correcta genera:

```text
target/classes/
target/test-classes/
target/SistemaDeAerolinea-1.0-SNAPSHOT.jar
```

## EjecuciÃ³n grÃ¡fica

```powershell
java -jar target/SistemaDeAerolinea-1.0-SNAPSHOT.jar
```

## EjecuciÃ³n por consola

```powershell
java -jar target/SistemaDeAerolinea-1.0-SNAPSHOT.jar --consola
```

## Laboratorios

DespuÃ©s de ejecutar `mvn clean verify`:

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

### Â¿DÃ³nde hay herencia?

```text
Persona -> Pasajero / Tripulante
Vuelo -> Nacional / Internacional / Charter
```

### Â¿DÃ³nde hay polimorfismo?

Una referencia `Vuelo` puede apuntar a cualquiera de sus tres subclases.

### Â¿DÃ³nde se usa una interfaz?

`Vuelo` implementa `IOperable`.

`Servicio<T>` trabaja contra `IRepositorio<T>`.

### Â¿DÃ³nde se usan genÃ©ricos?

```text
IRepositorio<T>
RepositorioArchivo<T>
IServicio<T>
Servicio<T>
```

### Â¿DÃ³nde se usa Comparable?

`Persona` y `Vuelo`.

### Â¿DÃ³nde se usa Comparator?

Comparadores externos para ordenar vuelos por criterios alternativos.

### Â¿DÃ³nde se usan Collections?

En las colecciones internas de `Aerolinea` y en los laboratorios pedagÃ³gicos.

### Â¿DÃ³nde se usan lambdas y Streams?

En bÃºsquedas, filtros, ordenamientos, sumas y eventos Swing.

### Â¿CÃ³mo se persisten los datos?

Por serializaciÃ³n Java a travÃ©s de `RepositorioArchivo<T>`.

### Â¿QuÃ© responsabilidad tiene Main?

Crear e interconectar repositorios, servicios, dominio e interfaz.

### Â¿QuÃ© responsabilidad tiene Aerolinea?

Reglas de negocio y estado del dominio, sin conocer persistencia.

### Â¿QuÃ© responsabilidad tiene PanelManager?

Administrar navegaciÃ³n, servicios de la UI, sincronizaciÃ³n y actualizaciÃ³n de modelos Swing.