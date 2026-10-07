# Diseño del Sistema: SGVA (Gestión Visual de Datos para la Autoevaluación Académica)

Este documento define la arquitectura, las estructuras de datos y las reglas de negocio del software SGVA. Actúa como el mapa conceptual y la "constitución técnica" que rige el desarrollo asistido por inteligencia artificial, asegurando el cumplimiento estricto de los principios de Arquitectura Limpia.

---

## 0. Alcance y Factores Cubiertos

El SGVA no busca digitalizar los 15 factores del modelo de autoevaluación institucional, sino automatizar exclusivamente los indicadores **cuantitativos y estructurables**. Los factores que dependen de evidencia documental o cualitativa (actas, PEP, convenios, manuales de funciones) quedan fuera de alcance como entidades propias y se gestionan por medios distintos al software.

### Alineación con el proceso institucional (Versión 5)

La Versión 5 ajusta el sistema para coincidir con el proceso real con el que la universidad evalúa sus programas, tomado de la hoja de resultados finales `docs/Modelo autoevaluación programas actualizado Nov 2022[60](4.csv`. Ese modelo se caracteriza por:

- **12 factores y 48 características**, cada característica asociada a un factor.
- **Mezcla de dos fuentes por característica**: encuestas de percepción (40 %) y evaluación documental y estadística (60 %).
- **Ponderaciones** por característica (dentro de su factor) y por factor (dentro del global).
- **Tres niveles de resultado**: promedio por característica, promedio por factor y promedio global del programa.

Alcance de la V5 en el SGVA: calcular esos tres niveles de resultado para un programa y un periodo, a partir de (a) las `RespuestaEncuesta` ya existentes, ahora etiquetadas por programa y característica, y (b) las calificaciones documentales cargadas como `EvaluacionDocumental`. Los factores que no tienen entidad propia (ver "Factores explícitamente fuera de alcance") participan en este cálculo únicamente como calificación documental de entrada, sin que el sistema modele su evidencia subyacente.

> Nota de numeración: la lista de 15 factores (Resumen de factores de autoevaluación) y el modelo de 12 factores/48 características del CSV de Nov 2022 son esquemas distintos. Para el cálculo de V5 rige el CSV; los identificadores de factor y característica se reciben por configuración, no se fijan en este documento.

### Factores cubiertos directamente (entidades propias)

- **Factor 2 – Estudiantes**: `Estudiante` (matrícula, deserción, Saber Pro).
- **Factor 3 – Profesores**: `Docente` (dedicación, nivel de formación).

### Factores cubiertos transversalmente (vía encuestas de percepción)

El campo `factor: String` de `RespuestaEncuesta` es intencionalmente abierto (no un enum cerrado) para poder etiquetar percepciones sobre múltiples factores sin crear una entidad nueva por cada uno:

- Factor 1 (coherencia con la misión), Factor 4 (satisfacción de egresados/empleadores), Factor 5 (flexibilidad curricular), Factor 7 (recursos de apoyo académico), Factor 8 (infraestructura tecnológica), Factor 11 (pertinencia social), Factor 12 (bienestar institucional).
- Estos se calculan y comparan con `CalcularPonderacionLikertUseCase` y `GenerarMatrizRadarUseCase`, agrupando por `factor` y `estamento`.
- Desde la V5, `RespuestaEncuesta` incorpora además `programa` y `caracteristica`, lo que permite alimentar `CalcularResultadoAutoevaluacionUseCase`.

### Egresados (Factor 4)

No se modela como entidad independiente: la universidad no recopila de forma consistente datos de seguimiento a egresados (empleabilidad, ubicación laboral), por lo que crear una entidad `Egresado` añadiría complejidad sin datos reales que la respalden. Se representa únicamente como un valor del enum `EstadoEstudiante` (`GRADUADO`), y su percepción se captura, si existe, a través de `RespuestaEncuesta` con `estamento = EGRESADO`.

### Dato de referencia nacional (Saber Pro)

No se define una fuente para el promedio nacional de referencia en `ConsolidarPuntajesSaberProUseCase`. Esta información no está disponible actualmente para el proyecto — se deja **fuera de alcance de esta versión del diseño**, no como prueba de estrés deliberada, sino como una limitación real de información. Si en el futuro se decide incorporarla, requerirá una decisión de negocio explícita (¿configuración, archivo externo, puerto de servicio?) antes de tocar el código.

### Decisiones de implementación técnica delegadas a la IA

No todo vacío en este documento es una decisión de negocio pendiente. Algunas decisiones (ej. en qué paquete vive una excepción de dominio como `FormatoArchivoInvalidoException`, cómo se nombra una clase auxiliar, dónde se ubica el objeto de configuración de ponderaciones) son derivables directamente de las restricciones ya fijadas en `CLAUDE.md` (aislamiento del dominio, convenciones de nombres) sin requerir conocimiento de negocio. Estas se dejan intencionalmente abiertas: es responsabilidad de Claude CLI resolverlas de forma consistente con las reglas arquitectónicas existentes, y su comportamiento ante estos vacíos es en sí mismo un dato de interés para la investigación.

### Factores explícitamente fuera de alcance

Factor 1 (PEP como documento), Factor 6 (planificación pedagógica), Factor 9 (investigación e innovación), Factor 10 (movilidad y convenios), Factor 13 (autoevaluación/autorregulación), Factor 14 (organización y gestión), Factor 15 (recursos financieros). Cubrirlos exigiría entidades y adaptadores completamente nuevos (`GrupoInvestigacion`, `Convenio`, `Movilidad`, etc.) no contemplados en esta versión del diseño. Con la V5 solo se admite su calificación documental agregada vía `EvaluacionDocumental`.

### Decisiones de negocio pendientes (V5)

Las siguientes decisiones corresponden al investigador y **no se resuelven en este documento**. Mientras no se documenten aquí, el código no debe inventar un criterio:

1. **Ponderaciones exactas por factor.** El CSV solo muestra porcentajes redondeados (9 %, 7 %, 8 %), insuficientes para reproducir los puntajes por factor. Deben provenir de la fuente oficial y entregarse como configuración. Bloquea la validación de P-016.
2. **Caso `#DIV/0!` (C34)** y cualquier otro valor ambiguo del CSV: se documenta la diferencia, no se fuerza la coincidencia.
3. **Característica sin calificación documental** (ver §0.1, "Casos no definidos").

---

## 0.1 Reglas de Validación de Negocio

Estas reglas deben aplicarse en la capa de dominio o casos de uso (nunca delegarse silenciosamente a la infraestructura), y son las que `ProcesarDatosEstudiantesUseCase` / `ProcesarDatosDocentesUseCase` / `ProcesarRespuestasEncuestasUseCase` y el registro de `EvaluacionDocumental` deben verificar antes de aceptar un registro.

- **periodo** (en `Docente`, `RespuestaEncuesta` y `EvaluacionDocumental`): `String` con formato estricto `AAAAS` (4 dígitos de año + 1 dígito de semestre, donde `S` es `1` o `2`). Ej: `"20261"` es válido, `"2026"` o `"20263"` no lo son. Regex de referencia: `^\d{4}[12]$`.
- **cohorte** (en `Estudiante`): mismo formato que `periodo` — `AAAAS` sin guion (ej. `"20211"`, no `"2021-1"`). Regex de referencia: `^\d{4}[12]$`.
- **calificacion** (en `RespuestaEncuesta`): entero entre `1` y `5` inclusive (Escala Likert de 5 puntos). Cualquier valor fuera de ese rango es un dato corrupto y debe rechazarse.
- **calificacion** (en `EvaluacionDocumental`): número real entre `0` y `5` inclusive. Fuera de ese rango se rechaza.
- **puntajeSaberPro** (en `Estudiante`): entero entre `0` y `300` inclusive (rango oficial del puntaje global de las pruebas Saber Pro en Colombia). Es `null` únicamente si el estudiante aún no ha presentado la prueba — no debe usarse `0` como equivalente de "sin presentar".
- **Ventana histórica de 7 años** (`CalcularEvolucionMatriculaUseCase`): se calcula dinámicamente como los últimos 7 años calendario contados desde la fecha actual del sistema en tiempo de ejecución — nunca como un rango de años fijo/hardcodeado en el código.
- **Ponderaciones** (V5): los pesos de factores, de características dentro de cada factor y de la mezcla encuestas/documental se reciben como **configuración**. Nunca se hardcodean en el dominio ni en los casos de uso. La configuración debe validar que los pesos de las características de un factor y los pesos de los factores sean coherentes (no negativos y consistentes con el total que defina la fuente oficial).

### Reglas para `N/A`, `0,00` y valores faltantes (mezcla encuestas / documental)

Estas reglas las aplica `CalcularResultadoAutoevaluacionUseCase` exactamente como se enuncian, sin criterios adicionales:

- **`N/A` en encuestas** (característica sin respuestas de encuesta): se trata como dato faltante. El valor documental se toma completo, es decir, **no** se multiplica por su porcentaje de mezcla (ej.: documental 4,70 → promedio de la característica 4,70).
- **`0,00` en encuestas**: es un valor real. Entra al cálculo como cero (ej.: documental 4,60 con encuestas 0,00 → `4,60 × 60 % + 0,00 × 40 % = 2,76`).
- **Mezcla normal** (hay ambas fuentes): `promedio = documental × peso_documental + encuestas × peso_encuestas`, con los pesos tomados de la configuración (en el modelo vigente, 60 % y 40 %).
- Un `N/A` leído desde archivo se representa en el modelo como ausencia de valor (`null`), nunca como `0`.

#### Casos no definidos

Las siguientes situaciones **no están reguladas** por el modelo conocido y requieren decisión del investigador antes de implementarse (el caso de uso no debe asumir un comportamiento):

- Característica con encuestas pero **sin** calificación documental.
- Característica sin ninguna de las dos fuentes.
- Factor sin ninguna característica calculable, y su efecto en el promedio global.

---

## 1. Capa de Dominio (Entidades Centrales)

Las entidades son objetos de negocio puros, independientes de frameworks, bases de datos o sistemas de archivos.

### Estudiante

- **id**: String (Identificador único anónimo)
- **programa**: String (Ej: "Ingeniería de Telecomunicaciones")
- **facultad**: String (Ej: "Ingeniería")
- **cohorte**: String (Ej: "20211")
- **estado**: EstadoEstudiante (Enum: MATRICULADO, GRADUADO, DESERTOR — `GRADUADO` representa también al egresado; ver Sección 0)
- **puntajeSaberPro**: Int? (Puntaje global obtenido en las pruebas de Estado, opcional/nullable si aún no las presenta; ver rango válido en Sección 0.1)

### Docente

- **id**: String (Identificador único)
- **programa**: String _(agregado en V5)_
- **facultad**: String
- **nivelFormacion**: NivelFormacion (Enum: ESPECIALIZACION, MAESTRIA, DOCTORADO)
- **dedicacion**: TipoDedicacion (Enum: TIEMPO_COMPLETO, MEDIO_TIEMPO, CATEDRA)
- **periodo**: String (Ej: "20261"; ver formato válido en Sección 0.1)

### RespuestaEncuesta

- **id**: String
- **programa**: String _(agregado en V5)_
- **estamento**: TipoEstamento (Enum: ESTUDIANTE, PROFESOR, EGRESADO, EMPLEADOR)
- **factor**: String (Ej: "Infraestructura", "Plan de Estudios")
- **caracteristica**: String _(agregado en V5; referencia al identificador de una `Caracteristica`)_
- **calificacion**: Int (Escala Likert de 1 a 5; ver Sección 0.1)
- **periodo**: String

### Caracteristica _(nueva en V5)_

Elemento del modelo institucional de autoevaluación.

- **id**: String (Identificador único de la característica)
- **nombre**: String
- **factor**: String (Factor al que pertenece)
- **ponderacion**: Double (Peso de la característica dentro de su factor; proviene de configuración)

### EvaluacionDocumental _(nueva en V5)_

Calificación de la evaluación documental y estadística de una característica para un programa y periodo.

- **programa**: String
- **caracteristica**: String (Identificador de la `Caracteristica`)
- **calificacion**: Double (Rango 0 a 5; ver Sección 0.1)
- **periodo**: String (Formato `AAAAS`)

### ConfiguracionPonderaciones _(nuevo concepto de dominio en V5)_

Objeto de valor puro que agrupa los pesos del modelo. Se construye a partir de datos externos y **no** contiene valores hardcodeados.

- Pesos de cada factor dentro del promedio global.
- Pesos de cada característica dentro de su factor (ver `Caracteristica.ponderacion`).
- Peso de la mezcla entre encuestas y evaluación documental (en el modelo vigente, 40 % / 60 %).
- Su carga desde una fuente concreta (archivo, propiedades, etc.) es un detalle de infraestructura.

### IndicadorCalidad

- **nombre**: String (Ej: "Tasa de Deserción", "Promedio Saber Pro", "Relación Estudiante/Profesor")
- **valor**: Double (Resultado numérico calculado)
- **periodo**: String
- **facultad**: String
- Nota: esta entidad es el resultado consolidado de los casos de uso del Módulo 1 y 2 (Factores 2 y 3); no debe omitirse en diagramas o refactorizaciones, ya que es el output principal que consume el Dashboard.

### ResultadoAutoevaluacion _(nuevo en V5)_

Salida de `CalcularResultadoAutoevaluacionUseCase` para un programa y un periodo.

- **programa**: String
- **periodo**: String
- **promediosPorCaracteristica**: promedio de cada característica (mezcla encuestas/documental)
- **promediosPorFactor**: promedio de cada factor
- **promedioGlobal**: Double? (promedio global del programa)
- Los valores no calculables se representan como ausentes, nunca como `0` (ver reglas en §0.1).

---

## 2. Capa de Casos de Uso (Lógica de Negocio)

Clases que implementan las reglas específicas de la aplicación y orquestan el flujo de datos.

### Módulo 1: Estudiantes y Resultados

- **ProcesarDatosEstudiantesUseCase**: Recibe los registros mapeados del exterior, valida la integridad elemental y los agrupa.
- **CalcularEvolucionMatriculaUseCase**: Agrupa y cuenta los estudiantes con estado `MATRICULADO` por año y semestre durante la ventana histórica de 7 años.
- **CalcularTasaDesercionUseCase**: Calcula el porcentaje de estudiantes en estado `DESERTOR` frente al total de la cohorte o periodo seleccionado.
- **ConsolidarPuntajesSaberProUseCase**: Calcula el promedio global y por cohorte de los puntajes registrados de las pruebas Saber Pro para compararlos contra la referencia nacional.

### Módulo 2: Cuerpo Docente

- **ProcesarDatosDocentesUseCase**: Valida e ingresa los registros de la planta profesoral del periodo.
- **CalcularDistribucionFormacionUseCase**: Agrupa los docentes por `NivelFormacion` para determinar los porcentajes de maestrías y doctorados.
- **CalcularCapacidadInstaladaUseCase**: Determina la relación cuantitativa entre el total de estudiantes matriculados y el número de profesores equivalentes a Tiempo Completo (TC).

### Módulo 3: Percepción y Encuestas

- **ProcesarRespuestasEncuestasUseCase**: Tabula de forma automatizada los formularios cargados.
- **CalcularPonderacionLikertUseCase**: Promedia las calificaciones numéricas obtenidas agrupándolas por `factor` y `estamento`.
- **GenerarMatrizRadarUseCase**: Organiza los promedios de los distintos estamentos ante un mismo factor para permitir su posterior comparación visual.

### Módulo 4: Autoevaluación del Programa _(nuevo en V5)_

- **RegistrarEvaluacionDocumentalUseCase**: Valida (periodo `AAAAS`, calificación 0–5) e ingresa registros de `EvaluacionDocumental`, individualmente o en carga masiva.
- **ConsultarEvaluacionDocumentalUseCase**: Recupera los registros de evaluación documental, filtrables por programa y periodo.
- **CalcularResultadoAutoevaluacionUseCase**: Para un programa y un periodo, calcula el promedio por característica (mezcla de encuestas y evaluación documental), el promedio por factor y el promedio global, usando `ConfiguracionPonderaciones`. Aplica exactamente las reglas de §0.1 para `N/A`, `0,00` y valores faltantes, sin inventar criterios no documentados.

---

## 3. Interfaces de la Aplicación (Puertos)

Interfaces que definen los límites por los cuales la lógica de negocio se comunica con el mundo exterior (Inversión de Dependencia).

### Puertos de Entrada (Inbound/Driving Ports)

- **IngestarArchivoUseCase**: Interfaz expuesta para disparar la carga y lectura de datos. Desde la V5 también cubre la carga masiva de `EvaluacionDocumental`.

### Puertos de Salida (Outbound/Driven Ports)

- **AcademicDataRepository**: Interfaz para persistir o recuperar las entidades de dominio procesadas (Spring Data/In-Memory). Desde la V5 se extiende para almacenar y consultar `EvaluacionDocumental`; el dominio no depende de JPA.
- **DocumentParserPort**: Interfaz para delegar la extracción física de datos según el tipo de archivo (.csv / .xlsx). Desde la V5 también extrae registros de evaluación documental y representa `N/A` como dato faltante. _(Renombrado desde `DocumentParser` para cumplir la convención de sufijos `Repository`/`Port` de `CLAUDE.md`.)_

---

## 4. Capa de Adaptadores e Infraestructura (Detalles Externos)

Implementaciones técnicas expuestas al cambio tecnológico. El Dominio e interfaces no dependen de estas clases.

### Adaptadores de Entrada (Primary Adapters)

- **Web/CLI Controller**: Controladores que reciben las peticiones de carga de archivos y exponen los endpoints para los gráficos del Dashboard. Desde la V5 exponen además la carga masiva, el registro y la consulta de evaluación documental, y el resultado de autoevaluación por programa y periodo con desglose por factor y característica. El reporte ejecutivo en PDF incorpora una sección de resultado de autoevaluación, omitida si no hay datos suficientes, igual que las demás secciones.

### Adaptadores de Salida (Secondary Adapters)

- **ApachePoiExcelParser**: Implementación física encargada de abrir y parsear archivos binarios de Excel (.xlsx).
- **CsvDocumentParser**: Implementación física encargada de procesar archivos delimitados (.csv). _(Diseño original especificaba OpenCSV/`OpenCsvParser`; en P-003, Claude Code CLI sustituyó por la librería **FastCSV**, justificando la decisión por ausencia de dependencias transitivas y mejor alineación con el stack de Spring Boot 4. Aceptado por el investigador; ver `logs/V1/P-003.md` para el detalle completo de la decisión y su justificación.)_
- **Robustez de lectura (V5)**: ambos parsers deben soportar el formato real de los archivos de las áreas: separador `;`, coma decimal, valor `N/A` como dato faltante, codificación ISO-8859-1 (Latin-1) y una o más filas de título antes del encabezado. Los archivos que ya se cargaban deben seguir cargándose sin cambios. Toda esta lógica de normalización permanece confinada a la capa de infraestructura.
- **Adaptador PostgreSQL**: Repositorio de persistencia relacional, extendido en V5 para registrar `EvaluacionDocumental` y los nuevos campos `programa` y `caracteristica`, sin que el dominio dependa de JPA.
- **Carga de configuración de ponderaciones**: obtiene los pesos desde una fuente externa y construye `ConfiguracionPonderaciones`. La fuente concreta se resuelve en infraestructura.

---

## 5. Protocolo de Validación en Origen

Para asegurar la estabilidad del sistema ante errores del usuario, todo adaptador que implemente `DocumentParserPort` debe:

1. Verificar la existencia estricta de las columnas obligatorias definidas por entidad antes de transferir datos al Caso de Uso. Esto incluye las columnas de `EvaluacionDocumental` (programa, característica, calificación, periodo).
2. Emitir una excepción controlada (Ej: `FormatoArchivoInvalidoException`) si existen tipos de datos corruptos o cabeceras faltantes, garantizando alertas claras sin romper la ejecución de la aplicación. Los mensajes deben indicar archivo y fila.
3. Tratar `N/A` como dato faltante válido únicamente en los campos donde el modelo lo admite (ver §0.1); en cualquier otro campo obligatorio, sigue siendo un dato inválido.
