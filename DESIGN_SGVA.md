# Diseño del Sistema: SGVA (Gestión Visual de Datos para la Autoevaluación Académica)

Este documento define la arquitectura, las estructuras de datos y las reglas de negocio del software SGVA. Actúa como el mapa conceptual y la "constitución técnica" que rige el desarrollo asistido por inteligencia artificial, asegurando el cumplimiento estricto de los principios de Arquitectura Limpia.

---

## 0. Alcance y Factores Cubiertos

El SGVA automatiza los indicadores **cuantitativos y estructurables** de la autoevaluación. La evidencia documental y cualitativa (actas, PEP, convenios, manuales de funciones) no se digitaliza como entidades propias y se gestiona por medios distintos al software.

> Nota de numeración: en las versiones V1 a V4, las referencias a "Factor N" en esta sección siguen la lista de 15 factores (Resumen de factores de autoevaluación). Desde la V5, el cálculo del resultado de autoevaluación se rige por el **modelo institucional de 12 factores y 48 características** (ver siguiente subsección). Los identificadores de factor y característica del modelo de 12 factores se reciben por configuración y no se fijan en este documento.

### Alineación con el proceso institucional (Versión 5)

La Versión 5 ajusta el sistema para coincidir con el proceso real con el que la universidad evalúa sus programas. La fuente es el **modelo de autoevaluación diligenciado 2025** de la Especialización en Gestión de Servicios de Tecnologías de la Información. El modelo de 2022 (con ponderaciones redondeadas y errores #DIV/0!) está **superado** y no se usa como fuente ni como valor esperado.

El modelo se caracteriza por:

- **12 factores y 48 características**, cada característica asociada a un factor.
- **Mezcla de dos fuentes por característica**: encuestas de percepción (40 %) y evaluación documental y estadística (60 %).
- **Ponderaciones exactas** por factor (dentro del global) y por característica (dentro de su factor), tomadas de la hoja «1. Ponderación» del modelo.
- **Tres niveles de resultado**: promedio por característica, por factor y global del programa.
- **7 actores en la encuesta**: estudiantes, profesores, directivos, administrativos, egresados, empleadores y aliados estratégicos.
- Los datos de encuesta disponibles son **promedios por pregunta** (de 1 a 5), no respuestas individuales.

Alcance de la V5: calcular los tres niveles de resultado para un programa y un periodo, a partir de (a) los `ResultadoEncuestaPregunta` y (b) las `EvaluacionDocumental` cargadas, usando `ConfiguracionPonderaciones`. Esto incluye lo necesario para operar de punta a punta (carga masiva, REST, reporte PDF).

Los factores sin entidad propia participan en el cálculo únicamente a través de su calificación documental y de los resultados de encuesta de sus características. El sistema no modela su evidencia subyacente.

### Factores cubiertos directamente (entidades propias)

- **Factor 2 – Estudiantes**: `Estudiante` (matrícula, deserción, Saber Pro).
- **Factor 3 – Profesores**: `Docente` (dedicación, nivel de formación).

### Factores cubiertos transversalmente (vía encuestas de percepción)

El campo `factor: String` de `RespuestaEncuesta` es intencionalmente abierto (no un enum cerrado) para poder etiquetar percepciones sobre múltiples factores sin crear una entidad nueva por cada uno:

- Factor 1 (coherencia con la misión), Factor 4 (satisfacción de egresados/empleadores), Factor 5 (flexibilidad curricular), Factor 7 (recursos de apoyo académico), Factor 8 (infraestructura tecnológica), Factor 11 (pertinencia social), Factor 12 (bienestar institucional).
- Estos se calculan y comparan con `CalcularPonderacionLikertUseCase` y `GenerarMatrizRadarUseCase`, agrupando por `factor` y `estamento`.
- `RespuestaEncuesta` **no participa** en el cálculo del resultado de autoevaluación de V5, que usa `ResultadoEncuestaPregunta`.

### Egresados (Factor 4)

No se modela como entidad independiente: la universidad no recopila de forma consistente datos de seguimiento a egresados (empleabilidad, ubicación laboral), por lo que crear una entidad `Egresado` añadiría complejidad sin datos reales que la respalden. Se representa únicamente como un valor del enum `EstadoEstudiante` (`GRADUADO`), y su percepción se captura, si existe, a través de los estamentos `EGRESADO` en las encuestas.

### Dato de referencia nacional (Saber Pro)

No se define una fuente para el promedio nacional de referencia en `ConsolidarPuntajesSaberProUseCase`. Esta información no está disponible actualmente para el proyecto: se deja **fuera de alcance**, no como prueba de estrés deliberada, sino como una limitación real de información. Si en el futuro se decide incorporarla, requerirá una decisión de negocio explícita antes de tocar el código.

### Decisiones de implementación técnica delegadas a la IA

No todo vacío en este documento es una decisión de negocio pendiente. Algunas decisiones (ej. en qué paquete vive una excepción de dominio como `FormatoArchivoInvalidoException`, cómo se nombra una clase auxiliar, dónde se ubica el objeto de configuración de ponderaciones, cómo se nombran los valores de la valoración cualitativa) son derivables directamente de las restricciones ya fijadas en `CLAUDE.md` (aislamiento del dominio, convenciones de nombres) sin requerir conocimiento de negocio. Estas se dejan intencionalmente abiertas: es responsabilidad de Claude CLI resolverlas de forma consistente con las reglas arquitectónicas existentes, y su comportamiento ante estos vacíos es en sí mismo un dato de interés para la investigación.

### Qué queda fuera de alcance

- La evidencia documental (actas, PEP, convenios, manuales, matriz de evidencias): no se digitaliza. Solo se admite su **calificación consolidada** como `EvaluacionDocumental`.
- La generación del informe narrativo de autoevaluación.
- Entidades como `GrupoInvestigacion`, `Convenio` o `Movilidad`.

### Datos de prueba derivados de documentos institucionales

El repositorio es público. Los fixtures construidos a partir de documentos institucionales contienen **únicamente códigos y valores numéricos** (ponderaciones, promedios por pregunta, calificaciones). No incluyen justificaciones, sugerencias de mejora, nombres de personas ni datos personales.

### Verificación del investigador

Verificado contra la hoja «1. Ponderación» del modelo 2025: los pesos de los factores suman 1,0 y los pesos de las características de cada factor suman 1,0. La regla de validación correspondiente (ver §0.1) aplica.

---

## 0.1 Reglas de Validación de Negocio

Estas reglas deben aplicarse en la capa de dominio o casos de uso (nunca delegarse silenciosamente a la infraestructura). Son las que `ProcesarDatosEstudiantesUseCase`, `ProcesarDatosDocentesUseCase`, `ProcesarRespuestasEncuestasUseCase` y los casos de uso de resultados de encuesta y evaluación documental deben verificar antes de aceptar un registro.

- **programa** (en `Estudiante`, `Docente`, `ResultadoEncuestaPregunta`, `EvaluacionDocumental` y `ResultadoAutoevaluacion`): identificador corto y estable del programa (ej. `"EGSTI"`). Es obligatorio y no puede ser vacío. No se usa el nombre completo como identificador, para que los resultados sean comparables entre periodos.
- **periodo** (en `Docente`, `ResultadoEncuestaPregunta` y `EvaluacionDocumental`): `String` con formato estricto `AAAAS` (4 dígitos de año + 1 dígito de semestre, donde `S` es `1` o `2`). Ej: `"20252"` es válido, `"2025"` o `"20253"` no lo son. Regex de referencia: `^\d{4}[12]$`.
- **cohorte** (en `Estudiante`): mismo formato que `periodo` (ej. `"20211"`, no `"2021-1"`). Regex de referencia: `^\d{4}[12]$`.
- **calificacion** (en `RespuestaEncuesta`): entero entre `1` y `5` inclusive (escala Likert de 5 puntos).
- **promedio** (en `ResultadoEncuestaPregunta`): número real entre `1` y `5` inclusive. El valor `0` es un dato inválido, no un cero real. La ausencia de dato (`N/A` en archivo) se representa como `null` y es válida.
- **calificacion** (en `EvaluacionDocumental`): número real entre `1` y `5` inclusive. Fuera de ese rango se rechaza. `N/A` no es válido en este campo.
- **estamento**: debe ser uno de los siete actores definidos (ver `TipoEstamento`).
- **puntajeSaberPro** (en `Estudiante`): entero entre `0` y `300` inclusive (rango oficial del puntaje global de Saber Pro). Es `null` únicamente si el estudiante aún no ha presentado la prueba; no debe usarse `0` como equivalente de "sin presentar".
- **Ventana histórica de 7 años** (`CalcularEvolucionMatriculaUseCase`): se calcula dinámicamente como los últimos 7 años calendario contados desde la fecha actual del sistema en tiempo de ejecución, nunca como un rango fijo en el código.
- **Ponderaciones** (V5): los pesos de factores, de características dentro de cada factor y de la mezcla encuestas/documental se reciben como **configuración**. Nunca se hardcodean en el dominio ni en los casos de uso. La configuración debe validar que todos los pesos sean no negativos y que los pesos de los factores sumen `1,0` con una tolerancia de `0,001`. Los pesos de las características dentro de cada factor también deben sumar `1,0` con la misma tolerancia de `0,001`.
- **Valoración cualitativa** (en `EvaluacionDocumental`): se deriva de la calificación **redondeada a un decimal** (mitad hacia arriba), según la escala institucional:

  | Calificación redondeada | Valoración                     |
  | ----------------------- | ------------------------------ |
  | 4,6 a 5,0               | Se cumple plenamente           |
  | 4,0 a 4,5               | Se cumple en alto grado        |
  | 3,0 a 3,9               | Se cumple aceptablemente       |
  | 2,0 a 2,9               | Se cumple insatisfactoriamente |
  | 1,0 a 1,9               | No se cumple                   |

  El valor de `calificacion` se almacena **tal como llega**, sin redondear. El redondeo se usa únicamente para derivar la valoración.

### Reglas de cálculo del resultado de autoevaluación

`CalcularResultadoAutoevaluacionUseCase` aplica exactamente estas reglas, sin criterios adicionales:

1. **Resultado de encuesta de una característica** = promedio simple de los `promedio` de sus preguntas (promedio de los promedios), para el programa y periodo consultados. Las preguntas con `promedio = null` no cuentan. Si la característica no tiene ninguna pregunta con valor, el resultado de encuesta es **ausente** (equivale al `N/A` del modelo institucional).
2. **Promedio de una característica**:
   - Hay resultado de encuesta y evaluación documental: `documental × peso_documental + encuesta × peso_encuestas` (en el modelo vigente, 60 % y 40 %, tomados de la configuración).
   - Encuesta ausente y hay evaluación documental: el promedio es el valor documental **completo**, sin multiplicarlo por su peso de mezcla (ej.: documental 4,70 → promedio 4,70).
   - No hay evaluación documental (con o sin encuesta), o no hay ninguna de las dos fuentes: el promedio es **ausente**.
3. **Promedio de un factor** = promedio ponderado de los promedios de sus características que no son ausentes, usando los pesos de la configuración **reponderados** entre las características presentes. Si ninguna es calculable, el promedio del factor es **ausente**.
4. **Promedio global** = promedio ponderado de los promedios de los factores que no son ausentes, usando los pesos de la configuración **reponderados** entre los factores presentes. Si ningún factor es calculable, el global es **ausente**.
5. Cuando todas las características y factores tienen dato, la reponderación no cambia ningún valor.
6. Un valor ausente se representa como `null` y **nunca** como `0`. Un `N/A` leído desde archivo se representa como `null`.

---

## 1. Capa de Dominio (Entidades Centrales)

Las entidades son objetos de negocio puros, independientes de frameworks, bases de datos o sistemas de archivos.

### Estudiante

- **id**: String (identificador único anónimo)
- **programa**: String (identificador del programa; ver §0.1)
- **facultad**: String (ej. "Ingeniería")
- **cohorte**: String (ej. "20211")
- **estado**: EstadoEstudiante (Enum: MATRICULADO, GRADUADO, DESERTOR; `GRADUADO` representa también al egresado, ver §0)
- **puntajeSaberPro**: Int? (puntaje global de las pruebas de Estado, nullable si aún no las presenta; ver §0.1)

### Docente

- **id**: String (identificador único)
- **programa**: String _(agregado en V5, obligatorio; ver §0.1)_
- **facultad**: String
- **nivelFormacion**: NivelFormacion (Enum: ESPECIALIZACION, MAESTRIA, DOCTORADO)
- **dedicacion**: TipoDedicacion (Enum: TIEMPO_COMPLETO, MEDIO_TIEMPO, CATEDRA)
- **periodo**: String (ej. "20261"; ver §0.1)

### RespuestaEncuesta

Sin cambios de campos en V5.

- **id**: String
- **estamento**: TipoEstamento
- **factor**: String (ej. "Infraestructura", "Plan de Estudios")
- **calificacion**: Int (escala Likert de 1 a 5; ver §0.1)
- **periodo**: String

### TipoEstamento _(ampliado en V5)_

Enum de actores que responden las encuestas, ahora los siete del proceso institucional: ESTUDIANTE, PROFESOR, DIRECTIVO, ADMINISTRATIVO, EGRESADO, EMPLEADOR, ALIADO_ESTRATEGICO. Las funcionalidades existentes que usan los cuatro valores anteriores deben seguir funcionando.

### Caracteristica _(nueva en V5)_

Elemento del modelo institucional de autoevaluación.

- **id**: String (identificador único de la característica)
- **nombre**: String
- **factor**: String (identificador del factor al que pertenece)
- **ponderacion**: Double (peso de la característica dentro de su factor; proviene de configuración)

### ResultadoEncuestaPregunta _(nueva en V5)_

Promedio de una pregunta de la encuesta institucional, asociada a una característica.

- **programa**: String
- **periodo**: String (formato `AAAAS`)
- **codigoPregunta**: String (ej. "A2")
- **caracteristica**: String (identificador de la `Caracteristica`)
- **estamentos**: conjunto de `TipoEstamento` (actores que responden la pregunta)
- **promedio**: Double? (de 1 a 5; `null` si el dato falta; ver §0.1)

### EvaluacionDocumental _(nueva en V5)_

Evaluación documental y estadística consolidada de una característica para un programa y periodo.

- **programa**: String
- **caracteristica**: String (identificador de la `Caracteristica`)
- **periodo**: String (formato `AAAAS`)
- **calificacion**: Double (de 1 a 5; se almacena sin redondear)
- **valoracionCualitativa**: derivada de la calificación según la tabla de §0.1
- **justificacion**: String? (opcional)
- **sugerenciasMejora**: String? (opcional)

### ConfiguracionPonderaciones _(nuevo concepto de dominio en V5)_

Objeto de valor puro que agrupa los pesos del modelo. Se construye a partir de datos externos y **no** contiene valores hardcodeados.

- Peso de cada factor dentro del promedio global.
- Peso de cada característica dentro de su factor (ver `Caracteristica.ponderacion`).
- Peso de la mezcla entre encuestas y evaluación documental (en el modelo vigente, 40 % / 60 %).
- Su carga desde una fuente concreta es un detalle de infraestructura.

### IndicadorCalidad

- **nombre**: String (ej. "Tasa de Deserción", "Promedio Saber Pro", "Relación Estudiante/Profesor")
- **valor**: Double (resultado numérico calculado)
- **periodo**: String
- **facultad**: String
- Nota: esta entidad es el resultado consolidado de los casos de uso del Módulo 1 y 2; no debe omitirse en diagramas o refactorizaciones, ya que es el output principal que consume el Dashboard.

### ResultadoAutoevaluacion _(nuevo en V5)_

Salida de `CalcularResultadoAutoevaluacionUseCase` para un programa y un periodo.

- **programa**: String
- **periodo**: String
- **promediosPorCaracteristica**: promedio de cada característica (puede ser ausente)
- **promediosPorFactor**: promedio de cada factor (puede ser ausente)
- **promedioGlobal**: Double? (puede ser ausente)
- Los valores no calculables se representan como ausentes, nunca como `0` (ver §0.1).

---

## 2. Capa de Casos de Uso (Lógica de Negocio)

Clases que implementan las reglas específicas de la aplicación y orquestan el flujo de datos.

### Módulo 1: Estudiantes y Resultados

- **ProcesarDatosEstudiantesUseCase**: recibe los registros mapeados del exterior, valida la integridad elemental y los agrupa.
- **CalcularEvolucionMatriculaUseCase**: agrupa y cuenta los estudiantes con estado `MATRICULADO` por año y semestre durante la ventana histórica de 7 años.
- **CalcularTasaDesercionUseCase**: calcula el porcentaje de estudiantes en estado `DESERTOR` frente al total de la cohorte o periodo seleccionado.
- **ConsolidarPuntajesSaberProUseCase**: calcula el promedio global y por cohorte de los puntajes registrados de Saber Pro para compararlos contra la referencia nacional.

### Módulo 2: Cuerpo Docente

- **ProcesarDatosDocentesUseCase**: valida e ingresa los registros de la planta profesoral del periodo.
- **CalcularDistribucionFormacionUseCase**: agrupa los docentes por `NivelFormacion` para determinar los porcentajes de maestrías y doctorados.
- **CalcularCapacidadInstaladaUseCase**: determina la relación cuantitativa entre el total de estudiantes matriculados y el número de profesores equivalentes a Tiempo Completo (TC).

### Módulo 3: Percepción y Encuestas

- **ProcesarRespuestasEncuestasUseCase**: tabula de forma automatizada los formularios cargados.
- **CalcularPonderacionLikertUseCase**: promedia las calificaciones numéricas obtenidas agrupándolas por `factor` y `estamento`.
- **GenerarMatrizRadarUseCase**: organiza los promedios de los distintos estamentos ante un mismo factor para permitir su posterior comparación visual.

### Módulo 4: Autoevaluación del Programa _(nuevo en V5)_

- **RegistrarResultadoEncuestaPreguntaUseCase**: valida (periodo `AAAAS`, promedio de 1 a 5 o ausente, estamentos válidos) e ingresa registros de `ResultadoEncuestaPregunta`, individualmente o en carga masiva.
- **ConsultarResultadoEncuestaPreguntaUseCase**: recupera los resultados de encuesta por pregunta, filtrables por programa y periodo.
- **RegistrarEvaluacionDocumentalUseCase**: valida (periodo `AAAAS`, calificación de 1 a 5) e ingresa registros de `EvaluacionDocumental`, individualmente o en carga masiva.
- **ConsultarEvaluacionDocumentalUseCase**: recupera los registros de evaluación documental, filtrables por programa y periodo.
- **CalcularResultadoAutoevaluacionUseCase**: para un programa y un periodo, calcula el promedio por característica, por factor y global, usando `ConfiguracionPonderaciones` y aplicando exactamente las reglas de cálculo de §0.1.

---

## 3. Interfaces de la Aplicación (Puertos)

Interfaces que definen los límites por los cuales la lógica de negocio se comunica con el mundo exterior (Inversión de Dependencia).

### Puertos de Entrada (Inbound/Driving Ports)

- **IngestarArchivoUseCase**: interfaz expuesta para disparar la carga y lectura de datos. Desde la V5 también cubre la carga masiva de `ResultadoEncuestaPregunta` y de `EvaluacionDocumental`.

### Puertos de Salida (Outbound/Driven Ports)

- **AcademicDataRepository**: interfaz para persistir o recuperar las entidades de dominio procesadas. Desde la V5 se extiende para almacenar y consultar `ResultadoEncuestaPregunta` y `EvaluacionDocumental`; el dominio no depende de JPA.
- **DocumentParserPort**: interfaz para delegar la extracción física de datos según el tipo de archivo (.csv / .xlsx). Desde la V5 también extrae resultados de encuesta por pregunta y evaluación documental, y representa `N/A` como dato faltante. _(Renombrado desde `DocumentParser` para cumplir la convención de sufijos `Repository`/`Port` de `CLAUDE.md`.)_

---

## 4. Capa de Adaptadores e Infraestructura (Detalles Externos)

Implementaciones técnicas expuestas al cambio tecnológico. El Dominio y las interfaces no dependen de estas clases.

### Adaptadores de Entrada (Primary Adapters)

- **Web/CLI Controller**: controladores que reciben las peticiones de carga de archivos y exponen los endpoints para los gráficos del Dashboard. Desde la V5 exponen además la carga masiva, el registro y la consulta de resultados de encuesta por pregunta y de evaluación documental, y el resultado de autoevaluación por programa y periodo con desglose por factor y característica. El reporte ejecutivo en PDF incorpora una sección de resultado de autoevaluación, omitida si no hay datos suficientes, igual que las demás secciones.

### Adaptadores de Salida (Secondary Adapters)

- **ApachePoiExcelParser**: implementación física encargada de abrir y parsear archivos binarios de Excel (.xlsx).
- **CsvDocumentParser**: implementación física encargada de procesar archivos delimitados (.csv). _(Diseño original especificaba OpenCSV/`OpenCsvParser`; en P-003, Claude Code CLI sustituyó por la librería **FastCSV**, justificando la decisión por ausencia de dependencias transitivas y mejor alineación con el stack de Spring Boot 4. Aceptado por el investigador; ver `logs/V1/P-003.md`.)_
- **Robustez de lectura (V5)**: ambos parsers deben soportar el formato real de los archivos de las áreas: separador `;`, coma decimal, valor `N/A` como dato faltante, codificación ISO-8859-1 (Latin-1) y una o más filas de título antes del encabezado. Los archivos que ya se cargaban con las columnas vigentes deben seguir cargándose sin cambios. Toda esta lógica de normalización permanece confinada a la capa de infraestructura.
- **Adaptador PostgreSQL**: repositorio de persistencia relacional, extendido en V5 para registrar `ResultadoEncuestaPregunta` y `EvaluacionDocumental` y el nuevo campo `programa` de `Docente`, sin que el dominio dependa de JPA.
- **Carga de configuración de ponderaciones**: obtiene los pesos desde una fuente externa entregada por el investigador, construida a partir de la hoja «1. Ponderación» del modelo 2025, y construye `ConfiguracionPonderaciones`. El formato y la ubicación concretos se resuelven en infraestructura.

---

## 5. Protocolo de Validación en Origen

Para asegurar la estabilidad del sistema ante errores del usuario, todo adaptador que implemente `DocumentParserPort` debe:

1. Verificar la existencia estricta de las columnas obligatorias definidas por entidad antes de transferir datos al Caso de Uso. Desde la V5 esto incluye `programa` en los archivos de docentes, y las columnas de `ResultadoEncuestaPregunta` (programa, periodo, código de pregunta, característica, estamentos, promedio) y de `EvaluacionDocumental` (programa, característica, periodo, calificación; justificación y sugerencias son opcionales).
2. Emitir una excepción controlada (ej. `FormatoArchivoInvalidoException`) si existen tipos de datos corruptos o cabeceras faltantes, garantizando alertas claras sin romper la ejecución de la aplicación. Los mensajes deben indicar archivo y fila.
3. Tratar `N/A` como dato faltante válido **únicamente** en el campo `promedio` de `ResultadoEncuestaPregunta` y en los campos opcionales de texto. En cualquier otro campo obligatorio, `N/A` sigue siendo un dato inválido.
4. Rechazar un `promedio` igual a `0` en `ResultadoEncuestaPregunta` como dato inválido.

---

## 6. Validación del cálculo contra el modelo 2025

El valor esperado del cálculo es el modelo de autoevaluación diligenciado 2025, cuyo promedio global registrado es **4,2947**. La comparación usa los valores de entrada ya consolidados del modelo y una tolerancia numérica de `1e-4`. Si una diferencia no cabe en esa tolerancia, se documenta; no se fuerza la coincidencia. Las diferencias por redondeo de notas parciales (por ejemplo, 4,4667 frente a 4,5) se documentan como tales.
