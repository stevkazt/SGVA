# Amenazas a la Validez

Se organiza siguiendo el marco estándar para estudios empíricos en ingeniería de software (validez de constructo, interna, externa y de conclusión), nombrando explícitamente las limitaciones en vez de omitirlas.

---

## 1. Validez de Constructo — ¿mide el estudio lo que dice medir?

- **`ArchitectureTest` mide cumplimiento estructural binario, no calidad de diseño.** Verifica dependencias entre capas y convenciones de nomenclatura fijadas de antemano; no puede detectar un caso de uso técnicamente conforme pero conceptualmente mal diseñado, ni acoplamiento sutil que no viole una regla explícita.
- **El Ratio de Deuda Técnica de SonarQube depende del Quality Profile activo**, fijado en la calibración de V0 y no modificado después — pero un profile distinto podría producir valores diferentes para el mismo código.
- **Verificado empíricamente que el instrumento puede fallar:** se introdujo una violación arquitectónica real (anotación `@Component` en una entidad de dominio) y `ArchitectureTest` la detectó correctamente, citando la regla exacta incumplida (ver `logs/verificacion-instrumento-negativo.md`). Esto reduce, aunque no elimina, la amenaza de que el instrumento esté configurado de forma que nunca pueda fallar.

## 2. Validez Interna — ¿el efecto observado se debe realmente a la causa propuesta?

- **El diseño arquitectónico completo (`DESIGN_SGVA.md`) estuvo disponible desde el inicio de cada iteración**, incluyendo notas anticipatorias explícitas sobre los cambios de V3 y V4. Esto es una condición favorable no controlada: el estudio no compara contra un escenario donde la IA reciba requisitos sin especificación arquitectónica previa.
- **Las reglas de `ArchitectureTest.kt` se fijaron y bloquearon antes de P-001**, confirmado objetivamente vía `git log` (tres commits, todos anteriores a P-001 por al menos cinco días — ver `logs/verificacion-instrumento-inmutable.md`), evitando circularidad entre el instrumento de medición y los resultados que produce.
- **Continuidad de sesión entre fases:** P-007 (V2) y P-008 (V3) compartieron el mismo Session ID de Claude Code CLI — el contexto conversacional de V2 estaba presente al ejecutar V3. Esto pudo influir en el error de clasificación de fase observado en P-008, y no puede descartarse como factor en otros resultados de esa sesión.
- **El investigador dirigió el fraseo de los prompts** dentro del protocolo funcional definido en la Fase 3 (sin nombrar clases/paquetes explícitamente), pero la elección de qué ambigüedades dejar abiertas y cuáles resolver de antemano fue una decisión humana que pudo influir en qué comportamientos de la IA quedaron expuestos a medición.
- **Se identificó y corrigió una inconsistencia de orden de commits** (`4082d01`) mediante verificación independiente — documentada en `logs/verificacion-independiente.md` — que no afecta la validez de los resultados finales pero sí ilustra que el proceso de registro no estuvo libre de error humano.

## 3. Validez Externa — ¿generalizan los resultados?

- **Un solo proyecto (SGVA), un solo dominio (gestión académica), una sola herramienta de IA (Claude Code CLI) y un único investigador.** Los resultados no pueden generalizarse a otros dominios, escalas de proyecto, o herramientas de codificación asistida por IA sin estudios adicionales.
- **Muestra pequeña:** nueve iteraciones documentadas. Suficiente para observar patrones cualitativos (autocorrecciones, desviaciones de diseño, manejo de ambigüedad) pero insuficiente para inferencia estadística sobre tendencias de deuda técnica.
- **El proyecto alcanzó una escala moderada** (145 pruebas, cuatro capas, cinco entidades) al cierre de V4. No se puede descartar que una tendencia de degradación exista pero requiera una escala o un número de iteraciones mayor para manifestarse.

## 4. Validez de Conclusión — ¿son fiables las relaciones observadas entre los datos?

- **Ausencia de análisis estadístico formal:** con nueve puntos de datos y una variable (Debt Ratio) que se mantuvo casi constante (0.0%–0.2%), no se aplican pruebas de significancia; las conclusiones se basan en observación directa e interpretación cualitativa de los logs, no en inferencia estadística.
- **Verificación independiente de resultados reportados por la IA:** para mitigar el riesgo de aceptar acríticamente los resúmenes de Claude Code CLI, se reprodujeron de forma independiente los resultados de compilación y pruebas en los commits de cierre de cada versión (`logs/verificacion-independiente.md`), lo que además permitió detectar una inconsistencia real no relacionada con el comportamiento de la IA.
- **El rol de un asistente de IA conversacional (Claude, vía interfaz de chat) en el diseño de prompts, interpretación de resultados de SonarQube/ArchUnit, y estructuración del protocolo de logging** es una fuente adicional de influencia humana-asistida sobre el proceso experimental, distinta del objeto de estudio (Claude Code CLI generando código). Se declara explícitamente para evitar la impresión de que el diseño experimental fue enteramente autónomo del investigador.

## 5. Limitación reconocida en los datos de negocio

Los factores de conversión a Tiempo Completo Equivalente (`EquivalenciaTiempoCompleto`, introducidos en P-005) son provisionales y no han sido confirmados contra la normativa institucional real. Cualquier valor derivado de `CalcularCapacidadInstaladaUseCase` en este estudio debe interpretarse como ilustrativo de la arquitectura del sistema, no como un dato institucional válido.
