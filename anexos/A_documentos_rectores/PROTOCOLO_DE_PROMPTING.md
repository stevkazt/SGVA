# Protocolo de Interacción con la IA — SGVA

**Proyecto:** SGVA (Sistema de Gestión Visual de Datos para la Autoevaluación Académica)
**Herramienta bajo estudio:** Claude Code CLI

Este documento fija el protocolo de interacción con la IA para las iteraciones del proyecto, estructurado en los tres componentes definidos en la Fase 3 del anteproyecto: Contexto Global, Peticiones Funcionales y Criterio de Aceptación.

---

## 1. Definición de Contexto Global

El proyecto opera bajo un contexto fijo, definido en `CLAUDE.md`, que se mantiene constante durante toda la sesión de trabajo independientemente de la iteración en curso. Este contexto establece:

- Las cuatro capas del proyecto (Dominio, Casos de Uso, Infraestructura) y la Regla de Dependencia (las dependencias apuntan hacia adentro).
- Prohibición de que Dominio y Casos de Uso dependan de Spring, Jakarta/javax o Jackson.
- Convenciones de nomenclatura (sufijos `UseCase`, `Repository`/`Port`, `Exception`, `Controller`).
- Prohibición de bloques `catch` vacíos y de uso de `println`.
- Protocolo de commits: Conventional Commits, ejecutados manualmente por el investigador — la IA nunca comete cambios.

`ArchitectureTest.kt` verifica de forma automatizada el cumplimiento de este contexto: toda sesión ejecuta la suite completa antes de considerarse cerrada.

## 2. Peticiones Funcionales (Task Prompts)

Principio rector: la petición describe **qué** debe hacer el sistema desde la perspectiva del negocio, nunca **cómo** debe estructurarse internamente. Una petición nombra una capa o un tipo de artefacto únicamente cuando el propio requerimiento lo exige por definición (por ejemplo, pedir un puerto de persistencia o un controlador REST porque es literalmente el artefacto solicitado), nunca como microgestión estructural adicional. Las decisiones de diseño internas — dónde ubicar un objeto de valor, si reutilizar o crear una entidad, cómo nombrar un adaptador — quedan a criterio de la IA, sujetas a verificación automatizada después de generarse, no restringidas por adelantado en el texto de la petición.

Ejemplos ilustrativos del estilo de petición que aplica este principio:

- "Necesito modelar las entidades de negocio para gestionar estudiantes y docentes... Impleméntalo de forma que las reglas de validación de formato y rango se apliquen correctamente."
- "Implementa los casos de uso para registrar y consultar estudiantes y docentes. Define los puertos de persistencia necesarios en la capa de dominio y una implementación en memoria en la capa de infraestructura para esta versión."
- "Implementa la funcionalidad para cargar archivos CSV y Excel... Cualquier inconsistencia de formato en los datos debe normalizarse en la capa de infraestructura."
- "Implementa los casos de uso para calcular la evolución de la matrícula estudiantil, la tasa de deserción y la consolidación de puntajes Saber Pro, generando los indicadores de calidad correspondientes."
- "Implementa un controlador REST que exponga la carga de archivos, el registro y consulta de estudiantes y docentes, y los indicadores de calidad calculados."
- "Implementa el registro y la tabulación automatizada de respuestas de encuestas de percepción, para los estamentos y factores definidos en DESIGN_SGVA.md."
- "Implementa la generación de reportes ejecutivos en PDF que exporten los indicadores de calidad y las respuestas de encuestas ya registradas en el sistema, expuestos mediante un nuevo endpoint REST."

Ninguna petición especifica nombres de clases concretas, paquetes internos o algoritmos de implementación más allá del contrato de negocio solicitado.

## 3. Criterio de Aceptación

Una respuesta de la IA se acepta cuando: (a) compila, (b) pasa la suite de pruebas vigente (existente más la nueva generada en la sesión), y (c) satisface el requerimiento funcional planteado. Las decisiones arquitectónicas autónomas de la IA no se aprueban ni rechazan en el momento de la petición — se evalúan después, contra `ArchitectureTest.kt` y SonarQube, nunca restringidas de antemano en el texto del prompt.

## 4. Constantes de flujo de trabajo

- Cada sesión cierra con un prompt de registro explícito ("Registra el log de esta sesión en logs/V{N}/P-{ID}.md"), generado por la propia IA sin leer ni modificar `PROMPT_LOG.md` (reservado al hook `SessionEnd`).
- La IA reporta verificación (`./gradlew test`, y según la sesión `./gradlew assemble` o pruebas manuales end-to-end con `curl`/`bootRun`) antes de considerar cerrada la sesión.
- Ningún commit se ejecuta dentro de la sesión de IA; el investigador revisa y comete manualmente después.
