# Doxygen

This folder contains the Doxygen configuration for SistemaDeAerolinea.

## Scope

Doxygen processes:

- src/main/java
- src/test/java
- README.md
- docs/ARQUITECTURA.md
- docs/GUIA_ESTUDIO.md

Every Java source has an explicit `@file` header and every Java package has a
`package-info.java` file with package-level documentation.

The configuration also enables:

- EXTRACT_ALL
- EXTRACT_PRIVATE
- EXTRACT_STATIC
- EXTRACT_LOCAL_CLASSES
- EXTRACT_LOCAL_METHODS
- SOURCE_BROWSER
- INLINE_SOURCES

Therefore public, protected, package-private, private, and static members are
included in the generated documentation.

## Generate HTML

From repository root:

```powershell
doxygen .\docs\doxygen\Doxyfile
```

Open:

```powershell
start .\docs\doxygen\html\index.html
```

Generated HTML is intentionally not stored in Git because it can be recreated
from source code and the Doxyfile.
