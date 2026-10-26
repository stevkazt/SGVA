# SonarQube original de V1–V4: lo que consta en la base restaurada

Fuente: volúmenes del contenedor `sonarqube` del equipo original (Fedora, Podman), exportados el 15-oct-2026 y restaurados en una copia de trabajo (`~/respaldos_sgva/sq-restore/`) con la imagen por digest. Respuestas de la API guardadas en `~/respaldos_sgva/sonar-original/` (JSON). Los `.tar` originales no se modificaron.

## 1. Servidor y perfil (verificado en la API del servidor restaurado)

| Dato | Valor |
|---|---|
| Versión de SonarQube | 26.8.0.126808 (`/api/server/version`) |
| Imagen | `sonarqube:community`, digest `sha256:9026624a61cd25542a402a9e7213dd7dbb39724ac9597e331e6b85362558c079`, construida el 21-ago-2026, descargada el 27-ago-2026 y nunca vuelta a descargar (eventos de Podman) |
| Plugin de Kotlin | Kotlin Code Quality and Security 3.7.0 (build 9514) |
| Perfil de Kotlin del proyecto | «Sonar way», integrado (`isBuiltIn`), sin herencia ni copia, 129 reglas activas, `rulesUpdatedAt` 27-ago-2026 08:16 UTC (arranque inicial del servidor) |
| Quality gate | «Sonar way» (por defecto) |
| Período de New Code | `PREVIOUS_VERSION` (heredado) |
| Base de datos | H2 embebida; solo un usuario (`admin`) |

El perfil no se personalizó en ningún momento: no hay copia ni perfil derivado, y la fecha de actualización de reglas es la del arranque inicial. Es el mismo perfil (nombre y número de reglas) que el de la re-medición de la serie (Sonar way Kotlin, 129 reglas, servidor 26.7.0.124771). La versión del servidor sí difiere (26.8.0.126808 original frente a 26.7.0.124771 de la re-medición).

## 2. Análisis conservados (solo 5)

| Fecha (UTC) | Revisión registrada | Corresponde a | ncloc | Archivos | Ratio deuda | Smells | Vuln. | Bugs | Ratio deuda New Code |
|---|---|---|---|---|---|---|---|---|---|
| 27-ago 08:40 | 801b16a | P-000 | 10 | 4 | 3,3 % | 2 | 1 | 0 | — |
| 01-sep 10:37 | 4e32d17 | P-002 | 201 | 18 | 0,2 % | 2 | 1 | 0 | 0,0 % |
| 09-sep 03:48 | 7b1b392 | P-006 | 933 | 43 | 0,1 % | 5 | 1 | 0 | 0,01806 % |
| 12-sep 15:12 | dd0c2bb | P-008 | 1327 | 57 | 0,1 % | 5 | 1 | 0 | 0,01266 % |
| 13-sep 01:13 | fed00dd | P-009 | 1559 | 61 | 0,1 % | 6 | 1 | 0 | 0,01076 % |

La columna «Corresponde a» se asignó por coincidencia exacta de ncloc, archivos, ratio y smells con la serie re-medida (`sonar_resultados_serie.md`), no porque la base registre el prompt. Las 5 filas coinciden exactamente (rating A en todas). Las revisiones registradas no coinciden con los hashes de la serie salvo P-009 (`fed00dd`): el análisis original se hizo sobre el árbol de trabajo en ese momento.

## 3. Lo que no consta

- **No existen los análisis de P-001, P-003, P-004, P-005 ni P-007.** Los valores de esos prompts solo están en los logs y en el índice de `PROMPT_LOG.md`. La configuración del servidor tiene la limpieza automática por defecto (`hoursBeforeKeepingOnlyOneSnapshotByDay` = 24, `weeksBeforeKeepingOnlyOneSnapshotByWeek` = 4), compatible con la desaparición de los análisis intermedios de un mismo día. No se verificó que esa sea la causa ni por qué se conservó uno u otro de cada día.
- Issues: la base conserva 8 (7 abiertos: 4 de S6624, 1 de S6517, 1 de S6474 y 1 de S6629; 1 cerrado: S1874). No se comparó aún su detalle con el de la serie.

## 4. Consecuencias para la tesis

1. **Perfil:** dejar de decir «no está documentado»: consta en el servidor original que el perfil de Kotlin era «Sonar way» integrado con 129 reglas, sin modificar (lo que el investigador ya afirmaba).
2. **Versión:** V1–V4 se midieron con SonarQube 26.8.0.126808 y la re-medición de la serie con 26.7.0.124771. Mencionar la diferencia; las cifras coinciden donde existe análisis original.
3. **New Code de V1–V4:** hay valores originales para P-002, P-006, P-008 y P-009 (tabla de arriba). Los de P-006, P-008 y P-009 (0,018 %, 0,013 %, 0,011 %) no estaban en los logs, que usan el ratio de código total.
4. Los valores de los prompts sin análisis conservado siguen respaldados por los logs, no por Sonar.
