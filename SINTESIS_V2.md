# Síntesis V2 — Expansión Funcional

**Proyecto:** SGVA (Sistema de Gestión Visual de Datos para la Autoevaluación Académica)
**Periodo:** P-007
**Estado:** Cerrado — 136 pruebas / 0 fallos, ArchUnit 8/8, SonarQube Rating A (0.1% deuda técnica)

---

## 1. Alcance implementado

| Capa | Contenido | Prompts |
|---|---|---|
| Dominio | `TipoEstamento`, `RespuestaEncuesta`, `PonderacionLikert`, `MatrizRadar`/`SerieRadar`, `CalificacionFueraDeRangoException` | P-007 |
| Casos de uso | Registro y consulta de respuestas de encuesta; ingesta masiva (`ProcesarRespuestasEncuestasUseCase`); `CalcularPonderacionLikertUseCase`; `GenerarMatrizRadarUseCase` | P-007 |
| Infraestructura | Extensión de los parsers CSV/Excel existentes (sin modificar `CsvDocumentParser`/`ExcelDocumentParser`); tercer almacén en el repositorio en memoria; `EncuestaController` | P-007 |

Con el cierre de P-007 quedan completos los tres módulos de `DESIGN_SGVA.md` §2 (Factores 2, 3 y percepción), satisfaciendo la definición de V2 dada en la Fase 4 del anteproyecto: expansión funcional por acumulación de requerimientos.

---

## 2. Hallazgos metodológicos acumulados

### 2.1 Restauraciones Arquitectónicas Autodetectadas (0 eventos)

Ninguna en esta sesión — primera iteración desde P-004 sin ningún conflicto con `ArchitectureTest`. Responde parcialmente la pregunta abierta dejada en `SINTESIS_V1.md` §4 ("¿la ausencia de restauraciones en P-005/P-006 es aprendizaje o coincidencia?"): con P-007 se acumulan ya tres iteraciones consecutivas sin eventos (P-005, P-006, P-007), pero ninguna de las tres requirió las construcciones sintéticas de Kotlin (companions, funciones inline generando clases anónimas) que causaron los 4 eventos de V1. No se puede aún distinguir aprendizaje de coincidencia de ausencia de la condición que dispara el problema.

### 2.2 Decisión de diseño no trivial: nuevos tipos en vez de reutilizar `IndicadorCalidad`

`PonderacionLikert` y `MatrizRadar` se introdujeron como tipos de dominio nuevos en lugar de forzar las salidas del Módulo 3 dentro de `IndicadorCalidad`, citando el propio texto de `DESIGN_SGVA.md` §1, que ata esa entidad explícitamente a los Módulos 1 y 2. Alternativa descartada explícitamente: agregar un `facultad` sentinel a `IndicadorCalidad`.

### 2.3 Precedente de composición entre casos de uso

`GenerarMatrizRadarUseCase` se construyó por composición sobre `CalcularPonderacionLikertUseCase`, en vez de repetir el acceso al repositorio — el primer caso de uso de cálculo del proyecto que depende de otro caso de uso en vez de solo del repositorio.

### 2.4 Ritmo de construcción distinto a V1

V1 dividió trabajo de alcance comparable en varios prompts (P-004/P-005 para los Módulos 1-2, P-006 aparte para REST). V2 completó el Módulo 3 de punta a punta —entidad, puertos, casos de uso, infraestructura y REST— en un solo prompt. Dato de interés para comparar el ritmo de construcción entre iteraciones; no se investigó la causa.

---

## 3. Evolución de la deuda técnica (SonarQube, Overall Code)

| Iteración | Rating | Debt Ratio | Issues nuevos |
|---|---|---|---|
| P-007 | A | 0.1% | 0 |

Sin cambio respecto al cierre de V1 (0.1%).

---

## 4. Preguntas abiertas para V3–V4

- ¿Se sostiene la ausencia de restauraciones arquitectónicas (P-005 a P-007, tres iteraciones consecutivas) cuando V3/V4 introduzcan cambios de infraestructura más profundos, que podrían requerir construcciones más complejas?
- Si el Dashboard (fuera de alcance de este backend) espera un contrato único para "todo lo que es un indicador", la coexistencia de `IndicadorCalidad` y `PonderacionLikert`/`MatrizRadar` como formas de salida distintas podría requerir una capa de traducción no contemplada en el diseño actual.
- La pregunta de V1 sobre si la decisión de no usar DTOs de solicitud/respuesta se sostiene bajo cambios de infraestructura más profundos sigue abierta — V2 no introdujo cambios de infraestructura, solo extensión funcional.
