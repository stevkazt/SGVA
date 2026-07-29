# Diseño del Sistema: SGVA (Gestión Visual de Datos para la Autoevaluación Académica)

Este documento define la arquitectura, las estructuras de datos y las reglas de negocio del software SGVA. Actúa como el mapa conceptual y la "constitución técnica" que rige el desarrollo asistido por inteligencia artificial, asegurando el cumplimiento estricto de los principios de Arquitectura Limpia.

---

## 1. Capa de Dominio (Entidades Centrales)

Las entidades son objetos de negocio puros, independientes de frameworks, bases de datos o sistemas de archivos.

### Estudiante
- **id**: String (Identificador único anónimo)
- **programa**: String (Ej: "Ingeniería de Telecomunicaciones")
- **facultad**: String (Ej: "Ingeniería")
- **cohorte**: String (Ej: "2021-1")
- **estado**: EstadoEstudiante (Enum: MATRICULADO, GRADUADO, DESERTOR)
- **puntajeSaberPro**: Int? (Puntaje global obtenido en las pruebas de Estado, opcional/nullable si aún no las presenta)

### Docente
- **id**: String (Identificador único)
- **facultad**: String
- **nivelFormacion**: NivelFormacion (Enum: ESPECIALIZACION, MAESTRIA, DOCTORADO)
- **dedicacion**: TipoDedicacion (Enum: TIEMPO_COMPLETO, MEDIO_TIEMPO, CATEDRA)
- **periodo**: String (Ej: "20261")

### RespuestaEncuesta
- **id**: String
- **estamento**: TipoEstamento (Enum: ESTUDIANTE, PROFESOR, EGRESADO, EMPLEADOR)
- **factor**: String (Ej: "Infraestructura", "Plan de Estudios")
- **calificacion**: Int (Escala Likert de 1 a 5)
- **periodo**: String

### IndicadorCalidad
- **nombre**: String (Ej: "Tasa de Deserción", "Promedio Saber Pro", "Relación Estudiante/Profesor")
- **valor**: Double (Resultado numérico calculado)
- **periodo**: String
- **facultad**: String

---

## 2. Capa de Casos de Uso (Lógica de Negocio)

Clases que implementan las reglas específicas de la aplicación y orquestan el flujo de datos.

### Módulo 1: Estudiantes y Resultados
- **ProcesarDatosEstudiantes**: Recibe los registros mapeados del exterior, valida la integridad elemental y los agrupa.
- **CalcularEvolucionMatricula**: Agrupa y cuenta los estudiantes con estado `MATRICULADO` por año y semestre durante la ventana histórica de 7 años.
- **CalcularTasaDesercion**: Calcula el porcentaje de estudiantes en estado `DESERTOR` frente al total de la cohorte o periodo seleccionado.
- **ConsolidarPuntajesSaberPro**: Calcula el promedio global y por cohorte de los puntajes registrados de las pruebas Saber Pro para compararlos contra la referencia nacional.

### Módulo 2: Cuerpo Docente
- **ProcesarDatosDocentes**: Valida e ingresa los registros de la planta profesoral del periodo.
- **CalcularDistribucionFormacion**: Agrupa los docentes por `NivelFormacion` para determinar los porcentajes de maestrías y doctorados.
- **CalcularCapacidadInstalada**: Determina la relación cuantitativa entre el total de estudiantes matriculados y el número de profesores equivalentes a Tiempo Completo (TC).

### Módulo 3: Percepción y Encuestas
- **ProcesarRespuestasEncuestas**: Tabula de forma automatizada los formularios cargados.
- **CalcularPonderacionLikert**: Promedia las calificaciones numéricas obtenidas agrupándolas por `factor` y `estamento`.
- **GenerarMatrizRadar**: Organiza los promedios de los distintos estamentos ante un mismo factor para permitir su posterior comparación visual.

---

## 3. Interfaces de la Aplicación (Puertos)

Interfaces que definen los límites por los cuales la lógica de negocio se comunica con el mundo exterior (Inversión de Dependencia).

### Puertos de Entrada (Inbound/Driving Ports)
- **IngestarArchivoUseCase**: Interfaz expuesta para disparar la carga y lectura de datos.

### Puertos de Salida (Outbound/Driven Ports)
- **AcademicDataRepository**: Interfaz para persistir o recuperar las entidades de dominio procesadas (Spring Data/In-Memory).
- **DocumentParser**: Interfaz para delegar la extracción física de datos según el tipo de archivo (.csv / .xlsx).

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

Para asegurar la estabilidad del sistema ante errores del usuario, todo adaptador que implemente `DocumentParser` debe:
1. Verificar la existencia estricta de las columnas obligatorias definidas por entidad antes de transferir datos al Caso de Uso.
2. Emitir una excepción controlada (Ej: `FormatoArchivoInvalidoException`) si existen tipos de datos corruptos o cabeceras faltantes, garantizando alertas claras sin romper la ejecución de la aplicación.