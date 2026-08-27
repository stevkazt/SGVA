# Diseño del Sistema: SGVA (Gestión Visual de Datos para la Autoevaluación Académica)

Este documento define la arquitectura, las estructuras de datos y las reglas de negocio del software SGVA. Actúa como el mapa conceptual y la "constitución técnica" que rige el desarrollo asistido por inteligencia artificial, asegurando el cumplimiento estricto de los principios de Arquitectura Limpia.

---

## 0. Alcance y Factores Cubiertos

El SGVA no busca digitalizar los 15 factores del modelo de autoevaluación institucional, sino automatizar exclusivamente los indicadores **cuantitativos y estructurables**. Los factores que dependen de evidencia documental o cualitativa (actas, PEP, convenios, manuales de funciones) quedan fuera de alcance y se gestionan por medios distintos al software.

### Factores cubiertos directamente (entidades propias)
- **Factor 2 – Estudiantes**: `Estudiante` (matrícula, deserción, Saber Pro).
- **Factor 3 – Profesores**: `Docente` (dedicación, nivel de formación).

### Factores cubiertos transversalmente (vía encuestas de percepción)
El campo `factor: String` de `RespuestaEncuesta` es intencionalmente abierto (no un enum cerrado) para poder etiquetar percepciones sobre múltiples factores sin crear una entidad nueva por cada uno:
- Factor 1 (coherencia con la misión), Factor 4 (satisfacción de egresados/empleadores), Factor 5 (flexibilidad curricular), Factor 7 (recursos de apoyo académico), Factor 8 (infraestructura tecnológica), Factor 11 (pertinencia social), Factor 12 (bienestar institucional).
- Estos se calculan y comparan con `CalcularPonderacionLikertUseCase` y `GenerarMatrizRadarUseCase`, agrupando por `factor` y `estamento`.

### Egresados (Factor 4)
No se modela como entidad independiente: la universidad no recopila de forma consistente datos de seguimiento a egresados (empleabilidad, ubicación laboral), por lo que crear una entidad `Egresado` añadiría complejidad sin datos reales que la respalden. Se representa únicamente como un valor del enum `EstadoEstudiante` (`GRADUADO`), y su percepción se captura, si existe, a través de `RespuestaEncuesta` con `estamento = EGRESADO`.

### Dato de referencia nacional (Saber Pro)
No se define una fuente para el promedio nacional de referencia en `ConsolidarPuntajesSaberProUseCase`. Esta información no está disponible actualmente para el proyecto — se deja **fuera de alcance de esta versión del diseño**, no como prueba de estrés deliberada, sino como una limitación real de información. Si en el futuro se decide incorporarla, requerirá una decisión de negocio explícita (¿configuración, archivo externo, puerto de servicio?) antes de tocar el código.

### Decisiones de implementación técnica delegadas a la IA
No todo vacío en este documento es una decisión de negocio pendiente. Algunas decisiones (ej. en qué paquete vive una excepción de dominio como `FormatoArchivoInvalidoException`, cómo se nombra una clase auxiliar) son derivables directamente de las restricciones ya fijadas en `CLAUDE.md` (aislamiento del dominio, convenciones de nombres) sin requerir conocimiento de negocio. Estas se dejan intencionalmente abiertas: es responsabilidad de Claude CLI resolverlas de forma consistente con las reglas arquitectónicas existentes, y su comportamiento ante estos vacíos es en sí mismo un dato de interés para la investigación.

### Factores explícitamente fuera de alcance
Factor 1 (PEP como documento), Factor 6 (planificación pedagógica), Factor 9 (investigación e innovación), Factor 10 (movilidad y convenios), Factor 13 (autoevaluación/autorregulación), Factor 14 (organización y gestión), Factor 15 (recursos financieros). Cubrirlos exigiría entidades y adaptadores completamente nuevos (`GrupoInvestigacion`, `Convenio`, `Movilidad`, etc.) no contemplados en esta versión del diseño.

---

## 0.1 Reglas de Validación de Negocio

Estas reglas deben aplicarse en la capa de dominio o casos de uso (nunca delegarse silenciosamente a la infraestructura), y son las que `ProcesarDatosEstudiantesUseCase` / `ProcesarDatosDocentesUseCase` / `ProcesarRespuestasEncuestasUseCase` deben verificar antes de aceptar un registro.

- **periodo** (en `Docente` y `RespuestaEncuesta`): `String` con formato estricto `AAAAS` (4 dígitos de año + 1 dígito de semestre, donde `S` es `1` o `2`). Ej: `"20261"` es válido, `"2026"` o `"20263"` no lo son. Regex de referencia: `^\d{4}[12]$`.
- **calificacion** (en `RespuestaEncuesta`): entero entre `1` y `5` inclusive (Escala Likert de 5 puntos). Cualquier valor fuera de ese rango es un dato corrupto y debe rechazarse.
- **puntajeSaberPro** (en `Estudiante`): entero entre `0` y `300` inclusive (rango oficial del puntaje global de las pruebas Saber Pro en Colombia). Es `null` únicamente si el estudiante aún no ha presentado la prueba — no debe usarse `0` como equivalente de "sin presentar".
- **Ventana histórica de 7 años** (`CalcularEvolucionMatriculaUseCase`): se calcula dinámicamente como los últimos 7 años calendario contados desde la fecha actual del sistema en tiempo de ejecución — nunca como un rango de años fijo/hardcodeado en el código.

- **cohorte** (en `Estudiante`): mismo formato que `periodo` — `AAAAS` sin guion (ej. `"20211"`, no `"2021-1"`). Regex de referencia: `^\d{4}[12]$`.

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
- **facultad**: String
- **nivelFormacion**: NivelFormacion (Enum: ESPECIALIZACION, MAESTRIA, DOCTORADO)
- **dedicacion**: TipoDedicacion (Enum: TIEMPO_COMPLETO, MEDIO_TIEMPO, CATEDRA)
- **periodo**: String (Ej: "20261"; ver formato válido en Sección 0.1)

### RespuestaEncuesta
- **id**: String
- **estamento**: TipoEstamento (Enum: ESTUDIANTE, PROFESOR, EGRESADO, EMPLEADOR)
- **factor**: String (Ej: "Infraestructura", "Plan de Estudios")
- **calificacion**: Int (Escala Likert de 1 a 5; ver Sección 0.1)
- **periodo**: String

### IndicadorCalidad
- **nombre**: String (Ej: "Tasa de Deserción", "Promedio Saber Pro", "Relación Estudiante/Profesor")
- **valor**: Double (Resultado numérico calculado)
- **periodo**: String
- **facultad**: String
- Nota: esta entidad es el resultado consolidado de los casos de uso del Módulo 1 y 2 (Factores 2 y 3); no debe omitirse en diagramas o refactorizaciones, ya que es el output principal que consume el Dashboard.

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

---

## 3. Interfaces de la Aplicación (Puertos)

Interfaces que definen los límites por los cuales la lógica de negocio se comunica con el mundo exterior (Inversión de Dependencia).

### Puertos de Entrada (Inbound/Driving Ports)
- **IngestarArchivoUseCase**: Interfaz expuesta para disparar la carga y lectura de datos.

### Puertos de Salida (Outbound/Driven Ports)
- **AcademicDataRepository**: Interfaz para persistir o recuperar las entidades de dominio procesadas (Spring Data/In-Memory).
- **DocumentParserPort**: Interfaz para delegar la extracción física de datos según el tipo de archivo (.csv / .xlsx). *(Renombrado desde `DocumentParser` para cumplir la convención de sufijos `Repository`/`Port` de `CLAUDE.md`.)*

---

## 4. Capa de Adaptadores e Infraestructura (Detalles Externos)

Implementaciones técnicas expuestas al cambio tecnológico. El Dominio e interfaces no dependen de estas clases.

### Adaptadores de Entrada (Primary Adapters)
- **Web/CLI Controller**: Controladores que reciben las peticiones de carga de archivos y exponen los endpoints para los gráficos del Dashboard.

### Adaptadores de Salida (Secondary Adapters)
- **ApachePoiExcelParser**: Implementación física encargada de abrir y parsear archivos binarios de Excel (.xlsx).
- **OpenCsvParser**: Implementación física encargada de procesar archivos delimitados por comas (.csv).

---

## 5. Protocolo de Validación en Origen

Para asegurar la estabilidad del sistema ante errores del usuario, todo adaptador que implemente `DocumentParserPort` debe:
1. Verificar la existencia estricta de las columnas obligatorias definidas por entidad antes de transferir datos al Caso de Uso.
2. Emitir una excepción controlada (Ej: `FormatoArchivoInvalidoException`) si existen tipos de datos corruptos o cabeceras faltantes, garantizando alertas claras sin romper la ejecución de la aplicación.
