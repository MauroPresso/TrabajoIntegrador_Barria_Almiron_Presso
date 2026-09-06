param(
    [switch]$Push
)

$ErrorActionPreference = "Stop"

$BaseBranch = "refactor/programacion-ii-etapa3-genericos"
$TargetBranch = "refactor/programacion-ii-etapa3b-dominio"
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

    if (-not $content.Contains($Old)) {
        throw "No se encontró el texto esperado en $Path:`n$Old"
    }

    $content = $content.Replace($Old, $New)
    Write-Utf8NoBom -Path $Path -Content $content
}

function Get-RelevantGitStatus {
    $temporaryNames = @(
        "SistemaDeAerolinea_ETAPA2_maven.ps1",
        "SistemaDeAerolinea_ETAPA2_maven_CORREGIDO.ps1",
        "SistemaDeAerolinea_ETAPA3A_genericos.ps1",
        "SistemaDeAerolinea_ETAPA3B_dominio.ps1",
        $ThisScriptName
    ) | Select-Object -Unique

    $lines = @(git status --porcelain)

    return @(
        $lines | Where-Object {
            $line = $_

            if (-not $line) {
                return $false
            }

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
Write-Host " SistemaDeAerolinea - Programacion II - ETAPA 3B"
Write-Host " Aerolinea como dominio puro + persistencia en Servicio"
Write-Host "============================================================"

Assert-Command "git"
Assert-Command "mvn"
Assert-Command "java"

if (-not (Test-Path ".git")) {
    throw "Ejecutá este script desde la raíz del repositorio SistemaDeAerolinea."
}

$requiredPaths = @(
    "pom.xml",
    "src/main/java/aerolinea/main/Main.java",
    "src/main/java/aerolinea/ui/Menu.java",
    "src/main/java/aerolinea/servicio/Aerolinea.java",
    "src/main/java/aerolinea/servicio/Servicio.java",
    "src/main/java/aerolinea/repositorio/RepositorioArchivo.java"
)

foreach ($path in $requiredPaths) {
    if (-not (Test-Path $path)) {
        throw "No se encontró el archivo esperado: $path. Partí desde la Etapa 3A."
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
Write-Host "[1/8] Moviendo Aerolinea desde servicio hacia dominio..."

git mv `
    "src/main/java/aerolinea/servicio/Aerolinea.java" `
    "src/main/java/aerolinea/dominio/Aerolinea.java"
Assert-LastExitCode "No se pudo mover Aerolinea.java al paquete dominio."

Write-Host "[2/8] Eliminando persistencia de Aerolinea..."

$aerolineaPath = "src/main/java/aerolinea/dominio/Aerolinea.java"
$oldAerolinea = Get-Content -Raw -Encoding UTF8 $aerolineaPath

$methodMarker = "    private void reconstruirPersonasDesdeVuelos()"
$methodIndex = $oldAerolinea.IndexOf($methodMarker)

if ($methodIndex -lt 0) {
    throw "No se encontró reconstruirPersonasDesdeVuelos() en Aerolinea.java."
}

$docIndex = $oldAerolinea.LastIndexOf("    /**", $methodIndex)

if ($docIndex -lt 0) {
    throw "No se encontró el bloque de documentación anterior a reconstruirPersonasDesdeVuelos()."
}

$tail = $oldAerolinea.Substring($docIndex)

$header = @'
package aerolinea.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import aerolinea.excepcion.VueloNoDisponibleException;
import aerolinea.util.ComparadorVueloPorDestino;
import aerolinea.util.ComparadorVueloPorNumero;

/**
 * @file Aerolinea.java
 * @brief Objeto principal del dominio del sistema de aerolínea.
 */

/**
 * @class Aerolinea
 * @brief Gestiona vuelos, pasajeros y tripulantes como reglas del dominio.
 *
 * <p>Esta clase no conoce archivos, repositorios ni mecanismos de
 * persistencia. Su responsabilidad es representar y operar el estado de una
 * aerolínea mediante objetos y colecciones Java.</p>
 *
 * <p>Conceptos demostrados:</p>
 * <ul>
 *   <li>List y ArrayList para vuelos.</li>
 *   <li>HashMap para indexar personas por DNI.</li>
 *   <li>HashSet para evitar duplicados de pasajeros con reserva activa.</li>
 *   <li>Comparable, Comparator y Collections.sort().</li>
 *   <li>Streams, lambdas y referencias a métodos.</li>
 *   <li>Herencia, polimorfismo y excepciones de negocio.</li>
 * </ul>
 */
public class Aerolinea {

    /** Nombre comercial de la aerolínea. */
    private final String nombre;

    /** Vuelos administrados por el dominio. */
    private final List<Vuelo> vuelos;

    /** Personas indexadas por DNI. */
    private final HashMap<Integer, Persona> personasPorDni;

    /** Pasajeros que poseen al menos una reserva activa. */
    private final HashSet<Persona> pasajerosConReservaActiva;

    /**
     * Crea una aerolínea vacía, completamente en memoria.
     *
     * @param nombre nombre comercial de la aerolínea
     */
    public Aerolinea(String nombre) {
        this(nombre, new ArrayList<>());
    }

    /**
     * Crea una aerolínea a partir de una colección inicial de vuelos.
     *
     * <p>La colección recibida se copia. De este modo el objeto de dominio
     * conserva su propio estado y no queda acoplado a la colección interna de
     * un servicio o repositorio.</p>
     *
     * @param nombre nombre comercial de la aerolínea
     * @param vuelosIniciales vuelos con los que se inicializa el dominio
     */
    public Aerolinea(String nombre, List<Vuelo> vuelosIniciales) {
        this.nombre = validarTextoObligatorio(nombre, "nombre de la aerolínea");

        if (vuelosIniciales == null) {
            throw new IllegalArgumentException("La lista inicial de vuelos no puede ser nula.");
        }

        this.vuelos = new ArrayList<>(vuelosIniciales);
        this.personasPorDni = new HashMap<>();
        this.pasajerosConReservaActiva = new HashSet<>();

        reconstruirPersonasDesdeVuelos();
    }

'@

$newAerolinea = $header + $tail

# Quitamos lenguaje de persistencia de los comentarios que ahora pertenecen al dominio.
$newAerolinea = $newAerolinea.Replace(
    "@brief Reconstruye las colecciones de personas a partir de los vuelos cargados.",
    "@brief Reconstruye las colecciones auxiliares de personas a partir de los vuelos del dominio."
)

$newAerolinea = $newAerolinea.Replace(
    "Como el requerimiento del TP pide persistir la lista de vuelos, los`n     * pasajeros y tripulantes asociados a esos vuelos se recuperan desde el`n     * propio grafo de objetos serializado.",
    "Los pasajeros y tripulantes asociados a cada vuelo forman parte del mismo`n     * grafo de objetos del dominio. A partir de ellos se reconstruyen los índices`n     * auxiliares utilizados por la aerolínea."
)

$newAerolinea = $newAerolinea.Replace(
    "@brief Registra una persona recuperada desde el archivo.",
    "@brief Registra una persona asociada a los vuelos iniciales."
)

$newAerolinea = $newAerolinea.Replace(
    "@param persona Persona recuperada desde los vuelos serializados.",
    "@param persona Persona asociada a alguno de los vuelos del dominio."
)

$newAerolinea = $newAerolinea.Replace(
    "registrarPersonaRecuperada",
    "registrarPersonaAsociada"
)

Write-Utf8NoBom -Path $aerolineaPath -Content $newAerolinea

Write-Host "[3/8] Agregando sincronización explícita a Servicio<T>..."

$servicioPath = "src/main/java/aerolinea/servicio/Servicio.java"
$servicioContent = Get-Content -Raw -Encoding UTF8 $servicioPath

if (-not $servicioContent.Contains("public void reemplazarTodos(List<T> nuevosElementos)")) {
    $insertBefore = @'
    public IRepositorio<T> getRepositorio() {
        return repositorio;
    }
'@

    if (-not $servicioContent.Contains($insertBefore)) {
        throw "No se encontró getRepositorio() en Servicio.java."
    }

    $replacement = @'
    /**
     * Reemplaza la colección administrada por una copia del estado recibido.
     *
     * <p>Permite sincronizar una capa de dominio con el servicio sin hacer que
     * el dominio conozca repositorios o archivos.</p>
     */
    public void reemplazarTodos(List<T> nuevosElementos) {
        if (nuevosElementos == null) {
            throw new IllegalArgumentException("La lista de elementos no puede ser nula.");
        }

        elementos.clear();
        elementos.addAll(nuevosElementos);
    }

    public IRepositorio<T> getRepositorio() {
        return repositorio;
    }
'@

    $servicioContent = $servicioContent.Replace($insertBefore, $replacement)
    Write-Utf8NoBom -Path $servicioPath -Content $servicioContent
}

Write-Host "[4/8] Reconfigurando Main como composición de capas..."

$mainPath = "src/main/java/aerolinea/main/Main.java"

$main = @'
package aerolinea.main;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.Vuelo;
import aerolinea.repositorio.IRepositorio;
import aerolinea.repositorio.RepositorioArchivo;
import aerolinea.servicio.Servicio;
import aerolinea.ui.Menu;

/**
 * Punto de entrada principal del Sistema de Aerolínea.
 *
 * <p>Main ensambla las capas de la aplicación:</p>
 *
 * <pre>
 * RepositorioArchivo&lt;Vuelo&gt;
 *          ↓
 *     Servicio&lt;Vuelo&gt;
 *          ↓
 *       Aerolinea
 *          ↓
 *         Menu
 * </pre>
 */
public class Main {

    public static void main(String[] args) {
        IRepositorio<Vuelo> repositorioVuelos =
                new RepositorioArchivo<>("data/vuelos.dat");

        Servicio<Vuelo> servicioVuelos =
                new Servicio<>(repositorioVuelos);

        Aerolinea aerolinea =
                new Aerolinea("Aerolínea IFES", servicioVuelos.listar());

        Menu menu =
                new Menu(aerolinea, servicioVuelos);

        menu.iniciar();
    }
}
'@

Write-Utf8NoBom -Path $mainPath -Content $main

Write-Host "[5/8] Separando dominio y persistencia dentro de Menu..."

$menuPath = "src/main/java/aerolinea/ui/Menu.java"

Replace-InFile `
    -Path $menuPath `
    -Old "import aerolinea.servicio.Aerolinea;" `
    -New "import aerolinea.dominio.Aerolinea;`nimport aerolinea.servicio.Servicio;"

Replace-InFile `
    -Path $menuPath `
    -Old @'
    /**
     * @brief Scanner utilizado para leer datos desde consola.
     */
    private final Scanner scanner;
'@ `
    -New @'
    /**
     * Servicio genérico encargado de persistir los vuelos.
     */
    private final Servicio<Vuelo> servicioVuelos;

    /**
     * @brief Scanner utilizado para leer datos desde consola.
     */
    private final Scanner scanner;
'@

Replace-InFile `
    -Path $menuPath `
    -Old @'
    public Menu(Aerolinea aerolinea) {
        this.aerolinea = aerolinea;
        this.scanner = new Scanner(System.in);
        this.cambiosSinGuardar = false;
    }
'@ `
    -New @'
    public Menu(Aerolinea aerolinea, Servicio<Vuelo> servicioVuelos) {
        if (aerolinea == null) {
            throw new IllegalArgumentException("La aerolínea no puede ser nula.");
        }

        if (servicioVuelos == null) {
            throw new IllegalArgumentException("El servicio de vuelos no puede ser nulo.");
        }

        this.aerolinea = aerolinea;
        this.servicioVuelos = servicioVuelos;
        this.scanner = new Scanner(System.in);
        this.cambiosSinGuardar = false;
    }
'@

Replace-InFile `
    -Path $menuPath `
    -Old "            aerolinea.guardarVuelos();" `
    -New "            servicioVuelos.reemplazarTodos(aerolinea.getVuelos());`n            servicioVuelos.guardar();"

# Ajustes documentales: Menu ya no considera Aerolinea una clase de servicio.
$menuContent = Get-Content -Raw -Encoding UTF8 $menuPath
$menuContent = $menuContent.Replace(
    "@brief Servicio principal de la aerolínea.",
    "@brief Objeto principal del dominio de la aerolínea."
)
$menuContent = $menuContent.Replace(
    "@param aerolinea Servicio principal del sistema.",
    "@param aerolinea Objeto principal del dominio."
)
$menuContent = $menuContent.Replace(
    "Además, dispara el guardado de vuelos luego de las operaciones que modifican`n * la lista de vuelos o sus reservas.",
    "Además, utiliza un Servicio<Vuelo> separado para persistir los cambios sin`n * acoplar el objeto de dominio Aerolinea al mecanismo de almacenamiento."
)
Write-Utf8NoBom -Path $menuPath -Content $menuContent

Write-Host "[6/8] Verificando separación de responsabilidades..."

if (Test-Path "src/main/java/aerolinea/servicio/Aerolinea.java") {
    throw "Aerolinea.java todavía existe dentro del paquete servicio."
}

$aerolineaCheck = Get-Content -Raw -Encoding UTF8 $aerolineaPath

$forbiddenDomainTerms = @(
    "aerolinea.repositorio",
    "IRepositorio<",
    "RepositorioArchivo",
    "java.io.IOException",
    "guardarVuelos(",
    "cargarVuelosPersistidos(",
    "tienePersistenciaHabilitada("
)

foreach ($term in $forbiddenDomainTerms) {
    if ($aerolineaCheck.Contains($term)) {
        throw "Aerolinea todavía contiene una dependencia de persistencia: $term"
    }
}

$allJava = Get-ChildItem "src/main/java" -Recurse -Filter "*.java"
$legacyImport = @()

foreach ($file in $allJava) {
    $content = Get-Content -Raw -Encoding UTF8 $file.FullName

    if ($content.Contains("aerolinea.servicio.Aerolinea")) {
        $legacyImport += $file.FullName
    }
}

if ($legacyImport.Count -gt 0) {
    Write-Host "Imports antiguos encontrados:"
    $legacyImport | ForEach-Object { Write-Host "  $_" }
    throw "Quedaron referencias a aerolinea.servicio.Aerolinea."
}

Write-Host "[7/8] Compilando y verificando con Maven..."

& mvn clean verify
Assert-LastExitCode "Falló mvn clean verify. No se hará commit."

$jarPath = "target/SistemaDeAerolinea-1.0-SNAPSHOT.jar"

if (-not (Test-Path $jarPath)) {
    throw "No se generó el JAR esperado."
}

Write-Host ""
Write-Host "Prueba funcional mínima: iniciar JAR y seleccionar opción 0..."

$output = "0" | & java -jar $jarPath 2>&1
$javaExit = $LASTEXITCODE

$output | ForEach-Object { Write-Host $_ }

if ($javaExit -ne 0) {
    throw "El JAR compiló pero falló la prueba funcional de inicio/salida."
}

Write-Host "[8/8] Creando commit de la Etapa 3B..."

$temporaryScripts = @(
    "SistemaDeAerolinea_ETAPA2_maven.ps1",
    "SistemaDeAerolinea_ETAPA2_maven_CORREGIDO.ps1",
    "SistemaDeAerolinea_ETAPA3A_genericos.ps1",
    "SistemaDeAerolinea_ETAPA3B_dominio.ps1",
    $ThisScriptName
) | Select-Object -Unique

foreach ($temporaryScript in $temporaryScripts) {
    $tracked = @(git ls-files -- "$temporaryScript")

    if ($tracked.Count -gt 0) {
        git rm --cached --ignore-unmatch "$temporaryScript"
        Assert-LastExitCode "No se pudo retirar $temporaryScript del índice de Git."
    }
}

git add "src/main/java"
Assert-LastExitCode "No se pudieron preparar las fuentes."

$staged = @(git diff --cached --name-only)

if ($staged.Count -gt 0) {
    git commit -m "Refactor: separar dominio Aerolinea de la persistencia"
    Assert-LastExitCode "No se pudo crear el commit de la Etapa 3B."
}
else {
    Write-Host "No hay cambios nuevos para confirmar."
}

if ($Push) {
    git push -u origin $TargetBranch
    Assert-LastExitCode "El commit local fue creado, pero falló el push."
}

Write-Host ""
Write-Host "============================================================"
Write-Host " ETAPA 3B COMPLETADA"
Write-Host " Rama: $TargetBranch"
Write-Host ""
Write-Host " Arquitectura resultante:"
Write-Host "   aerolinea.dominio.Aerolinea"
Write-Host "       -> reglas y colecciones del dominio"
Write-Host ""
Write-Host "   aerolinea.servicio.Servicio<Vuelo>"
Write-Host "       -> carga, sincronizacion y guardado"
Write-Host ""
Write-Host "   aerolinea.repositorio.RepositorioArchivo<Vuelo>"
Write-Host "       -> serializacion"
Write-Host ""
Write-Host " Validacion:"
Write-Host "   mvn clean verify   OK"
Write-Host "   JAR generado       OK"
Write-Host "   inicio/salida JAR  OK"
Write-Host "============================================================"

$statusFinal = @(git status --porcelain)

if ($statusFinal.Count -gt 0) {
    Write-Host ""
    Write-Host "Archivos locales/temporales no versionados:"
    $statusFinal | ForEach-Object { Write-Host "  $_" }
}
