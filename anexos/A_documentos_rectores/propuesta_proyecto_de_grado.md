# Propuesta de Proyecto de Grado (texto extraído del PDF con paginación)

Fuente: `docs/base/PropuestaProyectoDeGrado-Ajustes-JohannCastellanos-1018513231.pdf` (18 páginas), extraída con `pdftotext`. Cada `[Página N]` marca el inicio de una página del PDF original; la página 1 (índice) y las 14 a 16 (cronograma) conservan su disposición en bloques de texto fijo porque la extracción corrida desordena las celdas de la tabla.

[Página 1] *Tabla o índice conservado con su disposición original (la extracción como texto corrido desordena las celdas).*

~~~~text
                              CONTENIDO

CONTENIDO                                                        1
TÍTULO                                                           2
1. PLANTEAMIENTO DEL PROBLEMA                                    3
2. PREGUNTA DE INVESTIGACIÓN                                     3
3. JUSTIFICACIÓN                                                 4
4. OBEJTIVOS                                                     4
    4.1 Objetivo General                                         4
    4.2 Objetivos Específicos                                    5
5. MARCO DE REFERENCIA                                           5
    5.1 Arquitectura Limpia                                      5
    5.2 Calidad de Software y Mantenibilidad (ISO/IEC 25010)     6
    5.3 Deuda Técnica y Ratio de Deuda                           6
    5.4 Herramientas de Evaluación Empírica                      7
6. METODOLOGÍA PROPUESTA                                         7
    6.1 Fase 1. Definición del caso de estudio                   7
    6.2 Fase 2. Diseño Arquitectónico                            8
    6.3 Fase 3. Desarrollo Asistido por IA                       8
    6.4 Fase 4. Evolución del Sistema y Estrés Arquitectonico    9
    6.5 Fase 5. Medición y Evaluación (Instrumentación)         10
7. ALCANCE Y DELIMITACIONES                                     11
8. LIMITACIONES DEL ESTUDIO                                     12
9. RESULTADOS ESPERADOS                                         12
10. HIPÓTESIS DE INVESTIGACIÓN                                  13
11. APORTE ESPERADO DE LA INVESTIGACIÓN                         14
12. CRONOGRAMA DE TRABAJO                                       14
13. BIBLIOGRÁFIA                                                17
~~~~

[Página 2] ANÁLISIS DE LA CALIDAD ARQUITECTÓNICA Y LA DEUDA TÉCNICA EN UN SISTEMA DESARROLLADO BAJO ARQUITECTURA LIMPIA CON ASISTENCIA DE INTELIGENCIA ARTIFICIAL GENERATIVA JOHANN STEV CASTELLANOS GONZALEZ Anteproyecto de grado para optar al título de Ingeniero de Telecomunicaciones Director: MAURICIO OCHOA SANA. Magíster en Ingeniería Electrónica UNIVERSIDAD SANTO TOMÁS FACULTAD DE INGENIERÍA DE TELECOMUNICACIONES BOGOTÁ 2026

### [Página 3] 1. PLANTEAMIENTO DEL PROBLEMA

La adopción de herramientas de inteligencia artificial generativa como ChatGPT, Claude y GitHub Copilot ha transformado significativamente los procesos de desarrollo de software. Estas herramientas permiten acelerar actividades como la generación de código, diseño de componentes, documentación y pruebas [4].

Sin embargo, aunque diversos estudios reportan mejoras en la velocidad y la productividad de los desarrolladores, existe incertidumbre respecto al impacto que estas herramientas tienen sobre la calidad de la estructura del software (arquitectura) y su facilidad para recibir mantenimiento a largo plazo [2],[9].

En proyectos de software que evolucionan continuamente mediante nuevos requerimientos, resulta fundamental garantizar el cumplimiento de principios arquitectónicos como la separación de responsabilidades, el bajo acoplamiento y el control de dependencias entre las diferentes capas del código. Actualmente, las herramientas de IA tienden a generar fragmentos de código enfocados en resolver problemas específicos y aislados, lo que podría poner en riesgo el orden global de la aplicación a medida que esta crece.

Aunque existe una cantidad creciente de investigaciones sobre el uso de herramientas de inteligencia artificial generativa en ingeniería de software, todavía se observa una limitada evidencia empírica centrada específicamente en la evolución de la calidad arquitectónica, la mantenibilidad y la deuda técnica de aplicaciones desarrolladas bajo Arquitectura Limpia mediante procesos iterativos asistidos por IA.

### 2. PREGUNTA DE INVESTIGACIÓN

¿De qué manera el uso continuo de inteligencia artificial generativa, priorizando la corrección funcional, altera el cumplimiento de las reglas de dependencias y la deuda técnica en un desarrollo iterativo bajo Arquitectura Limpia?

### [Página 4] 3. JUSTIFICACIÓN

La inteligencia artificial generativa se está incorporando rápidamente a los procesos de desarrollo de software. Sin embargo, gran parte de la literatura y el interés actual se concentra en medir incrementos de productividad y reducción de tiempos de implementación [1].

Esta investigación busca aportar evidencia sobre aspectos menos explorados, relacionados con la calidad de la estructura interna de los sistemas producidos con asistencia de IA. Para los ingenieros actuales, es crítico conocer si la velocidad que ofrece la IA genera un costo oculto en desorden o código difícil de mantener en el futuro. Los resultados de este trabajo podrán servir como referencia para organizaciones, instituciones educativas y equipos de desarrollo que buscan adoptar estas tecnologías de forma responsable.

Asimismo, el uso del Sistema de Gestión Visual de Datos para la Autoevaluación Académica (SGVA) como caso de estudio garantiza que la evaluación se realice sobre una necesidad institucional real y con una complejidad representativa, alejando la investigación de entornos de prueba artificiales o excesivamente simplificados.

### 4.OBJETIVOS

#### 4.1 Objetivo General

Analizar la calidad arquitectónica y la mantenibilidad de un sistema estructurado bajo Arquitectura Limpia, utilizando herramientas de inteligencia artificial generativa como asistencia principal en un ciclo de desarrollo iterativo, a través de análisis estático automatizado y verificación de dependencias.

#### [Página 5] 4.2 Objetivos Específicos

1. Establecer el diseño arquitectónico base del caso de estudio (SGVA) y el protocolo de interacción (prompts) con las herramientas de inteligencia artificial generativa.

2. Desarrollar las iteraciones funcionales del sistema priorizando la corrección funcional de los fragmentos de código generados por la inteligencia artificial, minimizando la refactorización estructural humana.

3. Medir los indicadores de deuda técnica y el número de violaciones a las reglas de dependencias al finalizar cada iteración, utilizando las herramientas de análisis estático SonarQube y ArchUnit.

4. Comparar la evolución de las métricas obtenidas entre las distintas versiones del software para identificar tendencias de degradación o sostenimiento en la estructura interna de la aplicación.

### 5. MARCO DE REFERENCIA

#### 5.1 Arquitectura Limpia

La Arquitectura Limpia (Clean Architecture), propuesta por Robert C. Martin, es un enfoque de diseño de software orientado a maximizar la mantenibilidad y el aislamiento de la lógica de negocio frente a tecnologías externas [12]. Su principio fundamental es la Regla de Dependencia, la cual estipula que las dependencias de código solo pueden apuntar hacia adentro, en dirección a las capas de abstracción más alta (Dominio y Casos de Uso). En esta investigación, este concepto se operacionaliza mediante restricciones estructurales rígidas, donde cualquier acoplamiento inverso (desde el núcleo hacia la infraestructura) constituye una anomalía de diseño verificable de forma automatizada.

#### [Página 6] 5.2 Calidad de Software y Mantenibilidad (ISO/IEC 25010)

De acuerdo con el estándar internacional ISO/IEC 25010, la mantenibilidad es el atributo de calidad que representa la eficacia y eficiencia con la que un producto de software puede ser modificado [7]. Este atributo se subdivide en características mensurables: modularidad, reusabilidad, analizabilidad, capacidad de ser modificado y capacidad de ser probado. Para efectos de este estudio, la mantenibilidad se evaluará formalmente a través del método SQALE (Software Quality Assessment based on Lifecycle Evaluation) implementado en SonarQube, el cual traduce la complejidad ciclomática, la duplicación y las violaciones de código en un índice de mantenibilidad calificado de la A a la E.

#### 5.3 Deuda Técnica y Ratio de Deuda

La deuda técnica (metáfora acuñada por Ward Cunningham) describe el costo acumulado por adoptar soluciones de desarrollo rápidas pero subóptimas a nivel estructural [6]. En lugar de utilizar métricas absolutas de tiempo (horas de trabajo estimadas para reparar el código), que tienden a distorsionarse de forma natural a medida que el tamaño del sistema crece, esta investigación adopta el \*\*Ratio de Deuda Técnica (Technical Debt Ratio)\*\*. Este indicador matemático calcula la relación entre el esfuerzo requerido para corregir los defectos del sistema y el esfuerzo estimado para reescribir dicho código desde cero, permitiendo una comparación normalizada y objetiva entre bases de código de diferentes tamaños.

#### [Página 7] 5.4 Herramientas de Evaluación Empírica

SonarQube: Plataforma de análisis estático que evalúa el código fuente para detectar "olores de código" (code smells), vulnerabilidades y mantenibilidad mediante reglas estandarizadas e independientes de la plataforma de desarrollo [17].

ArchUnit: Librería de código abierto para Java/Kotlin (o herramientas análogas según el ecosistema seleccionado) diseñada de manera específica para verificar la arquitectura del sistema mediante pruebas unitarias de infraestructura. Permite codificar las reglas de dependencias concéntricas de la Arquitectura Limpia como aserciones ejecutables, garantizando una detección binaria (cumple/no cumple) de las violaciones de capa [18].

### 6. METODOLOGÍA PROPUESTA

#### 6.1 Fase 1. Definición del caso de estudio

Selección y delimitación del Sistema de Gestión Visual de Datos para la Autoevaluación Académica (SGVA) como caso de estudio. La aplicación estará orientada a apoyar los procesos de autoevaluación y renovación de Registro Calificado mediante la consolidación, procesamiento y visualización de información académica proveniente de diferentes dependencias institucionales. El alcance del sistema se limitará a la carga de datos mediante archivos Excel y CSV, la generación de indicadores de calidad, tableros de control interactivos y reportes ejecutivos relacionados con estudiantes, cuerpo docente y resultados de encuestas. Esta delimitación permitirá desarrollar y evaluar la solución de manera individual, manteniendo una complejidad adecuada para el proyecto de investigación y garantizando la aplicación de principios de arquitectura de software y mantenibilidad apoyados por herramientas de inteligencia artificial generativa.

#### [Página 8] 6.2 Fase 2. Diseño Arquitectónico

Diseño de la estructura inicial de la aplicación utilizando Arquitectura Limpia. Se definirán formalmente las capas del proyecto (Dominio, Casos de Uso, Adaptadores e Infraestructura) y las reglas de comunicación permitidas entre ellas.

#### 6.3 Fase 3. Desarrollo Asistido por IA

El desarrollo se realizará utilizando Claude y Github Copilot como asistentes principales. Para garantizar la reproducibilidad del experimento y evitar que la forma de estructurar las peticiones altere las mediciones, se definirá al inicio de la ejecución un protocolo de interacción (prompting protocol).

A nivel metodológico para este anteproyecto, se establecen las siguientes reglas base para la interacción con la IA:

- Definición de Contexto Global (System Prompt): Al inicio de cada sesión de desarrollo, se proporcionará a la herramienta de IA un contexto explícito sobre las reglas del proyecto (ej. "Actúa como un desarrollador experto. Este proyecto sigue estrictamente la Arquitectura Limpia. Las capas son Dominio, Casos de Uso, Adaptadores e Infraestructura. La regla de dependencia siempre apunta hacia adentro").
- Peticiones Funcionales (Task Prompts): Durante la construcción de características, las peticiones se enfocarán en la necesidad del negocio y no en la microgestión estructural (ej. "Implementa la funcionalidad para validar la estructura de un archivo Excel de docentes", en lugar de "Crea un adaptador en la capa de infraestructura para leer Excel").
- Criterio de Aceptación: Se evaluará la capacidad de la IA para mantener las reglas arquitectónicas dadas en el contexto global de manera autónoma. Las respuestas se aceptarán si cumplen funcionalmente con el requerimiento, para observar si la

[Página 9] herramienta prioriza la rapidez de la solución sobre el respeto al contexto arquitectónico provisto.

#### 6.4 Fase 4. Evolución del Sistema y Estrés Arquitectónico

El Sistema de Gestión Visual de Datos para la Autoevaluación Académica (SGVA) será desarrollado mediante iteraciones funcionales y estructurales que permitan evaluar la capacidad de adaptación y mantenibilidad de la arquitectura. Las versiones propuestas son:

- Versión 1 (Construcción del Núcleo): Implementación de las funcionalidades básicas para la carga de archivos Excel y CSV, validación de datos, y almacenamiento en un repositorio ligero local (ej. SQLite o en memoria).

Visualización inicial de indicadores.

- Versión 2 (Expansión Funcional): Desarrollo de nuevos casos de uso agregando los módulos de análisis para estudiantes y resultados de aprendizaje. Esta iteración representa el crecimiento natural por acumulación de requerimientos.
- Versión 3 (Estrés Arquitectónico - Sustitución de Persistencia): Modificación profunda en la capa de infraestructura. Se reemplazará el almacenamiento local por un motor de base de datos relacional robusto (ej. PostgreSQL desplegado en un entorno Linux/Docker). El objetivo es verificar si la IA, al implementar los nuevos adaptadores de persistencia, logra hacerlo sin alterar las interfaces y entidades de la capa de Dominio creadas en las versiones anteriores.
- Versión 4 (Estrés Arquitectónico - Integración Externa): Sustitución del mecanismo de entrega de información. En lugar de procesar y visualizar los datos únicamente en tableros web, se integrará una librería externa para la generación de reportes ejecutivos en PDF y la exportación de información. Esto forzará la creación de nuevos presentadores y controladores, poniendo a prueba el aislamiento de los casos de uso.

[Página 10] Esta evolución alternada (crecimiento funcional seguido de cambios de infraestructura) permitirá observar si la Arquitectura Limpia, generada y mantenida con asistencia de IA, sobrevive a las modificaciones de fondo o si, por el contrario, los cambios externos terminan acoplándose y contaminando las capas internas del sistema.

#### 6.5 Fase 5. Medición y Evaluación (Instrumentación)

Para garantizar que las mediciones entre las versiones V1 y V4 sean estrictamente comparables y no dependan del simple aumento en el volumen de código, se establecen las siguientes condiciones de medición:

- SonarQube (Deuda Técnica y Calidad): Se configurará un perfil de calidad (Quality Profile) fijo e inmutable durante todo el experimento. En lugar de medir la deuda técnica en valores absolutos de tiempo, el indicador clave será el Ratio de Deuda Técnica (Technical Debt Ratio), el cual calcula la proporción del costo de remediación frente al costo de desarrollo de las líneas de código. Esto permitirá evaluar si la IA degrada el código más rápido de lo que lo construye.
- ArchUnit (Cumplimiento Arquitectónico): Para evitar circularidad en la evaluación, el conjunto de reglas de dependencias será definido, programado y bloqueado manualmente por el investigador antes de iniciar la Fase 3, sin asistencia de la IA. Estas reglas verificarán, por ejemplo, que las dependencias fluyan exclusivamente desde la Infraestructura y los Adaptadores hacia el núcleo de Casos de Uso y Dominio. El indicador clave será la Tasa de Violaciones Arquitectónicas por cada 1000 líneas de código (KLOC) en cada iteración.

Criterios de Interpretación de Resultados: Para garantizar un análisis objetivo de las métricas extraídas en cada iteración, se establecen los siguientes umbrales de evaluación:

[Página 11]

- Para el Ratio de Deuda Técnica (SonarQube): Se adoptará la escala de mantenibilidad estándar SQALE. Un ratio =< 5% (Calificación A) se interpretará como una arquitectura saludable y sostenible. Ratios entre 6% y 10% (Calificación B) representarán una degradación aceptable. Cualquier ratio superior al 10% (Calificaciones C, D o E) se interpretará como una degradación estructural severa atribuible a las decisiones de la IA.
- Para la Tasa de Violaciones (ArchUnit): Dado el rigor de la regla de dependencias de la Arquitectura Limpia, el umbral de tolerancia será de cero (0). Cualquier tasa superior a 0 violaciones por KLOC en las reglas de aislamiento de las capas de Dominio y Casos de Uso será catalogada como una ruptura arquitectónica crítica.

### 7. ALCANCE Y DELIMITACIONES

La presente investigación se centrará en el desarrollo y evaluación del Sistema de Gestión Visual de Datos para la Autoevaluación Académica (SGVA) como caso de estudio representativo de una aplicación empresarial de complejidad moderada.

El sistema será desarrollado siguiendo los principios de Arquitectura Limpia y evolucionará a través de cuatro iteraciones funcionales que simularán el crecimiento progresivo de un proyecto de software real. Durante el proceso de desarrollo se utilizarán herramientas de inteligencia artificial generativa como apoyo principal para la generación de código, documentación y pruebas.

Con el fin de observar de forma objetiva el comportamiento de dichas herramientas sobre la calidad arquitectónica del sistema, la intervención humana se limitará principalmente a la validación funcional de las soluciones propuestas por la IA. Las sugerencias generadas serán aceptadas siempre que satisfagan los requisitos funcionales y no presenten errores de compilación o ejecución evidentes. No se realizarán refactorizaciones preventivas

[Página 12] orientadas exclusivamente a mejorar métricas de calidad o corregir posibles deficiencias arquitectónicas generadas por la inteligencia artificial.

La evaluación se enfocará exclusivamente en los atributos de mantenibilidad, deuda técnica y cumplimiento de restricciones arquitectónicas. Aspectos como rendimiento, seguridad, experiencia de usuario, accesibilidad, aceptación por parte de usuarios finales o viabilidad comercial no forman parte del alcance de esta investigación.

Las mediciones se realizarán mediante las herramientas SonarQube y ArchUnit al finalizar cada iteración de desarrollo, permitiendo analizar la evolución de las métricas obtenidas a lo largo del crecimiento funcional del sistema.

### 8. LIMITACIONES DEL ESTUDIO

Los resultados obtenidos corresponderán a un único caso de estudio y a un conjunto específico de herramientas de inteligencia artificial generativa disponibles durante el periodo de investigación. Por lo tanto, las conclusiones deberán interpretarse dentro de dicho contexto y no necesariamente serán generalizables a todos los tipos de proyectos o modelos de IA existentes.

### 9. RESULTADOS ESPERADOS

- Caracterización Cuantitativa: Describir la trayectoria evolutiva de los indicadores de mantenibilidad y densidad de defectos en una base de código a medida que se incrementa la asistencia de la IA generativa.
- Taxonomía de Fallos Arquitectónicos: Clasificar y catalogar las violaciones estructurales y de acoplamiento más recurrentes en las que incurren los modelos de lenguaje cuando operan bajo restricciones arquitectónicas formales.

[Página 13]

- Evaluación del Ratio de Deuda: Determinar el comportamiento del Ratio de Deuda Técnica ante escenarios puntuales de estrés de infraestructura (migración de componentes externos).
- Corpus de Código Evolutivo: Disponer de un repositorio de acceso público con el historial de confirmaciones (commits) secuenciales e interacciones documentadas (prompts), que sirva como base empírica replicable para futuras investigaciones en ingeniería de software asistida.
- Guía de Buenas Prácticas: Estructurar un conjunto de recomendaciones metodológicas y restricciones de contexto (system prompts) dirigidas a desarrolladores para mitigar la degradación arquitectónica al usar asistentes de IA.

### 10. HIPÓTESIS DE INVESTIGACIÓN

El uso de inteligencia artificial generativa como asistencia principal para la codificación iterativa bajo Arquitectura Limpia, cuando se prioriza la rapidez funcional sobre la intervención estructural humana, produce un incremento progresivo en el Ratio de Deuda Técnica y en la Tasa de Violaciones Arquitectónicas ante escenarios de estrés estructural (cambio de dependencias o tecnologías externas).

[Página 14] *Tabla o índice conservado con su disposición original (la extracción como texto corrido desordena las celdas).*

~~~~text
11.          APORTE                     ESPERADO                      DE           LA
INVESTIGACIÓN
La investigación aportará evidencia empírica sobre el comportamiento de herramientas de
inteligencia artificial generativa en escenarios de desarrollo de software basados en
Arquitectura Limpia. Los resultados permitirán comprender cómo evolucionan la deuda
técnica, la mantenibilidad y el cumplimiento arquitectónico cuando la IA es utilizada como
asistente principal durante el ciclo de desarrollo. Adicionalmente, se generarán
recomendaciones prácticas para el uso responsable de estas herramientas en proyectos que
requieran altos estándares de calidad estructural.

12. CRONOGRAMA DE TRABAJO

 Mes       Fase y Actividades de Investigación        Entregables Tangibles

 Mes       Fase 1 y 2:         Infraestructura y      • Documento de revisión
 1         Protocolo Base                             bibliográfica finalizado.

           • Consolidación del estado del arte        • Repositorio inicial de Git con
           final.                                     suite de pruebas de cumplimiento
                                                      arquitectónico        (ArchUnit)
                                                      configurada.
           • Configuración del servidor local de
           SonarQube (vía Docker) y definición
           del Quality Profile.
~~~~

[Página 15] *Tabla o índice conservado con su disposición original (la extracción como texto corrido desordena las celdas).*

~~~~text
       • Codificación manual de las reglas de     • Manual/Documento base del
       control arquitectónico en ArchUnit.        protocolo de ingeniería de
                                                  prompts.

       • Definición formal del protocolo y
       plantillas de prompts globales.

       • Diseño del modelo de dominio base
       del SGVA.

 Mes   Fase 3 y 4: Línea Base y Acumulación       • Código fuente de V1 y V2
 2     (V1 & V2)                                  integrado en el repositorio.

       • Ejecución de la Versión 1 (Núcleo        • Log estructurado de prompts
       funcional y persistencia en memoria)       (PROMPT_LOG.md) e historial
       aplicando el protocolo estricto de         de interacciones.
       prompts.
                                                  • Reporte preliminar de
       • Ejecución de la Versión 2 (Expansión     métricas de SonarQube y
       funcional por acumulación).                ArchUnit (Comparativa V1 vs
                                                  V2).
       • Registro estructurado y continuo de la
       trazabilidad de prompts en cada
       subversión.• Extracción de métricas
       estáticas iniciales.
~~~~

[Página 16] *Tabla o índice conservado con su disposición original (la extracción como texto corrido desordena las celdas).*

~~~~text
 Mes   Fase 4: Escenarios de             Estrés    • Código fuente de V3 y V4
 3     Arquitectónico (V3 & V4)                    finalizado.

       • Desarrollo de la Versión 3 (Migración     •    Registro     completo    de
       estructural a PostgreSQL mediante           interacciones       de     estrés
       asistentes de IA).                          arquitectónico.

       • Desarrollo de la Versión 4 (Integración   • Reportes de violaciones de
       de componentes de terceros para             capas de ArchUnit y reporte
       reportes en PDF).                           comparativo      intermedio   de
                                                   métricas de estrés en SonarQube.
       • Ejecución automatizada de análisis
       estático y arquitectónico al cierre de
       cada subversión.

 Mes   Fase 5: Análisis de Datos y Redacción       • Informe final de tesis
 4     Final                                       redactado en su totalidad y
                                                   estructurado     bajo normas
                                                   institucionales.
       •    Procesamiento     estadístico   y
       comparativo del Ratio de Deuda
       Técnica y la Tasa de Violaciones            • Estructura base, metodología
       (KLOC) entre todas las versiones (V1 a      y consolidación de datos listos
       V4).                                        para el artículo de divulgación.

       • Redacción de la discusión de
       resultados y validación/refutación de la
       hipótesis.

       • Construcción de la guía de
       recomendaciones        prácticas para
       desarrollo asistido por IA.
~~~~

### [Página 17] 13. BIBLIOGRAFÍA

[1] A. M. Dakhel et al., "GitHub Copilot AI Pair Programmer: Asset or Liability?," Journal of Systems and Software, vol. 203, p. 111734, 2023. Disponible en: https://doi.org/10.1016/j.jss.2023.111734

[2] S. Fischer y C. Lanquillon, Evaluation of Generative AI-Assisted Software Design and Engineering: A User-Centered Approach. Cham, Suiza: Springer Nature Switzerland, 2024. Disponible en: https://doi.org/10.1007/978-3-031-60606-9_3

[3] K. He et al., "Bridging Generation and Training: A Systematic Review of Quality Issues in LLMs for Code," arXiv preprint arXiv:2405.05267, 2024. Disponible en: https://arxiv.org/abs/2405.05267

[4] X. Hou et al., "Large Language Models for Software Engineering: A Systematic Literature Review," ACM Transactions on Software Engineering and Methodology, vol. 33, no. 8, 2024. Disponible en: https://doi.org/10.1145/3695988

[5] S. R. Chidamber y C. F. Kemerer, "A Metrics Suite for Object Oriented Design," IEEE Transactions on Software Engineering, vol. 20, no. 6, pp. 476–493, jun. 1994.

[6] W. Cunningham, "The WyCash Portfolio Management System," en OOPSLA '92 Experience Report, Vancouver, Canadá, 1992.

[7] International Organization for Standardization (ISO)

e International Electrotechnical Commission (IEC), "Systems and Software Engineering — Systems and Software Quality Requirements and Evaluation (SQuaRE) — System and Software Quality Models," ISO/IEC 25010:2011, 2011.

[8] T. McDonnell, N. Nagappan, M. Nagappan y L. Williams, "An Empirical Study of Technical Debt," en Proceedings of the 2013 International Symposium on Empirical Software Engineering and Measurement (ESEM), Baltimore, EE. UU., 2013.

[Página 18] [9] N. Selvanayagam, T. A. Ghaleb y M. Abdellatif, "Self-Admitted Technical Debt in LLM Software: An Empirical Comparison with ML and Non-ML Software," arXiv preprint arXiv:2401.06266, 2024. Disponible en: https://arxiv.org/abs/2401.06266

[10] M. Fowler, Refactoring: Improving the Design of Existing Code, 2a ed. Boston, MA, EE. UU.: Addison-Wesley Professional, 2018.

[11] R. C. Martin, Agile Software Development: Principles, Patterns, and Practices. Upper Saddle River, NJ, EE. UU.: Prentice Hall, 2003.

[12] R. C. Martin, Clean Architecture: A Craftsman's Guide to Software Structure and Design. Boston, MA, EE. UU.: Prentice Hall, 2018.

[13] R. C. Martin, Clean Code: A Handbook of Agile Software Craftsmanship. Upper Saddle River, NJ, EE. UU.: Prentice Hall, 2008.

[14] R. C. Martin, The Clean Coder: A Code of Conduct for Professional Programmers. Upper Saddle River, NJ, EE. UU.: Prentice Hall, 2011.

[15] E. Evans, Domain-Driven Design: Tackling Complexity in the Heart of Software. Boston, MA, EE. UU.: Addison-Wesley Professional, 2004.

[16] V. Vernon, Implementing Domain-Driven Design. Boston, MA, EE. UU.:

Addison-Wesley Professional, 2013.

[17] SonarSource SA, "SonarQube Documentation," 2026. [En línea]. Disponible en: https://docs.sonarsource.com/sonarqube

[18] TNG Technology Consulting GmbH, "ArchUnit User Guide," 2026. [En línea]. Disponible en: https://www.archunit.org
