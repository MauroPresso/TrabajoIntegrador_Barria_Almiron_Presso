param(
    [switch]$Push
)

$ErrorActionPreference = "Stop"

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
    [System.IO.File]::WriteAllText(
        (Resolve-Path $Path).Path,
        $Content,
        $utf8NoBom
    )
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

Write-Host "============================================================"
Write-Host " SistemaDeAerolinea - Programacion II - Etapa 1"
Write-Host " Reorganizacion de paquetes sin cambiar comportamiento"
Write-Host "============================================================"

Assert-Command "git"
Assert-Command "javac"

# Debe ejecutarse desde la raíz del repositorio.
if (-not (Test-Path ".git")) {
    throw "Ejecutá este script desde la raíz del repositorio SistemaDeAerolinea."
}

# Comprobaciones de identidad mínimas para evitar actuar sobre otro repo.
$required = @(
    "src/Main.java",
    "src/modelo/Vuelo.java",
    "src/modelo/Persona.java",
    "src/menu/Menu.java",
    "src/servicio/Aerolinea.java",
    "src/interfaces/IRepositorio.java",
    "src/interfaces/IOperable.java",
    "src/repositorio/RepositorioVuelosArchivo.java"
)

foreach ($path in $required) {
    if (-not (Test-Path $path)) {
        throw "No se encontró el archivo esperado: $path. Se cancela sin modificar."
    }
}

# Trabajamos sólo con un árbol limpio para poder revertir con seguridad.
$status = git status --porcelain
if ($status) {
    throw "El repositorio tiene cambios sin confirmar. Hacé commit/stash antes de ejecutar esta etapa."
}

# La serialización Java incluye el nombre completo de la clase (package + clase).
# Si existe un archivo creado con las clases antiguas modelo.*, lo respaldamos
# antes de cambiar a aerolinea.dominio.* para evitar cualquier pérdida.
if (Test-Path "data/vuelos.dat") {
    $backup = "data/vuelos.pre-etapa1.dat"
    Copy-Item -Force "data/vuelos.dat" $backup
    Write-Warning "Se encontró data/vuelos.dat. Se creó el respaldo $backup."
    Write-Warning "El archivo anterior puede no deserializar luego del cambio de package."
}

$branch = "refactor/programacion-ii-etapa1"
$currentBranch = (git branch --show-current).Trim()

if ($currentBranch -ne $branch) {
    $branchExists = git branch --list $branch
    if ($branchExists) {
        git switch $branch
    }
    else {
        git switch -c $branch
    }
}

Write-Host ""
Write-Host "[1/6] Creando estructura equivalente a Biblioteca..."

$dirs = @(
    "src/aerolinea/main",
    "src/aerolinea/dominio",
    "src/aerolinea/excepcion",
    "src/aerolinea/repositorio",
    "src/aerolinea/servicio",
    "src/aerolinea/ui",
    "src/aerolinea/util"
)

foreach ($dir in $dirs) {
    New-Item -ItemType Directory -Force -Path $dir | Out-Null
}

Write-Host "[2/6] Moviendo archivos con git mv..."

git mv "src/Main.java" "src/aerolinea/main/Main.java"

git mv "src/modelo/EstadoVuelo.java"         "src/aerolinea/dominio/EstadoVuelo.java"
git mv "src/modelo/Pasajero.java"            "src/aerolinea/dominio/Pasajero.java"
git mv "src/modelo/Persona.java"              "src/aerolinea/dominio/Persona.java"
git mv "src/modelo/Tripulante.java"           "src/aerolinea/dominio/Tripulante.java"
git mv "src/modelo/Vuelo.java"                "src/aerolinea/dominio/Vuelo.java"
git mv "src/modelo/VueloCharter.java"         "src/aerolinea/dominio/VueloCharter.java"
git mv "src/modelo/VueloInternacional.java"   "src/aerolinea/dominio/VueloInternacional.java"
git mv "src/modelo/VueloNacional.java"        "src/aerolinea/dominio/VueloNacional.java"
git mv "src/interfaces/IOperable.java"        "src/aerolinea/dominio/IOperable.java"

git mv "src/excepciones/VueloNoDisponibleException.java" `
       "src/aerolinea/excepcion/VueloNoDisponibleException.java"

git mv "src/interfaces/IRepositorio.java" `
       "src/aerolinea/repositorio/IRepositorio.java"
git mv "src/repositorio/RepositorioVuelosArchivo.java" `
       "src/aerolinea/repositorio/RepositorioVuelosArchivo.java"

git mv "src/servicio/Aerolinea.java" "src/aerolinea/servicio/Aerolinea.java"
git mv "src/menu/Menu.java"           "src/aerolinea/ui/Menu.java"

git mv "src/comparadores/ComparadorVueloPorDestino.java" `
       "src/aerolinea/util/ComparadorVueloPorDestino.java"
git mv "src/comparadores/ComparadorVueloPorNumero.java" `
       "src/aerolinea/util/ComparadorVueloPorNumero.java"

Write-Host "[3/6] Actualizando declaraciones package..."

# Main no tenía package.
$mainPath = "src/aerolinea/main/Main.java"
$main = Get-Content -Raw -Encoding UTF8 $mainPath
if (-not $main.StartsWith("package aerolinea.main;")) {
    $main = "package aerolinea.main;`r`n`r`n" + $main
    Write-Utf8NoBom -Path $mainPath -Content $main
}

Get-ChildItem "src/aerolinea/dominio" -Filter "*.java" | ForEach-Object {
    Replace-InFile $_.FullName "package modelo;" "package aerolinea.dominio;"
}
Replace-InFile "src/aerolinea/dominio/IOperable.java" `
               "package interfaces;" `
               "package aerolinea.dominio;"

Replace-InFile "src/aerolinea/excepcion/VueloNoDisponibleException.java" `
               "package excepciones;" `
               "package aerolinea.excepcion;"

Replace-InFile "src/aerolinea/repositorio/IRepositorio.java" `
               "package interfaces;" `
               "package aerolinea.repositorio;"
Replace-InFile "src/aerolinea/repositorio/RepositorioVuelosArchivo.java" `
               "package repositorio;" `
               "package aerolinea.repositorio;"

Replace-InFile "src/aerolinea/servicio/Aerolinea.java" `
               "package servicio;" `
               "package aerolinea.servicio;"

Replace-InFile "src/aerolinea/ui/Menu.java" `
               "package menu;" `
               "package aerolinea.ui;"

Get-ChildItem "src/aerolinea/util" -Filter "*.java" | ForEach-Object {
    Replace-InFile $_.FullName "package comparadores;" "package aerolinea.util;"
}

Write-Host "[4/6] Actualizando imports entre capas..."

$javaFiles = Get-ChildItem "src/aerolinea" -Recurse -Filter "*.java"

$replacements = [ordered]@{
    "import modelo."                         = "import aerolinea.dominio."
    "import excepciones."                   = "import aerolinea.excepcion."
    "import comparadores."                  = "import aerolinea.util."
    "import servicio."                      = "import aerolinea.servicio."
    "import menu."                          = "import aerolinea.ui."
    "import repositorio."                   = "import aerolinea.repositorio."
    "import interfaces.IRepositorio;"       = "import aerolinea.repositorio.IRepositorio;"
    "import interfaces.IOperable;"          = "import aerolinea.dominio.IOperable;"
}

foreach ($file in $javaFiles) {
    foreach ($entry in $replacements.GetEnumerator()) {
        Replace-InFile $file.FullName $entry.Key $entry.Value
    }
}

# Eliminamos imports redundantes producidos por mover interfaces al paquete
# que conceptualmente les corresponde, igual que en el repo Biblioteca.
$vueloPath = "src/aerolinea/dominio/Vuelo.java"
$vuelo = Get-Content -Raw -Encoding UTF8 $vueloPath
$vuelo = $vuelo.Replace("import aerolinea.dominio.IOperable;`r`n", "")
$vuelo = $vuelo.Replace("import aerolinea.dominio.IOperable;`n", "")
Write-Utf8NoBom -Path $vueloPath -Content $vuelo

$repoPath = "src/aerolinea/repositorio/RepositorioVuelosArchivo.java"
$repo = Get-Content -Raw -Encoding UTF8 $repoPath
$repo = $repo.Replace("import aerolinea.repositorio.IRepositorio;`r`n", "")
$repo = $repo.Replace("import aerolinea.repositorio.IRepositorio;`n", "")
Write-Utf8NoBom -Path $repoPath -Content $repo

Write-Host "[5/6] Verificando que no queden imports/packages viejos..."

$legacyPatterns = @(
    "package modelo;",
    "package interfaces;",
    "package comparadores;",
    "package excepciones;",
    "package repositorio;",
    "package servicio;",
    "package menu;",
    "import modelo.",
    "import interfaces.",
    "import comparadores.",
    "import excepciones.",
    "import repositorio.",
    "import servicio.",
    "import menu."
)

$legacyFound = @()
foreach ($file in (Get-ChildItem "src/aerolinea" -Recurse -Filter "*.java")) {
    $content = Get-Content -Raw -Encoding UTF8 $file.FullName
    foreach ($pattern in $legacyPatterns) {
        if ($content.Contains($pattern)) {
            $legacyFound += "$($file.FullName): $pattern"
        }
    }
}

if ($legacyFound.Count -gt 0) {
    Write-Host "Se encontraron referencias antiguas:"
    $legacyFound | ForEach-Object { Write-Host "  $_" }
    throw "La validación de paquetes/imports falló."
}

Write-Host "[6/6] Compilando todo el proyecto con javac..."

$buildDir = ".build-etapa1"
if (Test-Path $buildDir) {
    Remove-Item -Recurse -Force $buildDir
}
New-Item -ItemType Directory -Force -Path $buildDir | Out-Null

$sourceFiles = Get-ChildItem "src/aerolinea" -Recurse -Filter "*.java" |
    Select-Object -ExpandProperty FullName

& javac -encoding UTF-8 -d $buildDir $sourceFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "La compilación falló. NO se hará commit."
    Write-Host "Podés volver al estado anterior con:"
    Write-Host "  git reset --hard HEAD"
    throw "Falló javac."
}

Remove-Item -Recurse -Force $buildDir

Write-Host ""
Write-Host "Compilación OK."
Write-Host ""
git status --short

git add src

$staged = git diff --cached --name-only
if (-not $staged) {
    Write-Host "No hay cambios para confirmar."
    exit 0
}

git commit -m "Refactor: organizar SistemaDeAerolinea con estructura de Programacion II"

if ($LASTEXITCODE -ne 0) {
    throw "No se pudo crear el commit."
}

if ($Push) {
    Write-Host ""
    Write-Host "Subiendo rama a GitHub..."
    git push -u origin $branch
    if ($LASTEXITCODE -ne 0) {
        throw "El commit local fue creado, pero falló el push."
    }
}

Write-Host ""
Write-Host "============================================================"
Write-Host " ETAPA 1 COMPLETADA"
Write-Host " Rama: $branch"
Write-Host " Estructura resultante:"
Write-Host "   src/aerolinea/main"
Write-Host "   src/aerolinea/dominio"
Write-Host "   src/aerolinea/excepcion"
Write-Host "   src/aerolinea/repositorio"
Write-Host "   src/aerolinea/servicio"
Write-Host "   src/aerolinea/ui"
Write-Host "   src/aerolinea/util"
Write-Host "============================================================"

if (-not $Push) {
    Write-Host ""
    Write-Host "Para publicar la rama en GitHub:"
    Write-Host "  git push -u origin $branch"
}
