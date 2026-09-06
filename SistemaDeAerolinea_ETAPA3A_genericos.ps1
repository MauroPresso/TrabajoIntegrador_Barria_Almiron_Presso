param(
    [switch]$Push
)

$ErrorActionPreference = "Stop"

$BaseBranch = "refactor/programacion-ii-etapa2-maven"
$TargetBranch = "refactor/programacion-ii-etapa3-genericos"
$ThisScriptName = Split-Path -Leaf $PSCommandPath

function Assert-Command {
    param([string]$Name)
    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "No se encontró '$Name' en PATH."
    }
}

function Assert-LastExitCode {
    param([string]$Message)
    if ($LASTEXITCODE -ne 0) {
        throw $Message
    }
}

function Write-Utf8NoBom {
    param(
        [string]$Path,
        [string]$Content
    )

    $utf8NoBom = New-Object -TypeName System.Text.UTF8Encoding -ArgumentList $false

    if (Test-Path $Path) {
        $resolved = (Resolve-Path $Path).Path
    }
    else {
        $resolved = [System.IO.Path]::GetFullPath((Join-Path (Get-Location) $Path))
    }

    [System.IO.File]::WriteAllText($resolved, $Content, $utf8NoBom)
}

function Replace-InFile {
    param(
        [string]$Path,
        [string]$Old,
        [string]$New
    )

    $content = Get-Content -Raw -Encoding UTF8 $Path
    $content = $content.Replace($Old, $New)
    Write-Utf8NoBom -Path $Path -Content $content
}

function Get-RelevantGitStatus {
    $temporaryNames = @(
        "SistemaDeAerolinea_ETAPA2_maven.ps1",
        "SistemaDeAerolinea_ETAPA2_maven_CORREGIDO.ps1",
        "SistemaDeAerolinea_ETAPA3A_genericos.ps1",
        $ThisScriptName
    ) | Select-Object -Unique

    $lines = @(git status --porcelain)

    return @(
        $lines | Where-Object {
            $line = $_
            if (-not $line) { return $false }

            foreach ($name in $temporaryNames) {
                if ($line -match [regex]::Escape($name) + '$') {
                    return $false
                }
            }

            return $true
        }
    )
}

Write-Host "============================================================"
Write-Host " SistemaDeAerolinea - Programacion II - ETAPA 3A"
Write-Host " Repositorio generico + Servicio generico"
Write-Host "============================================================"

Assert-Command "git"
Assert-Command "mvn"

if (-not (Test-Path ".git")) {
    throw "Ejecutá este script desde la raíz del repositorio SistemaDeAerolinea."
}

$requiredPaths = @(
    "pom.xml",
    "src/main/java/aerolinea/main/Main.java",
    "src/main/java/aerolinea/repositorio/IRepositorio.java",
    "src/main/java/aerolinea/repositorio/RepositorioVuelosArchivo.java",
    "src/main/java/aerolinea/servicio/Aerolinea.java"
)

foreach ($path in $requiredPaths) {
    if (-not (Test-Path $path)) {
        throw "No se encontró el archivo esperado: $path. Partí desde la Etapa 2."
    }
}

$status = Get-RelevantGitStatus
if ($status.Count -gt 0) {
    Write-Host "Cambios detectados:"
    $status | ForEach-Object { Write-Host "  $_" }
    throw "El repositorio tiene cambios sin confirmar. Hacé commit/stash antes de continuar."
}

$currentBranch = (git branch --show-current).Trim()

if ($currentBranch -ne $BaseBranch -and $currentBranch -ne $TargetBranch) {
    throw "La rama actual es '$currentBranch'. Cambiá primero a '$BaseBranch'."
}

if ($currentBranch -eq $BaseBranch) {
    $localTarget = @(git branch --list $TargetBranch)

    if ($localTarget.Count -gt 0) {
        git switch $TargetBranch
        Assert-LastExitCode "No se pudo cambiar a $TargetBranch."
    }
    else {
        git switch -c $TargetBranch
        Assert-LastExitCode "No se pudo crear $TargetBranch."
    }
}

Write-Host ""
Write-Host "[1/7] Generalizando RepositorioVuelosArchivo -> RepositorioArchivo<T>..."

git mv `
    "src/main/java/aerolinea/repositorio/RepositorioVuelosArchivo.java" `
    "src/main/java/aerolinea/repositorio/RepositorioArchivo.java"
Assert-LastExitCode "No se pudo renombrar el repositorio."

$repositorioArchivo = @'
package aerolinea.repositorio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio genérico basado en serialización Java.
 *
 * <p>La clase no conoce el tipo concreto que persiste. Puede utilizarse,
 * por ejemplo, como {@code RepositorioArchivo<Vuelo>} o
 * {@code RepositorioArchivo<Persona>}.</p>
 *
 * @param <T> tipo de elemento administrado por el repositorio
 */
public class RepositorioArchivo<T> implements IRepositorio<T> {

    private final Path rutaArchivo;

    /**
     * Crea un repositorio para una ruta de archivo determinada.
     *
     * @param rutaArchivo ruta del archivo de persistencia
     */
    public RepositorioArchivo(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía.");
        }

        this.rutaArchivo = Path.of(rutaArchivo);
    }

    /**
     * Guarda la lista completa de elementos mediante serialización.
     */
    @Override
    public void guardar(List<T> elementos) throws IOException {
        if (elementos == null) {
            throw new IllegalArgumentException("La lista de elementos no puede ser nula.");
        }

        Path carpeta = rutaArchivo.getParent();

        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }

        try (ObjectOutputStream salida = new ObjectOutputStream(
                new FileOutputStream(rutaArchivo.toFile()))) {

            salida.writeObject(new ArrayList<>(elementos));
        }
    }

    /**
     * Recupera la lista serializada.
     *
     * <p>Por el borrado de tipos (type erasure) de Java, el tipo genérico T
     * no puede verificarse directamente en tiempo de ejecución. La coherencia
     * del tipo queda garantizada por el uso consistente del repositorio.</p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> consultar() throws IOException, ClassNotFoundException {
        if (!Files.exists(rutaArchivo)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream entrada = new ObjectInputStream(
                new FileInputStream(rutaArchivo.toFile()))) {

            Object objetoLeido = entrada.readObject();

            if (!(objetoLeido instanceof List<?>)) {
                throw new IOException("El archivo no contiene una lista válida.");
            }

            return new ArrayList<>((List<T>) objetoLeido);
        }
    }

    public Path getRutaArchivo() {
        return rutaArchivo;
    }
}
'@

Write-Utf8NoBom `
    -Path "src/main/java/aerolinea/repositorio/RepositorioArchivo.java" `
    -Content $repositorioArchivo

Write-Host "[2/7] Creando IServicio<T>..."

$iservicio = @'
package aerolinea.servicio;

import java.util.List;

/**
 * Contrato genérico de una capa de servicio.
 *
 * <p>Es análogo a IServicio&lt;T&gt; del proyecto Biblioteca del profesor:
 * permite agregar elementos y obtener la colección administrada sin depender
 * de una clase concreta.</p>
 *
 * @param <T> tipo de elemento administrado
 */
public interface IServicio<T> {

    void agregar(T elemento);

    List<T> listar();
}
'@

Write-Utf8NoBom `
    -Path "src/main/java/aerolinea/servicio/IServicio.java" `
    -Content $iservicio

Write-Host "[3/7] Creando Servicio<T> conectado a IRepositorio<T>..."

$servicio = @'
package aerolinea.servicio;

import aerolinea.repositorio.IRepositorio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación genérica de servicio respaldada por un repositorio.
 *
 * <p>La clase aplica composición y genéricos: Servicio&lt;T&gt; trabaja contra
 * la abstracción IRepositorio&lt;T&gt;, no contra una implementación concreta.</p>
 *
 * @param <T> tipo de elemento administrado
 */
public class Servicio<T> implements IServicio<T> {

    private final IRepositorio<T> repositorio;
    private final List<T> elementos;

    /**
     * Inicializa el servicio recuperando los elementos persistidos.
     *
     * <p>Si todavía no hay datos, comienza con una lista vacía. Si existe un
     * problema de lectura, se informa y se permite iniciar el servicio vacío,
     * manteniendo el criterio tolerante utilizado por el sistema actual.</p>
     */
    public Servicio(IRepositorio<T> repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo.");
        }

        this.repositorio = repositorio;
        this.elementos = new ArrayList<>();

        try {
            List<T> recuperados = repositorio.consultar();

            if (recuperados != null) {
                elementos.addAll(recuperados);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(
                    "No se pudieron recuperar los elementos persistidos: " + e.getMessage());
        }
    }

    /**
     * Agrega un elemento a la colección administrada.
     *
     * <p>La persistencia explícita se realiza mediante {@link #guardar()}.
     * Esto es útil en el Sistema de Aerolínea porque algunas operaciones
     * modifican objetos ya existentes, por ejemplo una reserva dentro de un vuelo.</p>
     */
    @Override
    public void agregar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento no puede ser nulo.");
        }

        elementos.add(elemento);
    }

    /**
     * Devuelve una vista de la lista administrada.
     *
     * <p>Se conserva la misma instancia para que una capa de dominio pueda
     * operar sobre los objetos cargados. La encapsulación definitiva se
     * resolverá en la Etapa 3B al separar Aerolinea de la persistencia.</p>
     */
    @Override
    public List<T> listar() {
        return elementos;
    }

    /**
     * Persiste el estado actual de la colección.
     */
    public void guardar() throws IOException {
        repositorio.guardar(new ArrayList<>(elementos));
    }

    public IRepositorio<T> getRepositorio() {
        return repositorio;
    }
}
'@

Write-Utf8NoBom `
    -Path "src/main/java/aerolinea/servicio/Servicio.java" `
    -Content $servicio

Write-Host "[4/7] Usando el repositorio genérico en la aplicación real..."

$mainPath = "src/main/java/aerolinea/main/Main.java"
Replace-InFile `
    -Path $mainPath `
    -Old "import aerolinea.repositorio.RepositorioVuelosArchivo;" `
    -New "import aerolinea.repositorio.RepositorioArchivo;"

Replace-InFile `
    -Path $mainPath `
    -Old "new RepositorioVuelosArchivo();" `
    -New 'new RepositorioArchivo<Vuelo>("data/vuelos.dat");'

# Limpieza de documentación de IRepositorio para que ya no describa sólo Aerolinea.
$irepoPath = "src/main/java/aerolinea/repositorio/IRepositorio.java"
Replace-InFile `
    -Path $irepoPath `
    -Old "Esta interfaz permite desacoplar las clases de servicio del mecanismo`n * concreto de persistencia. De esta forma, Aerolinea puede trabajar contra`n * una abstracción sin saber si los datos se guardan en archivos, base de datos`n * u otro medio." `
    -New "Esta interfaz permite desacoplar las clases de servicio del mecanismo`n * concreto de persistencia. Cualquier servicio genérico puede trabajar contra`n * esta abstracción sin saber si los datos se guardan en archivos, base de datos`n * u otro medio."

Write-Host "[5/7] Verificando referencias antiguas..."

$javaFiles = Get-ChildItem "src/main/java" -Recurse -Filter "*.java"
$legacy = @()

foreach ($file in $javaFiles) {
    $content = Get-Content -Raw -Encoding UTF8 $file.FullName

    if ($content.Contains("RepositorioVuelosArchivo")) {
        $legacy += $file.FullName
    }
}

if ($legacy.Count -gt 0) {
    Write-Host "Referencias antiguas detectadas:"
    $legacy | ForEach-Object { Write-Host "  $_" }
    throw "Quedaron referencias a RepositorioVuelosArchivo."
}

Write-Host "[6/7] Ejecutando Maven verify..."

& mvn clean verify
Assert-LastExitCode "Falló mvn clean verify. No se hará commit."

$jarPath = "target/SistemaDeAerolinea-1.0-SNAPSHOT.jar"
if (-not (Test-Path $jarPath)) {
    throw "No se generó el JAR esperado después de Maven verify."
}

Write-Host "[7/7] Preparando commit..."

# Los scripts auxiliares no forman parte del código académico final.
$temporaryScripts = @(
    "SistemaDeAerolinea_ETAPA2_maven.ps1",
    "SistemaDeAerolinea_ETAPA2_maven_CORREGIDO.ps1",
    "SistemaDeAerolinea_ETAPA3A_genericos.ps1",
    $ThisScriptName
) | Select-Object -Unique

foreach ($temporaryScript in $temporaryScripts) {
    $tracked = @(git ls-files -- "$temporaryScript")
    if ($tracked.Count -gt 0) {
        git rm --cached --ignore-unmatch "$temporaryScript"
        Assert-LastExitCode "No se pudo retirar $temporaryScript del índice."
    }
}

git add "src/main/java"
Assert-LastExitCode "No se pudieron preparar las fuentes."

$staged = @(git diff --cached --name-only)

if ($staged.Count -gt 0) {
    git commit -m "Refactor: incorporar repositorio y servicio genericos"
    Assert-LastExitCode "No se pudo crear el commit."
}
else {
    Write-Host "No hay cambios nuevos para confirmar."
}

if ($Push) {
    git push -u origin $TargetBranch
    Assert-LastExitCode "El commit existe localmente, pero falló el push."
}

Write-Host ""
Write-Host "============================================================"
Write-Host " ETAPA 3A COMPLETADA"
Write-Host " Rama: $TargetBranch"
Write-Host ""
Write-Host " Conceptos incorporados:"
Write-Host "   IRepositorio<T>"
Write-Host "   RepositorioArchivo<T>"
Write-Host "   IServicio<T>"
Write-Host "   Servicio<T>"
Write-Host "   composicion Servicio -> IRepositorio"
Write-Host "   persistencia generica por serializacion"
Write-Host ""
Write-Host " Validacion:"
Write-Host "   mvn clean verify  OK"
Write-Host "   JAR generado      OK"
Write-Host "============================================================"

$statusFinal = @(git status --porcelain)
if ($statusFinal.Count -gt 0) {
    Write-Host ""
    Write-Host "Archivos locales/temporales no versionados:"
    $statusFinal | ForEach-Object { Write-Host "  $_" }
}
