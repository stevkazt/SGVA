# SGVA - Sistema de Gestión Visual de Datos para la Autoevaluación Académica

## Descripción del Proyecto
El **SGVA** es una plataforma diseñada para automatizar la recolección, procesamiento y visualización de indicadores académicos (estudiantes, docentes y encuestas de percepción) en instituciones de educación superior.

Este repositorio también sirve como laboratorio experimental para un proyecto de grado enfocado en medir rigurosamente la evolución de la calidad arquitectónica y la deuda técnica en el código generado de manera iterativa mediante herramientas de Inteligencia Artificial Generativa.

## Arquitectura
El proyecto está estructurado bajo los principios de **Arquitectura Limpia (Clean Architecture)** y el patrón de **Puertos y Adaptadores**, garantizando que las reglas de negocio en la capa de dominio permanezcan completamente aisladas de detalles de infraestructura, bases de datos y frameworks.

## Stack Tecnológico
* **Lenguaje:** Kotlin (JVM 17)
* **Framework:** Spring Boot 3.x
* **Gestor de Dependencias:** Gradle (Kotlin DSL)
* **Pruebas de Arquitectura:** ArchUnit
* **Análisis de Código Estático:** SonarQube local

## Estructura Base de Paquetes
* `domain/`: Entidades centrales del negocio, independientes de cualquier tecnología externa.
* `usecases/`: Lógica de la aplicación y puertos primarios.
* `infrastructure/`: Implementaciones de adaptadores, repositorios de bases de datos, controladores web y parseo de archivos.

## Ejecución del Entorno de Medición (Laboratorio)

Para validar las reglas arquitectónicas (ArchUnit) y asegurar que no hay violaciones de dependencia:
```bash
./gradlew test
```
Para ejecutar el análisis estático de código (requiere una instancia local de SonarQube configurada y corriendo):

```bash
./gradlew sonar -Dsonar.token="TU_TOKEN_AQUI"
```
Licencia
Distribuido bajo la Licencia MIT (Ver archivo LICENSE para más detalles).
