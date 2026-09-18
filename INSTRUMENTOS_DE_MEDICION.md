# Instrumentos de Medición — Qué mide cada uno, exactamente

---

## 1. ArchUnit — estructura y dependencias entre paquetes

**Dónde vive la definición:** `src/test/kotlin/com/example/sgva/ArchitectureTest.kt`
— código propio, escrito antes de P-001 y nunca modificado después.

**Qué mide:** únicamente dependencias entre paquetes y convenciones de
nomenclatura. No evalúa complejidad ni estilo general de código — eso lo
cubre SonarQube (sección 2).

### Las 8 reglas exactas

| # | Regla | Qué verifica |
|---|---|---|
| 1 | El dominio no debe depender de capas externas ni frameworks | Ninguna clase en `..domain..` puede depender de `..usecases..`, `..infrastructure..`, `org.springframework..`, `jakarta..`, `javax..` |
| 2 | Los casos de uso no deben depender de infraestructura ni frameworks | Ninguna clase en `..usecases..` puede depender de `..infrastructure..`, `org.springframework..`, `jakarta..`, `javax..` |
| 3 | El dominio y los casos de uso no deben usar anotaciones de Spring | Prohíbe `@Component`, `@Service`, `@Autowired`, etc. en `domain` y `usecases` |
| 4 | Las clases de casos de uso deben finalizar en `UseCase` | Convención de nomenclatura en `..usecases..` |
| 5 | Las interfaces de puerto en el dominio deben finalizar en `Repository` o `Port` | Convención de nomenclatura para interfaces de salida |
| 6 | Las excepciones de dominio deben finalizar en `Exception` | Convención de nomenclatura para la jerarquía de excepciones |
| 7 | Los adaptadores REST en infraestructura deben finalizar en `Controller` | Convención de nomenclatura para clases `@RestController` |
| 8 | No se debe usar la salida estándar ni de error directa (`println`) | Regla predefinida `NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS` |

**"ArchUnit PASS 8/8"** significa, literalmente, que estas 8 reglas pasaron. Nada más está siendo medido por este instrumento.

---

## 2. SonarQube — calidad interna del código fuente

**Dónde vive la definición:** en el servidor de SonarQube, no en este
repositorio — un **Quality Profile** con reglas predefinidas por SonarSource
(no escritas por mí), agrupadas por lenguaje.

**Perfil activo confirmado:**

| Campo | Valor |
|---|---|
| Nombre | Sonar way |
| Lenguaje | Kotlin |
| Key | `796451cc-bb25-4244-9e09-79089e3cdbd5` |
| Reglas activas | 129 |
| Tipo | Integrado de SonarQube (`isBuiltIn: true`), es el perfil por defecto — no fue personalizado |
| Última actualización de reglas | 2026-08-27 |

Al ser el perfil integrado por defecto (no editado), las 129 reglas activas
son las que SonarSource define de fábrica para Kotlin — ninguna fue
agregada, quitada, ni configurada manualmente para este proyecto.

**Listado completo de las 129 reglas:** exportado en `reglas_sonar.json` (en el repositorio, junto a este documento).

**Reglas específicas que generaron issues reales durante el experimento:**

| Regla | Descripción | Iteraciones |
|---|---|---|
| "Do not hardcode version numbers" | Versión literal de una dependencia sin gestión de BOM | P-003, P-009 |
| `kotlin:S1874` | Uso de una API deprecada | P-006 |
| S108 | Bloques (incluido `catch`) vacíos sin comentario | Activa, no disparada durante el experimento |

**Qué reportan el Rating y el Debt Ratio:** son un agregado de las 129 reglas del perfil, no de una regla individual.

---

## 3. Diferencia clave

| | ArchUnit | SonarQube |
|---|---|---|
| Qué mide | Dependencias entre paquetes, nomenclatura | Calidad interna del código |
| Quién escribió las reglas | Yo, antes de P-001 | SonarSource — uso el perfil por defecto sin modificar |
| Cantidad de reglas | 8 | 129 |
| Resultado | PASS/FAIL binario por regla | Rating (A-E) + % de deuda técnica |
