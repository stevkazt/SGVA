# Guía para Interacciones con IA (Proyecto SGVA)

## Definición del Rol
- **Claude CLI:** Generación de código, refactorización y solución de errores de compilación/pruebas, adhiriéndose estrictamente a las especificaciones dadas.
- **Planificador/Arquitecto:** Pipeline externo (Humano/Gemini). Seguir especificaciones de diseño (`DESIGN_SGVA.md`) sin alterar los límites arquitectónicos de forma autónoma.

## Stack Tecnológico y Reglas Centrales
- **Lenguaje:** Kotlin (JVM 17)
- **Framework:** Spring Boot 3.x con Gradle Kotlin DSL (`build.gradle.kts`)
- **Patrón de Arquitectura:** Arquitectura Limpia (Puertos y Adaptadores)

## Restricciones Arquitectónicas (Estrictas)
1. **Aislamiento del Dominio:**
   - Paquete: `com.example.sgva.domain`
   - **NO DEBE** importar Spring Framework (`org.springframework..`), JPA/Jakarta, Jackson, ni dependencias de infraestructura.
   - La lógica del dominio debe consistir puramente en código base de Kotlin.

2. **Estructura de Paquetes:**
   - `com.example.sgva.domain` -> Entidades de negocio puras y excepciones del dominio.
   - `com.example.sgva.usecases` -> Lógica de aplicación e implementaciones de puertos primarios.
   - `com.example.sgva.infrastructure` -> Entidades de base de datos, repositorios, configuraciones de Spring, lectores CSV, adaptadores externos.

3. **Convenciones de Nombres:**
   - Los Casos de Uso deben terminar con `UseCase` (Ej: `ProcesarDatosEstudiantesUseCase`).
   - Las interfaces de repositorios/puertos en el dominio deben terminar con `Repository` o `Port`.

4. **Requisitos de Calidad de Código:**
   - Preferir la inmutabilidad de Kotlin (`val` sobre `var`).
   - Usar tipado explícito para métodos públicos.
   - Evitar bloques catch vacíos o el uso directo de la salida estándar (`println`); usar SLF4J estructurado si es necesario.

## Convención de Commits
Todos los commits deben seguir el formato **Conventional Commits** en español:
- `feat`: Nueva funcionalidad o caso de uso.
- `fix`: Corrección de un error o test fallido.
- `refactor`: Cambios en el código que no alteran la funcionalidad ni corrigen errores.
- `test`: Adición o modificación de pruebas (ArchUnit, JUnit).
- `docs`: Cambios en documentación (`DESIGN_SGVA.md`, `README.md`, etc.).
- `chore`: Tareas de mantenimiento, Gradle o configuración del entorno.

**Ejemplo de estructura:**
`<tipo>(<módulo/ámbito>): <descripción corta en minúsculas y presente>`

## Aviso de Registro Experimental
- NO leer, analizar ni intentar actualizar `PROMPT_LOG.md`.