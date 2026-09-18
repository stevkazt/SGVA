# Análisis Comparativo V1–V4 y Validación de la Hipótesis de Investigación

**Corresponde a:** Fase 5 (Medición y Evaluación) y Sección 10 (Hipótesis de Investigación) del anteproyecto.

---

## 1. Hipótesis planteada (§10 del anteproyecto)

> "El uso de inteligencia artificial generativa como asistencia principal para la codificación iterativa bajo Arquitectura Limpia, cuando se prioriza la rapidez funcional sobre la intervención estructural humana, produce un incremento progresivo en el Ratio de Deuda Técnica y en la Tasa de Violaciones Arquitectónicas ante escenarios de estrés estructural (cambio de dependencias o tecnologías externas)."

La hipótesis predice, específicamente:
1. Una tendencia **creciente** en el Ratio de Deuda Técnica a lo largo de las iteraciones.
2. Un incremento en la Tasa de Violaciones Arquitectónicas, **particularmente** en los escenarios de estrés estructural (V3: sustitución de persistencia; V4: integración externa).

---

## 2. Datos observados

### 2.1 Tasa de Violaciones Arquitectónicas (ArchUnit)

| Iteración | Resultado ArchUnit | Violaciones en la medición final |
|---|---|---|
| P-001 a P-009 (V1–V4) | PASS, 8/8, en las nueve iteraciones | **0** |

Ninguna iteración registró una violación arquitectónica en su medición final. El umbral de tolerancia cero definido en la Fase 5 ("cualquier tasa superior a 0 violaciones por KLOC... será catalogada como una ruptura arquitectónica crítica") nunca se cruzó.

### 2.2 Ratio de Deuda Técnica (SonarQube, Overall Code)

| Iteración | Versión | Rating | Debt Ratio | Issues nuevos |
|---|---|---|---|---|
| P-001 | V1 | A | — | 0 |
| P-002 | V1 | A | 0.0% | 0 |
| P-003 | V1 | A | 0.2% | 2 |
| P-004 | V1 | A | 0.1% | 0 |
| P-005 | V1 | A | 0.1% | 0 |
| P-006 | V1 | A | 0.1% | 1 (corregido) |
| P-007 | V2 | A | 0.1% | 0 |
| P-008 | V3 (estrés) | A | 0.1% | 0 |
| P-009 | V4 (estrés) | A | 0.1% | 1 |

**Comportamiento observado:** el Ratio de Deuda Técnica se mantuvo en una banda estrecha de 0.0%–0.2% durante las nueve iteraciones, sin tendencia ascendente. El valor más alto (0.2%, P-003) ocurrió en **V1**, no en los escenarios de estrés (V3/V4), que la hipótesis señala específicamente como los momentos de mayor riesgo. Ambos escenarios de estrés estructural (P-008: sustitución de persistencia; P-009: integración de PDF) cerraron en 0.1% — igual o menor que varias iteraciones de construcción incremental en V1.

El umbral de "degradación estructural severa" (>10%, Fase 5) no se acercó en ningún punto; ni siquiera el umbral de "degradación aceptable" (6-10%, Calificación B) se alcanzó nunca.

---

## 3. Conclusión: la hipótesis no se sostiene con los datos observados

Los datos **refutan** la predicción específica de incremento progresivo. Ni el Ratio de Deuda Técnica ni la Tasa de Violaciones Arquitectónicas mostraron una tendencia creciente, y los dos escenarios diseñados explícitamente como pruebas de estrés (V3, V4) no produjeron peores resultados que las iteraciones de construcción incremental que los precedieron.

Esto no significa que el estudio carezca de valor — significa que el resultado es el opuesto al predicho, lo cual es en sí mismo un hallazgo que requiere explicación, no solo un reporte de "hipótesis no confirmada".

---

## 4. Explicaciones candidatas para el resultado observado

Ninguna de estas se prueba de forma aislada con el diseño experimental actual; se presentan como hipótesis explicativas post-hoc, a discutir en la sección de conclusiones y como líneas de trabajo futuro.

### 4.1 El mecanismo de autoverificación interrumpe la cadena causal que la hipótesis asume

La hipótesis asume que la rapidez funcional, priorizada sobre la intervención estructural humana, se traduce directamente en violaciones medibles. Pero en la práctica observada, Claude Code CLI ejecutó `ArchitectureTest` **antes** de reportar cada sesión como terminada, y corrigió autónomamente 4 violaciones reales (P-002, P-003, P-004×2) sin intervención humana, antes de que llegaran a la medición final. La "intervención estructural humana" que la hipótesis contempla como el factor protector ausente fue, en la práctica, sustituida por una intervención estructural *automatizada* — el propio agente actuando como su propio verificador. Esto sugiere que la hipótesis, tal como está formulada, no distingue entre "sin verificación estructural" y "con verificación estructural automatizada pero no humana" — una distinción que, a la luz de los datos, resulta determinante.

### 4.2 Especificación completa por adelantado

Las nueve iteraciones se ejecutaron con `DESIGN_SGVA.md` y `CLAUDE.md` completos y disponibles desde el inicio — incluyendo, en el caso de V3 y V4, notas anticipatorias explícitas sobre lo que se venía. Esto es distinto de un escenario de código heredado o de requisitos que emergen sin documentación previa. Es plausible que la hipótesis sea más aplicable a escenarios donde el diseño arquitectónico no está completamente fijado de antemano — condición que este estudio no varió.

### 4.3 Escala del proyecto

SGVA alcanzó, al cierre de V4, un tamaño moderado (145 pruebas, cuatro capas, cinco entidades de dominio). La hipótesis de degradación progresiva podría requerir una escala mayor, o un número mayor de iteraciones, para manifestarse — el estudio no puede descartar que la tendencia exista pero no se haya vuelto medible todavía dentro de nueve prompts.

### 4.4 El instrumento de medición tiene un piso de sensibilidad

`ArchitectureTest` mide binariamente (cumple/no cumple) contra reglas estructurales explícitas; no captura degradación conceptual sutil (por ejemplo, un caso de uso técnicamente conforme pero mal diseñado). Es posible que exista degradación de un tipo que este instrumento específico no está diseñado para detectar — límite reconocido explícitamente en la sección de Amenazas a la Validez.

---

## 5. Relación con los hallazgos secundarios documentados

Aunque la hipótesis central no se sostiene, el estudio sí produjo evidencia relevante para los Resultados Esperados del anteproyecto (§9):

- **Taxonomía de fallos arquitectónicos (parcial):** las 4 restauraciones autodetectadas comparten una única causa raíz (reglas de sufijo capturando construcciones sintéticas de Kotlin) — un patrón de fallo real, aunque de naturaleza distinta a la que la hipótesis predecía (no es degradación por prisa, es una interacción entre convención de nomenclatura y características del lenguaje).
- **Evaluación del comportamiento ante ambigüedad:** en tres casos (referencia Saber Pro, factores TCE, sustitución de librería CSV) la IA manejó vacíos de diseño de forma transparente, documentando supuestos en vez de fallar silenciosamente o inventar datos.
- **Regularidad de deuda técnica por dependencia:** hallazgo no anticipado en la hipótesis original — la deuda técnica nueva se predice mejor por si una dependencia está gestionada por el BOM de Spring Boot que por la naturaleza "de estrés" o "incremental" de la iteración.

---

## 6. Recomendación para la redacción de la tesis

Presentar este resultado explícitamente como una **refutación de la hipótesis específica planteada**, no como un fallo del estudio. El valor del hallazgo está precisamente en que contradice la expectativa inicial de forma medible y explicable, y en que el proceso de verificación (control negativo del instrumento, verificación independiente de resultados, confirmación de inmutabilidad del instrumento — ver `logs/verificacion-*.md`) sostiene que el resultado no es artefacto de un instrumento defectuoso o de confianza ciega en los reportes de la IA.
