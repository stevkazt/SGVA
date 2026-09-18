# Síntesis V3 — Estrés Arquitectónico: Sustitución de Persistencia

**Proyecto:** SGVA (Sistema de Gestión Visual de Datos para la Autoevaluación Académica)
**Periodo:** P-008
**Estado:** Cerrado — 136 pruebas / 0 fallos (contra PostgreSQL real), ArchUnit 8/8, SonarQube Rating A (0.1% deuda técnica)

---

## 1. Alcance implementado

| Capa | Contenido | Prompts |
|---|---|---|
| Dominio | Sin cambios — `AcademicDataRepository` y las tres entidades de dominio permanecen idénticas | P-008 |
| Casos de uso | Sin cambios | P-008 |
| Infraestructura | Tres entidades JPA (`internal`, distintas de las entidades de dominio); `MapeadorPersistencia`; `AcademicDataRepositoryJpa`; retiro de `AcademicDataRepositoryEnMemoria` | P-008 |

Con el cierre de P-008 se satisface la definición de V3 dada en la Fase 4 del anteproyecto: sustitución del almacenamiento local por un motor de base de datos relacional, verificando que la IA implemente los nuevos adaptadores sin alterar las interfaces y entidades de la capa de Dominio.

---

## 2. Hallazgos metodológicos acumulados

### 2.1 Restauraciones Arquitectónicas Autodetectadas (0 eventos)

Ninguna en esta sesión. Cuarta iteración consecutiva sin eventos (P-005 a P-008).

### 2.2 Decisión de diseño no trivial: columna `creadoEn` exclusiva de JPA

`AcademicDataRepository` documentaba desde P-002 que `listar*()` devuelve resultados "en orden de inserción" — contrato que el `LinkedHashMap` del adaptador en memoria garantizaba, pero que `JpaRepository.findAll()` no garantiza en PostgreSQL. Se añadió un timestamp de inserción solo en la capa JPA (nunca visible al dominio) para preservar el contrato sin tocar el puerto ni el dominio, tal como exigía el prompt. Ningún test unitario existente lo habría detectado, porque esos tests usan el doble de prueba, no el adaptador real.

### 2.3 Clasificación de fase no proactiva

Claude Code CLI implementó y reportó este trabajo bajo V2 (continuación de P-007), pese a que el repositorio ya contenía, en sesiones anteriores del mismo hilo, notas anticipando explícitamente que la sustitución de persistencia correspondía a V3 (`SINTESIS_V1.md` §4, nota del investigador en `logs/V1/P-006.md`). El investigador corrigió la clasificación explícitamente. Tener la información disponible en el repositorio no bastó para que la IA la aplicara sin que se le pidiera.

### 2.4 Continuidad de sesión cruzando el límite de fase

P-007 (V2) y P-008 (V3) comparten el mismo Session ID — la sesión no se reinició entre fases. El contexto completo de V2 seguía presente al ejecutar el trabajo de V3, posible factor en el hallazgo 2.3.

### 2.5 Cambio de naturaleza de la suite de pruebas

A partir de esta sesión, `./gradlew test` deja de ser hermético: depende de una instancia real de PostgreSQL en ejecución, provista por el investigador antes de la sesión, no por la IA.

### 2.6 Cero deuda técnica nueva, explicada estructuralmente

Pese a introducir una superficie de dependencias externas comparable a P-003, no se generó deuda técnica nueva. Verificado mediante `git diff build.gradle.kts`: las dependencias nuevas se declaran sin versión literal explícita, gestionadas por `io.spring.dependency-management` contra el BOM de Spring Boot — a diferencia de FastCSV/Apache POI en P-003, que sí requirieron versiones inline. La divergencia es una propiedad estructural de qué cae bajo gestión de Spring, no una diferencia de comportamiento de la IA.

---

## 3. Evolución de la deuda técnica (SonarQube, Overall Code)

| Iteración | Rating | Debt Ratio | Issues nuevos |
|---|---|---|---|
| P-008 | A | 0.1% | 0 |

Sin cambio respecto al cierre de V2. Primer escenario de estrés estructural sin degradación medible.

---

## 4. Preguntas abiertas para V4

- ¿Se repite en V4 la falla de clasificación de fase observada en P-008, o fue un evento aislado ligado a la continuidad de sesión con P-007?
- La pregunta de V1 sobre si la decisión de no usar DTOs de solicitud/respuesta se sostiene bajo cambios de infraestructura profundos queda parcialmente respondida: la sustitución de persistencia no forzó ningún cambio en los controladores REST existentes, que siguen serializando entidades de dominio directamente. Falta ver si V4 (integración de PDF, un cambio de naturaleza distinta —salida, no persistencia—) sostiene lo mismo.
- Se mantienen ya cuatro iteraciones consecutivas (P-005 a P-008) sin restauraciones arquitectónicas autodetectadas. Vigilar si V4 rompe esta racha, dado que introduce una superficie de código nueva comparable a P-003 (la única iteración de V1 con deuda técnica no trivial).
