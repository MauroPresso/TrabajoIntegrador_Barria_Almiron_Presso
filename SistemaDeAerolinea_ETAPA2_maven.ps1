param(
    [switch]$Push
)

$ErrorActionPreference = "Stop"

$BaseBranch = "refactor/programacion-ii-etapa1"
$TargetBranch = "refactor/programacion-ii-etapa2-maven"
$ThisScriptName = Split-Path -Leaf $PSCommandPath

function Assert-Command {
    param([string]$Name)
    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "No se encontró '$Name' en PATH."
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

function Get-RelevantGitStatus {
    $lines = @(git status --porcelain)
    return @(
        $lines | Where-Object {
            $_ -and ($_ -notmatch [regex]::Escape($ThisScriptName) + '$')
        }
    )
}

function Assert-LastExitCode {
    param([string]$Message)
    if ($LASTEXITCODE -ne 0) {
        throw $Message
    }
}

Write-Host "============================================================"
Write-Host " SistemaDeAerolinea - Programacion II - ETAPA 2"
Write-Host " Migracion a Maven + estructura estandar + JAR ejecutable"
Write-Host "============================================================"

Assert-Command "git"
Assert-Command "java"
Assert-Command "javac"
Assert-Command "jar"
Assert-Command "mvn"

if (-not (Test-Path ".git")) {
    throw "Ejecutá este script desde la raíz del repositorio SistemaDeAerolinea."
}

# La etapa debe comenzar exactamente desde la estructura validada en la Etapa 1.
if (-not (Test-Path "src/aerolinea/main/Main.java") -and
    -not (Test-Path "src/main/java/aerolinea/main/Main.java")) {
    throw "No se encontró la estructura de la Etapa 1. Se cancela sin modificar."
}

$status = Get-RelevantGitStatus
if ($status.Count -gt 0) {
    Write-Host "Cambios detectados:"
    $status | ForEach-Object { Write-Host "  $_" }
    throw "El repositorio tiene cambios sin confirmar. Hacé commit/stash antes de ejecutar esta etapa."
}

$currentBranch = (git branch --show-current).Trim()

if ($currentBranch -ne $BaseBranch -and $currentBranch -ne $TargetBranch) {
    throw "La rama actual es '$currentBranch'. Cambiá primero a '$BaseBranch' y volvé a ejecutar."
}

if ($currentBranch -eq $BaseBranch) {
    # git branch --list devuelve vacío/null cuando la rama todavía no existe.
    # Lo convertimos siempre en array para evitar llamar .Trim() sobre $null.
    $localTarget = @(git branch --list $TargetBranch)

    if ($localTarget.Count -gt 0) {
        git switch $TargetBranch
        Assert-LastExitCode "No se pudo cambiar a la rama $TargetBranch."
    }
    else {
        git switch -c $TargetBranch
        Assert-LastExitCode "No se pudo crear la rama $TargetBranch."
    }
}

Write-Host ""
Write-Host "[1/8] Verificando Java y Maven..."

$javacText = (& javac -version 2>&1 | Out-String).Trim()
Write-Host "  $javacText"

$match = [regex]::Match($javacText, 'javac\s+(\d+)(?:\.(\d+))?')
if ($match.Success) {
    $major = [int]$match.Groups[1].Value
    if ($major -eq 1 -and $match.Groups[2].Success) {
        $major = [int]$match.Groups[2].Value
    }

    if ($major -lt 17) {
        throw "Se requiere JDK 17 o superior para esta configuración Maven. JDK detectado: $javacText"
    }
}

& mvn -version
Assert-LastExitCode "Maven está instalado pero no pudo ejecutarse correctamente."

Write-Host ""
Write-Host "[2/8] Migrando al layout estándar de Maven..."

if (Test-Path "src/aerolinea") {
    New-Item -ItemType Directory -Force -Path "src/main/java" | Out-Null
    git mv "src/aerolinea" "src/main/java/aerolinea"
    Assert-LastExitCode "No se pudo mover src/aerolinea a src/main/java/aerolinea."
}

if (-not (Test-Path "src/main/java/aerolinea/main/Main.java")) {
    throw "La migración de fuentes no produjo la ruta esperada de Main.java."
}

# Dejamos visible la estructura estándar de pruebas aunque JUnit se incorporará después.
New-Item -ItemType Directory -Force -Path "src/test/java/aerolinea" | Out-Null
$gitkeep = "src/test/java/aerolinea/.gitkeep"
if (-not (Test-Path $gitkeep)) {
    Write-Utf8NoBom -Path $gitkeep -Content ""
}

Write-Host "[3/8] Creando pom.xml..."

$pom = @'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <!-- Coordenadas Maven del proyecto -->
    <groupId>ar.edu.ifes</groupId>
    <artifactId>sistema-de-aerolinea</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>SistemaDeAerolinea</name>
    <description>
        Proyecto académico de Programación II: sistema de aerolínea análogo
        al proyecto Biblioteca de la cátedra.
    </description>

    <properties>
        <!-- Java 17 se usa como nivel de compatibilidad de compilación. -->
        <maven.compiler.release>17</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>

    <!--
        En esta etapa el proyecto no necesita bibliotecas externas.
        Las dependencias de testing (JUnit) se incorporarán cuando
        construyamos los tests automatizados.
    -->
    <dependencies>
    </dependencies>

    <build>
        <!-- Nombre pedagógicamente legible del artefacto generado. -->
        <finalName>SistemaDeAerolinea-${project.version}</finalName>

        <plugins>
            <!-- Compila las fuentes Java durante las fases compile/test/package/etc. -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <release>${maven.compiler.release}</release>
                    <encoding>${project.build.sourceEncoding}</encoding>
                </configuration>
            </plugin>

            <!-- Genera un JAR ejecutable indicando cuál es el método main. -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.4.2</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>aerolinea.main.Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
'@

Write-Utf8NoBom -Path "pom.xml" -Content $pom

Write-Host "[4/8] Actualizando .gitignore..."

$gitignorePath = ".gitignore"
$gitignore = Get-Content -Raw -Encoding UTF8 $gitignorePath

if ($gitignore -notmatch '(?m)^/target/\s*$') {
    if (-not $gitignore.EndsWith("`n")) {
        $gitignore += "`r`n"
    }

    $gitignore += @'

# Salida estándar de Maven
/target/
'@

    Write-Utf8NoBom -Path $gitignorePath -Content $gitignore
}

# Los scripts de migración son herramientas temporales, no forman parte
# del código fuente final del proyecto.
if (Test-Path "SistemaDeAerolinea_ETAPA1_refactor.ps1") {
    git rm "SistemaDeAerolinea_ETAPA1_refactor.ps1"
    Assert-LastExitCode "No se pudo retirar el script temporal de la Etapa 1."
}

# Si algún script de la Etapa 2 fue agregado por accidente al repositorio,
# lo quitamos solamente del índice de Git. El archivo físico que está
# ejecutándose queda disponible hasta finalizar PowerShell.
$temporaryStage2Scripts = @(
    "SistemaDeAerolinea_ETAPA2_maven.ps1",
    $ThisScriptName
) | Select-Object -Unique

foreach ($temporaryScript in $temporaryStage2Scripts) {
    $trackedScript = @(git ls-files -- "$temporaryScript")
    if ($trackedScript.Count -gt 0) {
        git rm --cached --ignore-unmatch "$temporaryScript"
        Assert-LastExitCode "No se pudo retirar $temporaryScript del índice de Git."
    }
}

Write-Host "[5/8] Validando estructura Maven..."

$expectedPaths = @(
    "pom.xml",
    "src/main/java/aerolinea/main/Main.java",
    "src/main/java/aerolinea/dominio/Vuelo.java",
    "src/main/java/aerolinea/dominio/Persona.java",
    "src/main/java/aerolinea/repositorio/IRepositorio.java",
    "src/main/java/aerolinea/repositorio/RepositorioVuelosArchivo.java",
    "src/main/java/aerolinea/servicio/Aerolinea.java",
    "src/main/java/aerolinea/ui/Menu.java",
    "src/main/java/aerolinea/util/ComparadorVueloPorDestino.java"
)

foreach ($path in $expectedPaths) {
    if (-not (Test-Path $path)) {
        throw "Falta un elemento esperado de la estructura Maven: $path"
    }
}

if (Test-Path "src/aerolinea") {
    throw "Todavía existe src/aerolinea. La migración Maven quedó incompleta."
}

Write-Host "[6/8] Ejecutando el ciclo de vida Maven..."

Write-Host ""
Write-Host "---- mvn clean ----"
& mvn clean
Assert-LastExitCode "Falló la fase Maven clean."

Write-Host ""
Write-Host "---- mvn compile ----"
& mvn compile
Assert-LastExitCode "Falló la fase Maven compile."

Write-Host ""
Write-Host "---- mvn package ----"
& mvn package
Assert-LastExitCode "Falló la fase Maven package."

Write-Host ""
Write-Host "---- mvn verify ----"
& mvn verify
Assert-LastExitCode "Falló la fase Maven verify."

Write-Host "[7/8] Verificando el JAR generado..."

$jarPath = "target/SistemaDeAerolinea-1.0-SNAPSHOT.jar"

if (-not (Test-Path $jarPath)) {
    throw "Maven finalizó pero no se encontró el JAR esperado: $jarPath"
}

$jarEntries = @(& jar tf $jarPath)
Assert-LastExitCode "No se pudo inspeccionar el contenido del JAR."

if ($jarEntries -notcontains "aerolinea/main/Main.class") {
    throw "El JAR no contiene aerolinea/main/Main.class."
}

Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead((Resolve-Path $jarPath).Path)

try {
    $manifestEntry = $zip.GetEntry("META-INF/MANIFEST.MF")
    if ($null -eq $manifestEntry) {
        throw "El JAR no contiene META-INF/MANIFEST.MF."
    }

    $reader = New-Object System.IO.StreamReader($manifestEntry.Open())
    try {
        $manifest = $reader.ReadToEnd()
    }
    finally {
        $reader.Dispose()
    }

    if ($manifest -notmatch 'Main-Class:\s*aerolinea\.main\.Main') {
        Write-Host $manifest
        throw "El MANIFEST.MF no declara Main-Class: aerolinea.main.Main."
    }
}
finally {
    $zip.Dispose()
}

Write-Host "  JAR válido: $jarPath"
Write-Host "  Main-Class: aerolinea.main.Main"

Write-Host "[8/8] Creando commit de la Etapa 2..."

git add "pom.xml" ".gitignore" "src"
Assert-LastExitCode "No se pudieron preparar los cambios para el commit."

$staged = @(git diff --cached --name-only)
if ($staged.Count -eq 0) {
    Write-Host "No hay cambios nuevos para confirmar."
}
else {
    git commit -m "Build: migrar SistemaDeAerolinea a Maven"
    Assert-LastExitCode "No se pudo crear el commit de la Etapa 2."
}

if ($Push) {
    Write-Host ""
    Write-Host "Subiendo rama a GitHub..."
    git push -u origin $TargetBranch
    Assert-LastExitCode "El commit local fue creado, pero falló el push."
}

Write-Host ""
Write-Host "============================================================"
Write-Host " ETAPA 2 COMPLETADA"
Write-Host " Rama: $TargetBranch"
Write-Host ""
Write-Host " Fases verificadas:"
Write-Host "   mvn clean"
Write-Host "   mvn compile"
Write-Host "   mvn package"
Write-Host "   mvn verify"
Write-Host ""
Write-Host " Artefacto:"
Write-Host "   target/SistemaDeAerolinea-1.0-SNAPSHOT.jar"
Write-Host ""
Write-Host " Estructura:"
Write-Host "   pom.xml"
Write-Host "   src/main/java/aerolinea/..."
Write-Host "   src/test/java/aerolinea/..."
Write-Host "   target/   (ignorado por Git)"
Write-Host "============================================================"

if (-not $Push) {
    Write-Host ""
    Write-Host "Para publicar la rama:"
    Write-Host "  git push -u origin $TargetBranch"
}

$statusFinal = @(git status --porcelain)
if ($statusFinal.Count -gt 0) {
    Write-Host ""
    Write-Host "Nota: hay archivos no confirmados/temporales:"
    $statusFinal | ForEach-Object { Write-Host "  $_" }
    Write-Host "El script de esta Etapa puede quedar como archivo local sin agregarse al repo."
}
