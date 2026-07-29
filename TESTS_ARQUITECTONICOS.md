# Justificación Metodológica: Control Automatizado de Reglas Arquitectónicas

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
| **Aislamiento de Capas** | El dominio y casos de uso no dependen de `infrastructure` ni de frameworks (`Spring`, `Jackson`). | *Clean Architecture* (Martin, 2017). Garantiza la independencia tecnológica del núcleo de negocio. |
| **Desacoplamiento** | Prohibición de anotaciones del framework (`@Service`, `@Component`) en el núcleo. | Principio de Inversión de Dependencias (DIP). Mantiene la portabilidad del código de negocio sin atarlo a un runtime específico. |
| **Convenciones de Nomenclatura** | Sufijos obligatorios (`UseCase`, `Repository`, `Exception`, `Controller`). | *Domain-Driven Design* (Evans, 2003) y Guías de Estilo Estándar de Kotlin/Java. Facilita la legibilidad y la navegabilidad del proyecto. |
| **Calidad de Código** | Prohibición del uso de salidas estándar por consola (`println`). | Práctica estándar de registro (Logging) en sistemas empresariales (OWASP / Clean Code). |

---

## 4. Función en el Diseño Experimental de la Tesis

En la metodología del proyecto, el desarrollo es asistido por un agente de Inteligencia Artificial (Claude CLI). La suite de ArchUnit cumple un rol puramente experimental:

1. **Definición de Límites (Guardrails):** Garantiza que todo código generado por el agente, independientemente de la tecnología subyacente, respete los límites arquitectónicos prefijados en el diseño (`DESIGN_SGVA.md`).
2. **Medición Objetiva (Determinismo):** Proporciona un resultado binario (*Pasa / No Pasa*) en cada iteración ($V_0, V_1, V_2, \dots$). Esto elimina la subjetividad del investigador al evaluar si una iteración mantuvo o no la integridad del software.
3. **Métrica de Desviación:** Si una prueba falla tras la ejecución de un prompt, el fallo se registra objetivamente en el log del experimento (`PROMPT_LOG.md`) como un evento de *Restauración Arquitectónica*, permitiendo cuantificar el grado de supervisión requerido durante el proceso.

---