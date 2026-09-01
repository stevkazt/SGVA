Justificación Metodológica: Control Automatizado de Reglas Arquitectónicas
**Proyecto:** SGVA (Sistema de Gestión Visual de Datos para la Autoevaluación Académica)
**Dominio Técnico:** Evaluación de la Mantenibilidad y Calidad Estructural en Software Asistido por Inteligencia Artificial.
---
## 1. Propósito y Marco Académico
Este documento define la función metodológica de las pruebas de arquitectura estáticas (`ArchitectureTest.kt`) implementadas con la herramienta ArchUnit.
En el contexto de esta investigación, la suite de pruebas de arquitectura no se incluye como una preferencia arbitraria de implementación, sino como el **mecanismo de control de variables y validación continua** del atributo de calidad de **Mantenibilidad**, según lo define el estándar internacional **ISO/IEC 25010**.
---
## 2. Fundamentación Estándar de la Industria
El uso de pruebas de arquitectura automatizadas responde a tres principios consolidados en la ingeniería de software moderna:
### 2.1. Cumplimiento del Estándar ISO/IEC 25010 (Mantenibilidad y Modularidad)
El estándar ISO/IEC 25010 define la *Modularidad* como el grado en que un sistema se compone de componentes discretos tales que un cambio en un componente tiene un impacto mínimo en los demás.
* **Aplicación:** Las reglas de separación de capas (Dominio, Casos de Uso, Infraestructura) garantizan matemáticamente que el acoplamiento permanezca bajo y la cohesión alta.
### 2.2. Patrón Estándar de la Industria: *Fitness Functions* (Funciones de Idoneidad)
Definido por Ford, Parsons y Kua (2017) en *Building Evolutionary Architectures*, una **Fitness Function** es cualquier mecanismo que proporciona una evaluación de integridad objetiva e ininterrumpida de las características de una arquitectura de software.
* **Aplicación:** `ArchitectureTest.kt` opera como una *Fitness Function* ejecutable. En lugar de evaluar la arquitectura de forma manual o subjetiva, se delega la verificación a un motor estático computable y reproducible.
### 2.3. Principio de Inversión de Dependencias (SOLID / Clean Architecture)
Propuesto por Robert C. Martin, establece que los módulos de alto nivel (Dominio) no deben depender de los módulos de bajo nivel (Infraestructura/Frameworks); ambos deben depender de abstracciones.
* **Aplicación:** Las pruebas impiden que librerías externas de persistencia, entrada/salida o frameworks de inyección de dependencias contaminen las clases de negocio.
---
## 3. Sustentación de las Reglas Implementadas
Las verificaciones configuradas en `ArchitectureTest.kt` corresponden a prácticas estándar de diseño de software y se clasifican en tres categorías operativas:
| Categoría | Regla Evaluada | Estándar / Práctica de Referencia |
| :--- | :--- | :--- |
| **Aislamiento de Capas** | El dominio no depende de `usecases`, `infrastructure`, ni de frameworks o librerías externas (`Spring`, `Jakarta`/`javax`, `Jackson`); los casos de uso no dependen de `infrastructure` ni de `Spring`/`Jakarta`/`javax`. | *Clean Architecture* (Martin, 2017). Garantiza la independencia tecnológica del núcleo de negocio frente a cualquier framework o especificación externa, no solo Spring. |
| **Desacoplamiento** | Prohibición de anotaciones del framework (`@Service`, `@Component`, `@Autowired`) en el dominio y los casos de uso. | Principio de Inversión de Dependencias (DIP). Mantiene la portabilidad del código de negocio sin atarlo a un runtime específico. |
| **Convenciones de Nomenclatura** | Sufijos obligatorios: `UseCase` para casos de uso; `Repository` **o** `Port` para interfaces del dominio; `Exception` para excepciones de dominio; `Controller` para adaptadores REST anotados con `@RestController`. | *Domain-Driven Design* (Evans, 2003) y Guías de Estilo Estándar de Kotlin/Java. El doble sufijo `Repository`/`Port` refleja que el dominio expone tanto puertos de persistencia (`AcademicDataRepository`) como puertos de servicio genéricos (`DocumentParserPort`), ambos igualmente válidos como interfaces de salida (Outbound Ports). |
| **Calidad de Código** | Prohibición del uso de salidas estándar por consola (`println`). | Práctica estándar de registro (Logging) en sistemas empresariales (OWASP / Clean Code). |
### 3.1. Nota sobre cobertura de bloques `catch` vacíos
`CLAUDE.md` prohíbe explícitamente los bloques `catch` vacíos como requisito de calidad de código. Esta regla **no está implementada como prueba ArchUnit** en `ArchitectureTest.kt`, ya que ArchUnit no ofrece una regla predefinida equivalente a `GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS` para este caso, y su verificación depende de análisis de flujo a nivel de método más propio de un analizador estático de código que de un verificador de dependencias entre paquetes.
Esta restricción se delega a **SonarQube**, específicamente a la regla **S108** (*"Empty blocks should be removed or contain a comment"*), que sí opera a ese nivel de granularidad. Esta división de responsabilidades es intencional: ArchUnit gobierna la estructura y las dependencias entre capas (Tasa de Violaciones Arquitectónicas por KLOC); SonarQube gobierna la calidad interna del código (Ratio de Deuda Técnica). Verificar lo mismo en ambos instrumentos introduciría doble conteo al comparar las dos métricas entre versiones (V1–V4).
Verificado: la regla S108 ("Nested blocks of code should not be left empty") está activa en el Quality Profile de Kotlin usado para este proyecto (verificado el 27 de agosto de 2026, vía Rules → activation=true → s108, perfil Kotlin). Este gap queda cerrado sin necesidad de una regla ArchUnit adicional.
---
## 4. Función en el Diseño Experimental de la Tesis
En la metodología del proyecto, el desarrollo es asistido por un agente de Inteligencia Artificial (Claude CLI). La suite de ArchUnit cumple un rol puramente experimental:
1. **Definición de Límites (Guardrails):** Garantiza que todo código generado por el agente, independientemente de la tecnología subyacente, respete los límites arquitectónicos prefijados en el diseño (`DESIGN_SGVA.md`).
2. **Medición Objetiva (Determinismo):** Proporciona un resultado binario (*Pasa / No Pasa*) en cada iteración ($V_0, V_1, V_2, \dots$). Esto elimina la subjetividad del investigador al evaluar si una iteración mantuvo o no la integridad del software.
3. **Métrica de Desviación:** Si una prueba falla tras la ejecución de un prompt, el fallo se registra objetivamente en el log del experimento (`PROMPT_LOG.md`) como un evento de *Restauración Arquitectónica*, permitiendo cuantificar el grado de supervisión requerido durante el proceso.
---
## 5. Métrica: Restauraciones Arquitectónicas Autodetectadas

Además de la Tasa de Violaciones Arquitectónicas (post-hoc, medida por el investigador
al final de cada iteración), se registra un segundo indicador de menor granularidad:
todo evento en que Claude Code CLI, **dentro de una misma sesión de prompt**, genera
código que rompe una regla de `ArchitectureTest.kt`, lo detecta al ejecutar la suite de
pruebas, y lo corrige de forma autónoma antes de reportar el resultado al investigador —
sin que `ArchitectureTest.kt` sea modificado en el proceso.

### 5.1. Justificación

Estos eventos son invisibles en la medición final que ve el investigador (ArchUnit
PASS 8/8), pero representan una violación arquitectónica real ocurrida durante el
proceso generativo. Omitirlos subestimaría la frecuencia real con la que el agente
de IA genera código que viola las reglas de dependencia, y sobrestimaría su capacidad
de cumplimiento arquitectónico "de primera pasada" (*first-pass compliance*), que es
en sí misma una variable de interés distinta de la tasa de violaciones final.

### 5.2. Procedimiento de registro

Cada vez que ocurra un evento de este tipo durante una sesión, debe documentarse en
la sección "Decisiones autónomas relevantes" o "Restauración arquitectónica" del
`logs/V1/P-XXX.md` correspondiente, indicando: la regla violada, la causa técnica
concreta (qué construcción del lenguaje o patrón generó la violación) y la corrección
aplicada. Estos eventos se consolidan además en la siguiente tabla acumulativa:

| ID | Regla violada | Causa técnica | Corrección aplicada |
|---|---|---|---|
| P-002 | Clases en `usecases` deben terminar en `UseCase` | Clases de test ubicadas en el paquete `usecases` | Reubicadas al paquete de test `application` |
| P-003 | (misma regla) | DTOs `ResultadoIngesta`/`RegistroOmitido` ubicados en `usecases` | Reubicados al paquete `domain` |
| P-004 | (misma regla) | `companion object` genera una clase anidada (`$Companion`) sin sufijo `UseCase` | Constantes movidas a `domain` (`IndicadorCalidad`, `ParametrosDeAnalisis`) |
| P-004 | (misma regla) | `groupingBy { }` genera una clase anónima (`Grouping`) en `usecases` | Sustituido por `groupBy { }.mapValues { }`, sin clases anónimas |


### 5.3. Nota metodológica sobre la causa raíz común y decisión de diseño

Los cuatro eventos registrados hasta la fecha comparten la misma causa raíz: la regla
de sufijo `UseCase` se aplica, tal como está escrita, a *toda* clase no-interface del
paquete `usecases` — incluyendo clases sintéticas generadas por el compilador de
Kotlin (companions, clases anónimas de funciones inline como `groupingBy`), no solo
las clases de negocio que la regla pretendía originalmente restringir.

**Decisión:** la regla se mantiene sin modificar durante el resto del experimento
(V1–V4). Alterarla en este punto, a partir de las observaciones que la propia regla
produjo, introduciría la circularidad que la Fase 5 del anteproyecto busca evitar
explícitamente ("el conjunto de reglas de dependencias será definido, programado y
bloqueado manualmente por el investigador antes de iniciar la Fase 3"). Cambiar el
instrumento de medición a mitad del experimento también rompería la comparabilidad
de las métricas de ArchUnit entre versiones.

En consecuencia, estos eventos se interpretan como un hallazgo válido: evidencia de
que Claude Code CLI mantiene la disciplina arquitectónica incluso ante una restricción
más estricta de lo estrictamente necesario para el diseño de negocio (una regla que
también captura construcciones sintéticas del compilador). Refinar la regla para
excluir clases sintéticas queda documentado como una mejora válida para trabajo futuro,
no como una corrección aplicable a este experimento.--
