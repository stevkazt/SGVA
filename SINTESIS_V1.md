# Síntesis V1 — Construcción del Núcleo

**Proyecto:** SGVA (Sistema de Gestión Visual de Datos para la Autoevaluación Académica)
**Periodo:** P-001 a P-006
**Estado:** Cerrado — 102 pruebas / 0 fallos, ArchUnit 8/8, SonarQube Rating A (0.1% deuda técnica)

---

## 1. Alcance implementado

| Capa | Contenido | Prompts |
|---|---|---|
| Dominio | `Estudiante`, `Docente`, `IndicadorCalidad`, `ParametrosDeAnalisis`, `EquivalenciaTiempoCompleto`, jerarquía de excepciones de dominio | P-001, P-004, P-005 |
| Casos de uso | Registro y consulta de estudiantes/docentes; ingesta de archivos (`IngestarArchivoUseCase`); cinco casos de uso de indicadores de calidad (Módulos 1 y 2 completos según `DESIGN_SGVA.md` §2) | P-002 – P-005 |
| Infraestructura | `AcademicDataRepositoryEnMemoria`; parsers CSV/Excel (`CsvDocumentParser`, `ExcelDocumentParser`) con normalización centralizada; adaptador REST completo (`EstudianteController`, `DocenteController`, `IndicadorController`, `ManejadorGlobalDeErrores`) | P-002, P-003, P-006 |

Con el cierre de P-006 se satisface la definición de V1 dada en la Fase 4 del anteproyecto: carga de archivos, validación, almacenamiento ligero y visualización inicial de indicadores (expuesta vía API REST).

---

## 2. Hallazgos metodológicos acumulados

### 2.1 Restauraciones Arquitectónicas Autodetectadas (4 eventos)

Todas comparten la misma causa raíz: la regla de sufijo `UseCase` de `ArchitectureTest.kt` se aplica a *toda* clase no-interface del paquete `usecases`, incluyendo construcciones sintéticas del compilador de Kotlin (companions, clases anónimas de funciones inline), no solo clases de negocio.

| ID | Causa técnica | Corrección |
|---|---|---|
| P-002 | Clases de test en el paquete `usecases` | Reubicadas a `application` |
| P-003 | DTOs `ResultadoIngesta`/`RegistroOmitido` en `usecases` | Reubicados a `domain` |
| P-004 | `companion object` genera clase anidada sin sufijo `UseCase` | Constantes movidas a `domain` |
| P-004 | `groupingBy` genera clase anónima en `usecases` | Sustituido por `groupBy().mapValues()` |

**Decisión metodológica:** la regla se mantuvo sin modificar durante V1, para no introducir circularidad en la evaluación (la Fase 5 del anteproyecto exige que las reglas ArchUnit queden bloqueadas antes de Fase 3). Documentado en `TESTS_ARQUITECTONICOS.md` §5.

### 2.2 Desviación de diseño aceptada y documentada

**FastCSV en lugar de OpenCSV (P-003):** `DESIGN_SGVA.md` §4 especificaba `OpenCsvParser`. Claude Code CLI sustituyó la librería por FastCSV, justificando la decisión (ausencia de dependencias transitivas, alineación con el stack de Spring Boot 4) y señalándola explícitamente como "decisión a revisar" antes de que el investigador la aceptara. `DESIGN_SGVA.md` fue actualizado en consecuencia.

### 2.3 Ambigüedades de diseño manejadas de forma transparente

En ambos casos, la IA evitó inventar datos o políticas no especificadas, documentando explícitamente el vacío en vez de resolverlo silenciosamente:

- **Referencia nacional Saber Pro (P-004):** `DESIGN_SGVA.md` §0 declara esta información fuera de alcance. `ConsolidarPuntajesSaberProUseCase` calcula únicamente promedios propios (global y por cohorte), sin inventar un valor de referencia.
- **Factores de conversión a Tiempo Completo Equivalente (P-005):** sin política definida en el diseño. Se introdujo `EquivalenciaTiempoCompleto` con factores provisionales (TC=1.0, MEDIO_TIEMPO=0.5, CATEDRA=0.25), documentados explícitamente como supuesto pendiente de confirmar con la normativa institucional.

### 2.4 Inconsistencia menor autoinducida

**P-006:** al corregir el renombramiento de RFC 9110 (`isUnprocessableEntity` → `isUnprocessableContent`), la corrección se aplicó completamente en las pruebas pero no en el código de producción equivalente (`HttpStatus.UNPROCESSABLE_ENTITY`, deprecado), generando un issue de SonarQube (`kotlin:S1874`, Low) detectado y corregido en la misma sesión de revisión. Sugiere que el conocimiento de la API se aplicó localmente a cada archivo más que como regla global consistente.

---

## 3. Evolución de la deuda técnica (SonarQube, Overall Code)

| Iteración | Rating | Debt Ratio | Issues nuevos |
|---|---|---|---|
| P-001 | A | — | 0 (2 preexistentes) |
| P-002 | A | 0.0% | 0 |
| P-003 | A | 0.2% | 3 (versión hardcodeada de nuevas dependencias) |
| P-004 | A | 0.1% | 0 |
| P-005 | A | 0.1% | 0 |
| P-006 | A | 0.1% (tras corrección) | 1 (corregido) |

La única iteración con deuda técnica nueva no trivial fue P-003 — la única que introdujo dependencias externas nuevas al proyecto (FastCSV, Apache POI).

---

## 4. Preguntas abiertas para V2–V4

- ¿Se sostiene la decisión de no usar DTOs de solicitud/respuesta (serializar entidades de dominio directamente) cuando V3/V4 introduzcan cambios de infraestructura más profundos?
- ¿La ausencia de restauraciones arquitectónicas en P-005 y P-006 es indicio de que la IA "aprendió" a evitar las construcciones problemáticas de Kotlin, o coincidencia de que esas sesiones no requirieron construcciones sintéticas equivalentes? Vigilar en V2.
- Confirmar con la institución los factores reales de conversión a Tiempo Completo Equivalente antes de que `CalcularCapacidadInstaladaUseCase` se use para reportes reales.
