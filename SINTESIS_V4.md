# Síntesis V4 — Estrés Arquitectónico: Integración Externa (PDF)

**Proyecto:** SGVA (Sistema de Gestión Visual de Datos para la Autoevaluación Académica)
**Periodo:** P-009
**Estado:** Cerrado — 145 pruebas / 0 fallos, ArchUnit 8/8, SonarQube Rating A (0.1% deuda técnica)

---

## 1. Alcance implementado

| Capa | Contenido | Prompts |
|---|---|---|
| Dominio | `ReporteEjecutivo` — value object puro, sin referencia a PDF o renderizado | P-009 |
| Casos de uso | `GenerarReporteEjecutivoUseCase`, compuesto sobre los cinco casos de uso de indicadores más `ConsultarRespuestasEncuestaUseCase` | P-009 |
| Infraestructura | `GeneradorReportePdf` (Apache PDFBox), única clase del codebase con conocimiento de renderizado PDF; `ReporteController` | P-009 |

Con el cierre de P-009 se satisface la definición de V4 dada en la Fase 4 del anteproyecto: sustitución del mecanismo de entrega de información mediante una librería externa de generación de reportes, verificando el aislamiento de los casos de uso frente a la nueva infraestructura de salida.

---

## 2. Hallazgos metodológicos acumulados

### 2.1 Restauraciones Arquitectónicas Autodetectadas (0 eventos)

Ninguna en esta sesión. Quinta iteración consecutiva sin eventos (P-005 a P-009), pese a que esta sesión introdujo una superficie de código nueva (nueva librería externa, nuevo caso de uso, nuevo controlador) comparable en tamaño a P-003 — la única iteración de V1 con eventos de este tipo. Refuerza, sin confirmar de forma concluyente, que la ausencia de restauraciones no depende del tamaño de la sesión sino de si se usan las construcciones específicas de Kotlin (companions, funciones inline con clases anónimas) que las causaron en V1.

### 2.2 Aislamiento verificado sin respaldo mecánico específico

A diferencia de la sustitución de persistencia en P-008 (donde `ArchitectureTest` ya prohibía explícitamente que el dominio dependiera de `jakarta..`), ninguna regla de `ArchitectureTest.kt` nombra librerías de PDF. El investigador verificó directamente, mediante `grep` sobre el código fuente, que ninguna referencia a Apache PDFBox aparece en `domain` ni en `usecases`. El aislamiento se sostuvo sin que ninguna regla automatizada lo forzara específicamente para esta tecnología.

### 2.3 Decisiones de negocio no especificadas, resueltas sin inventar datos

Indicadores sin datos suficientes se omiten del reporte en vez de fallar la generación completa; la tasa de deserción exige una `cohorte` explícita, por no tener un valor por defecto sensato. Ninguna estaba especificada en `DESIGN_SGVA.md`.

### 2.4 Autoclasificación de versión correcta

Contraste directo con P-008: en esta sesión la IA identificó por sí misma que el trabajo correspondía a V4, citando las mismas notas anticipatorias del repositorio que en P-008 estaban disponibles pero no se aplicaron sin corrección del investigador. Un acierto después de un fallo no constituye evidencia de un patrón de aprendizaje dentro de la sesión — se documenta como par de datos, no como tendencia.

### 2.5 Tercera confirmación de la regularidad de deuda por dependencia no gestionada por Spring

Un issue nuevo por versión hardcodeada de Apache PDFBox — mismo patrón que P-003 (FastCSV, Apache POI) y en contraste con P-008 (JPA/PostgreSQL, gestionados por el BOM de Spring, sin issues nuevos). Ya no es un hallazgo nuevo sino una regularidad confirmada de forma independiente en tres iteraciones.

### 2.6 Decisión de no usar DTOs sostenida bajo un segundo tipo de cambio de infraestructura

`ReporteController` reutiliza las entidades de dominio existentes sin introducir DTOs de respuesta nuevos más allá de `ReporteEjecutivo` mismo (que es dominio puro). La pregunta abierta desde `SINTESIS_V1.md` §4 se sostiene también bajo un cambio de infraestructura de salida (PDF), no solo bajo el cambio de persistencia visto en V3.

---

## 3. Evolución de la deuda técnica (SonarQube, Overall Code)

| Iteración | Rating | Debt Ratio | Issues nuevos |
|---|---|---|---|
| P-009 | A | 0.1% | 1 (versión hardcodeada de Apache PDFBox) |

Con el cierre de V4 se completa el ciclo experimental V1–V4 definido en la Fase 4 del anteproyecto. Ningún escenario de estrés estructural (V3, V4) produjo un Debt Ratio mayor que el observado en construcción incremental (V1, V2); ninguna iteración se acercó al umbral de degradación severa (>10%) ni registró una violación arquitectónica no autocorregida en su medición final.

---

## 4. Preguntas abiertas para el análisis final

- ¿Por qué los dos escenarios de estrés estructural (V3, V4) no produjeron peores resultados que la construcción incremental? Ver `ANALISIS_HIPOTESIS.md` para las explicaciones candidatas.
- La racha de cinco iteraciones consecutivas sin restauraciones arquitectónicas (P-005 a P-009) — ¿es evidencia de que el patrón de V1 (P-002 a P-004) fue específico de las primeras iteraciones del proyecto, cuando aún se establecían las convenciones, o coincidencia de que las sesiones posteriores no requirieron las construcciones sintéticas problemáticas?
- Confirmar con la institución los factores reales de conversión a Tiempo Completo Equivalente (P-005) antes de que cualquier resultado de `CalcularCapacidadInstaladaUseCase` se cite como dato institucional válido.
