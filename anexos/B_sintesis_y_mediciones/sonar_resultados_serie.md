# Resultados de SonarQube — serie completa (V0 a V6)

Medición de toda la serie con el mismo servidor y perfil (SonarQube Community 26.7.0.124771, Sonar way Kotlin), en un proyecto aparte (`SGVA-serie`) y en orden cronológico de ejecución. Se corrió el 14-oct-2026 con `~/respaldos_sgva/bin/sonar-serie.sh` y `~/respaldos_sgva/bin/serie-run.sh` sobre el worktree `~/respaldos_sgva/wt-sonar`, sin tocar el repositorio. Cada fila se mide sobre el último commit de código del prompt. El orden de V6 es P-018, P-026, P-025 (el de ejecución real).

| Prompt | Versión | Hash | Líneas (ncloc) | Archivos | Deuda (ratio) | Rating | Code smells | Bugs | Vulnerab. | Issues nuevos |
|---|---|---|---|---|---|---|---|---|---|---|
| P-000 | V0 | `f2563ae` | 10 | 4 | 3,3 % | A | 2 | 0 | 1 | — (línea base) |
| P-001 | V1 | `2c3a212` | 94 | 11 | 0,4 % | A | 2 | 0 | 1 | 0 |
| P-002 | V1 | `31c205d` | 201 | 18 | 0,2 % | A | 2 | 0 | 1 | 0 |
| P-003 | V1 | `db31a61` | 512 | 30 | 0,2 % | A | 5 | 0 | 1 | 3 |
| P-004 | V1 | `0812005` | 673 | 36 | 0,1 % | A | 5 | 0 | 1 | 0 |
| P-005 | V1 | `c9c7b30` | 772 | 39 | 0,1 % | A | 5 | 0 | 1 | 0 |
| P-006 | V1 | `453950e` | 933 | 43 | 0,1 % | A | 5 | 0 | 1 | 0 |
| P-007 | V2 | `204c0ce` | 1199 | 53 | 0,1 % | A | 5 | 0 | 1 | 0 |
| P-008 | V3 | `02a54a2` | 1327 | 57 | 0,1 % | A | 5 | 0 | 1 | 0 |
| P-009 | V4 | `fed00dd` | 1559 | 61 | 0,1 % | A | 6 | 0 | 1 | 1 |
| P-010 | V5 | `2cd2851` | 1613 | 63 | 0,1 % | A | 7 | 0 | 1 | 1 |
| P-011 | V5 | `9081235` | 1879 | 69 | 0,1 % | A | 7 | 0 | 1 | 0 |
| P-012 | V5 | `14e30c5` | 2133 | 76 | 0,0 % | A | 7 | 0 | 1 | 0 |
| P-013 | V5 | `a2307c8` | 2150 | 76 | 0,0 % | A | 7 | 0 | 1 | 0 |
| P-014 | V5 | `2f17631` | 2331 | 79 | 0,1 % | A | 8 | 0 | 1 | 1 |
| P-015 | V5 | `a2b1f00` | 2349 | 80 | 0,0 % | A | 9 | 0 | 1 | 1 |
| P-016 | V5 | `93120db` | 2393 | 80 | 0,0 % | A | 9 | 0 | 1 | 0 |
| P-017 | V5 | `8623d35` | 2393 | 80 | 0,0 % | A | 9 | 0 | 1 | 0 |
| P-018 | V6 | `def59d9` | 3718 | 124 | 0,0 % | A | 9 | 0 | 1 | 0 |
| P-026 | V6 | `c3a1b14` | 3718 | 124 | 0,0 % | A | 9 | 0 | 1 | 0 |
| P-025 | V6 | `9598ea2` | 3875 | 126 | 0,1 % | A | 11 | 0 | 1 | 2 |

`Deuda (ratio)` es el ratio de deuda técnica de todo el código (Overall Code) en ese commit. Los issues nuevos son los que no existían en el análisis anterior de la serie.

## Issues nuevos por prompt

| Prompt | Regla, severidad, archivo y mensaje |
|---|---|
| P-000 | kotlin:S6474 MINOR (proyecto) — Dependencies are not verified because the "verification-metadata.xml" file is missing |
| P-000 | kotlin:S6629 MAJOR build.gradle.kts:22 — Group dependencies by their destination |
| P-000 | kotlin:S6624 MAJOR build.gradle.kts:29 — Do not hardcode version numbers |
| P-003 | kotlin:S6624 MAJOR build.gradle.kts 26 — Do not hardcode version numbers. |
| P-003 | kotlin:S6624 MAJOR build.gradle.kts 27 — Do not hardcode version numbers. |
| P-003 | kotlin:S6517 MAJOR IngestarArchivoUseCase.kt 11 — Make this interface functional or replace it with a function type. |
| P-009 | kotlin:S6624 MAJOR build.gradle.kts 30 — Do not hardcode version numbers. |
| P-010 | kotlin:S1135 INFO ReglasDeFormato.kt 42 — Complete the task associated to this TODO comment. |
| P-014 | kotlin:S6524 MAJOR CargadorConfiguracionPonderaciones.kt 65 — Make this collection immutable. |
| P-015 | kotlin:S1135 INFO ResultadoAutoevaluacionController.kt 13 — Complete the task associated to this TODO comment. |
| P-025 | kotlin:S107 MAJOR ConfiguracionCasosDeUso.kt 190 — This function has 9 parameters, which is greater than the 7 authorized. |
| P-025 | kotlin:S1192 CRITICAL GeneradorReportePdf.kt 123 — Define a constant instead of duplicating this literal "Sin datos" 3 times. |


## Comparación con los valores originales

- **V5 y V6:** líneas, archivos, ratio de deuda, rating, smells, bugs y vulnerabilidades coinciden exactamente con los de `sonar_resultados_V5.md` y `sonar_resultados_V6.md` (proyecto `SGVA`). La medición es reproducible.
- **V1 a V4:** los valores originales (en el índice de `PROMPT_LOG.md` y en los logs) coinciden en rating, número de smells y de issues nuevos: P-001 (2 smells), P-003 (3 issues nuevos), P-009 (1 issue nuevo), y el ratio de P-003 a P-009 (0,2 %, 0,1 %…). Diferencias de método: P-000 aparece como «3 Code Smells» en el índice, y aquí son 2 smells y 1 vulnerabilidad (3 issues en total); los ratios originales de P-001 (sin porcentaje, quedó como TODO en su log) y P-002 (0,0 %) eran de New Code, mientras que esta tabla usa el ratio de todo el código (P-001: 0,4 %; P-002: 0,2 %).
- El índice de `PROMPT_LOG.md` no tiene fila para P-008 (V3), aunque su log existe.

## Limitación: métricas de «New Code»

La deuda en New Code no se puede reconstruir a posteriori. Con la referencia «versión anterior», Sonar cuenta como nuevas las líneas cuya fecha de autoría en git es posterior al análisis anterior. Aquí los 21 análisis se hicieron en tres minutos, después de todos los commits, así que ninguna línea es «nueva» y el valor sale 0,0 % en todos. Los valores de New Code de V5 y V6 medidos en tiempo real (por ejemplo P-014 con 0,09208 % y P-025 con 0,55202 %) siguen siendo válidos y están en `sonar_resultados_V5.md` y `sonar_resultados_V6.md`. Para V1 a V4, la única fuente de New Code son los valores originales de los logs, medidos en la instalación anterior de Sonar. La atribución de issues nuevos por prompt de esta serie sí es válida (se calcula por diferencia entre análisis).

## Archivos de la corrida

`~/respaldos_sgva/serie-sonar/`: `resultados.txt` (salida completa), `sonar-P-0XX.out` (log de cada análisis de Gradle) y `state.json` (estado de issues del último análisis).
