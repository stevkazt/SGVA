# Datos consolidados de V5 (para la síntesis)

Fuente: logs de `logs/V5/`, transcripciones respaldadas en `~/respaldos_sgva/transcripts/`, `docs/sonar_resultados_V5.md` y los scripts de cierre. Un prompt por fila. Actualizar al cerrar cada prompt.

## Pruebas, arquitectura y fallos

| Prompt | Sesión | Tests totales | Nuevos | ArchUnit | Fallos transitorios | Commits de código (hash de Sonar en negrita) |
|---|---|---|---|---|---|---|
| P-010 dominio: características y ponderaciones | `e38c6237` (nueva) | 155 | 10 | 8/8 | 0 | `e5edacb`, **`2cd2851`** |
| P-011 programa, estamentos, encuesta por pregunta | `92a3e996` (reiniciada con `/clear`) | 190 | 35 | 8/8 | 2 (corridas de `./gradlew test` fallidas por esquema viejo en PostgreSQL local) | `29bbe09`, `fedf8d9`, `42f19b9`, **`9081235`** |
| P-012 evaluación documental | `c6f90551` (nueva; `/clear` solo al terminar) | 230 | 40 | 8/8 | 0 corridas fallidas | `d6d4076`, `ce45bbc`, `7a58da5`, **`14e30c5`** |
| P-013 robustez del parser | `4ff37a6a` (nueva; `/clear` solo al terminar) | 236 | 6 | 8/8 | 0 corridas fallidas | `5569cc7`, **`a2307c8`** |
| P-014 resultado de autoevaluación | `781f7d91` (nueva; `/clear` solo al terminar; dos prompts en la misma sesión) | 262 | 26 (20 + 6) | 8/8 | 1 (`BUILD FAILED`: `contextLoads` sin la propiedad de ponderaciones, corregido en la misma sesión) | `802d07f`, `25701af`, `b947c9e`, `0739b68`, **`2f17631`** |
| P-015 REST | `b24ca9db` | 269 | 7 | 8/8 | 1 (`BUILD FAILED`: aserción propia con valor esperado mal calculado, corregida en la misma sesión) | `dff05b9`, **`a2b1f00`** |
| P-016 reporte PDF | `57c6b914` | 276 | 7 | 8/8 | 0 | `4bcb4b7`, `30de462`, `9bde792`, **`93120db`** |
| P-017 validación modelo 2025 | `c54e0a66` | 279 | 3 | 8/8 | 0 | **`8623d35`** (solo test) |

## SonarQube (Community 26.7.0.124771, Sonar way Kotlin, 129 reglas)

| Versión | Líneas | Archivos | Deuda | Rating | Smells | Bugs | Vulnerab. | Deuda New Code | Issues nuevos |
|---|---|---|---|---|---|---|---|---|---|
| V4-final | 1559 | 61 | 0,1 % | A | 6 | 0 | 1 | — | línea base |
| P-010 | 1613 | 63 | 0,1 % | A | 7 | 0 | 1 | 0,0 % | 1 (`S1135`, falso positivo por la palabra «todo») |
| P-011 | 1879 | 69 | 0,1 % | A | 7 | 0 | 1 | 0,0 % | 0 |
| P-012 | 2133 | 76 | 0,0 % | A | 7 | 0 | 1 | 0,0 % | 0 |
| P-013 | 2150 | 76 | 0,0 % | A | 7 | 0 | 1 | 0,0 % | 0 |
| P-014 | 2331 | 79 | 0,1 % | A | 8 | 0 | 1 | 0,09208 % | 1 (`S6524`, Major, real: colección mutable en `CargadorConfiguracionPonderaciones.kt`) |
| P-015 | 2349 | 80 | 0,0 % | A | 9 | 0 | 1 | 0,0 % | 1 (`S1135`, Info, falso positivo conocido: «todo» en KDoc de `ResultadoAutoevaluacionController.kt`) |
| P-016 | 2393 | 80 | 0,0 % | A | 9 | 0 | 1 | 0,0 % | 0 |
| P-017 | 2393 | 80 | 0,0 % | A | 9 | 0 | 1 | 0,0 % | 0 |

## Alcance y comportamiento de la IA (observaciones)

(Por completar a medida que se cierren los prompts.)

## Condiciones del experimento declarables
- Consumo por prompt (contexto máximo / tokens de salida del `.jsonl`). La salida se da con dos métodos: A = suma por mensaje sin duplicados (id de mensaje único); B = suma de todas las líneas con `usage` (cuenta repetidos, tope superior). Contexto = input + cache leído + cache creado, máximo por línea.

| Prompt | Contexto máx. | Salida A | Salida B |
|---|---|---|---|
| P-010 | 111k | 23k | 41k |
| P-011 | 255k | 90k | 180k |
| P-012 | 206k | 65k | 123k |
| P-013 | 132k | 45k | 89k |
| P-014 | 218k | 87k | 182k |
| P-015 | 109k | 19k | 36k |
| P-016 | 136k | 36k | 71k |
| P-017 | 103k | 19k | 43k |

  (Cifras anteriores mezclaban métodos: P-010 «41k» era B y P-012 «~65k» era A.)
- `CLAUDE.md` cambió entre V4 y V5 (restricción de leer `docs/`, commit `4da7207`).
- El diseño de V5 se entregó completo antes de la ejecución (`DESIGN_SGVA.md`, commit `09e00ce`).
- El hook `SessionEnd` se corrigió el 9-oct-2026: generaba borradores erróneos con la versión V1 por defecto y en sesiones ajenas al experimento.
- Servidor de Sonar de V5: SonarQube Community 26.7.0.124771 en `localhost:9000`; V1–V4 se midieron por el investigador en otra máquina, con el mismo conjunto de reglas. El conjunto de 129 reglas es idéntico al de `reglas_sonar.json`.
