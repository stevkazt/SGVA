# Datos consolidados de V6 (para la síntesis)

Mismo formato que `sintesis_V5_datos.md`. Fuente: logs de `logs/V6/`, transcripciones en `~/respaldos_sgva/transcripts/` (`V6_P-0XX_...`), resultados de Sonar de `docs/sonar_resultados_V6.md` y los scripts de cierre.

| Prompt | Sesión | Tests totales | Nuevos | ArchUnit | Fallos transitorios | Commits de código (hash de Sonar en negrita) |
|---|---|---|---|---|---|---|
| P-018 ciclo, fases y actividades (alcance real: Módulo 5 casi completo) | `a509cfc4` | 376 | 97 | 8/8 | 1 (`BUILD FAILED`, 4 pruebas) | `27fc8bf` (domain), `1671d9d` (usecases), `3d80a7d` (infrastructure), **`def59d9`** (test) |
| P-019 grupos evaluadores y preevaluación | no ejecutado | — | — | — | — | cubierto por P-018 |
| P-020 participación de actores | no ejecutado | — | — | — | — | cubierto por P-018 |
| P-021 plan de mejoramiento | no ejecutado | — | — | — | — | cubierto por P-018 |
| P-022 avance y comparación entre ciclos | no ejecutado | — | — | — | — | cubierto por P-018 |
| P-023 carga masiva | no ejecutado | — | — | — | — | cubierto por P-018 |
| P-024 REST | no ejecutado | — | — | — | — | cubierto por P-018 |
| P-025 reporte PDF | `26621f9a` | 394 | 14 | 8/8 | 1 (`BUILD FAILED`, prueba sin el parámetro nuevo) | `90bcb36` (domain), `dd32438` (usecases), `19aeab0` (infrastructure), **`9598ea2`** (test) |
| P-026 validación contra el proceso 2025 | `c371f98d` | 380 | 4 | 8/8 | 0 | **`c3a1b14`** (solo test) |

Línea base de partida (fin de V5, P-017, sesión `c54e0a66`): 279 tests, ArchUnit 8/8; Sonar A, 0,0 % de deuda, 2393 líneas, 80 archivos, 9 smells, 0 bugs, 1 vulnerabilidad (`sonar_resultados_V5.md`, commit `8623d35`).

## SonarQube (mismo servidor y perfil que V5)

| Versión | Líneas | Archivos | Deuda | Rating | Smells | Bugs | Vulnerab. | Deuda New Code | Issues nuevos |
|---|---|---|---|---|---|---|---|---|---|
| P-018 | 3718 | 124 | 0,0 % | A | 9 | 0 | 1 | 0,0 % | 0 |
| P-026 | 3718 | 124 | 0,0 % | A | 9 | 0 | 1 | 0,0 % | 0 |
| P-025 | 3875 | 126 | 0,1 % | A | 11 | 0 | 1 | 0,55202 % | 2 |

## Consumo por prompt (métodos A y B como en V5)

| Prompt | Contexto máx. | Salida A | Salida B |
|---|---|---|---|
| P-018 | 306k | 150k | 281k |
| P-026 | 114k | 34k | 67k |
| P-025 | 220k | 62k | 114k |

## Alcance y comportamiento de la IA (observaciones)
- **P-018 desbordó el alcance.** El prompt pedía ciclo, fases y actividades; la IA implementó casi todo el Módulo 5 (equivale a P-019 a P-025, sin la sección del PDF ni P-026). Su log lo declara: tomó `DESIGN_SGVA.md` como la especificación completa y el prompt como «ilustración», apoyándose en el rol de `CLAUDE.md`. Ni el prompt, ni el diseño, ni `CLAUDE.md` definen la jerarquía entre ellos.
- Contraste con V5 (observación, no control): con el diseño de V5 también completo, P-010 y P-011 se ciñeron a su prompt. El diseño de V6 es mayor (401 líneas contra 290; 7 casos de uso contra 5), con la misma estructura y redacción. Prompts idénticos en forma («Según `DESIGN_SGVA.md`…»); `CLAUDE.md`, hook y protocolo sin cambios.
- Antes de decidir el alcance solo leyó el árbol de archivos, `DESIGN_SGVA.md` y tres archivos de código; no leyó logs, prompts, la guía ni memoria. Su anuncio de «delegar en un fork» no se ejecutó (0 llamadas a `Agent`).
- Decisión del investigador (12-oct-2026): cerrar la sesión como P-018 y registrar el comportamiento como dato, sin repetir el prompt (en desarrollo real se seguiría adelante al funcionar el resultado). P-019 a P-025 no se ejecutan como prompts separados.
- La IA narra en inglés en todas las sesiones (V1 a V6); no hay instrucción de idioma en `CLAUDE.md` ni en `settings.json`.
- P-019 a P-024 no se ejecutaron como prompts: su contenido lo cubrió P-018. P-025 (sección del PDF) sí se ejecutó, después de P-026 y fuera del orden previsto: P-026 se lanzó suponiendo que el PDF ya estaba hecho. El investigador añadió al prompt, al pegarlo, el criterio de comparación (ciclo inmediatamente anterior del mismo programa); `PROMPTS_V6.md` no cambió. Sonar de P-026 usó P-018 como referencia de New Code.
- P-026 reprodujo el oráculo sin diferencias, pero su log dice 26 consolidadas y 6 porcentajes y semáforos; el fixture tiene 25 consolidadas y 7 actores. Las 6 actores efectivamente comparados probablemente se deben a que `Graduados` aparece dos veces y la prueba lee los esperados en un mapa por actor (inferencia, no verificada en el código).
- Hook: el proceso `claude` de Debby (pid 104) conserva `SGVA_LOG_VERSION=V5` en su entorno; el `/clear` no lo reinicia. Por eso el hook creó borradores con iteración V5 (`logs/V5/P-018.md`, `P-019.md` y `P-027.md`), borrados y con su fila revertida antes de cada commit. Quien vuelva a lanzar Debby debe abrir un proceso nuevo, no hacer `/clear`.

## Condiciones declarables de V6
- El diseño de V6 se entregó completo antes de la ejecución (`docs/DESIGN_SGVA_con_V6.md`, aplicado en un commit `docs(design)` tras cerrar V5).
- Fixtures de V6 preparados por el investigador con códigos y valores numéricos (`docs/fixtures_v6/`).
- Notas del investigador diferidas al final de V6.

## Hallazgo de dominio: regla de mezcla de fuentes (14-oct-2026)
- Hipótesis inicial del investigador y su director: algunas características serían 100 % percepción (encuesta), otras solo datos medibles y otras solo consenso de un comité. Fue una inferencia al revisar archivos del modelo, sin respaldo documental.
- Verificación contra la fuente (detalle y citas en `docs/hallazgo_regla_mezcla_fuentes.md`): el modelo institucional usa una sola regla, 40 % encuesta y 60 % evaluación documental y estadística, con `N/A` en la encuesta cuando no aplica (entonces vale la nota documental completa). Ninguna característica es solo encuesta; 31 de 48 tienen encuesta y 17 solo nota documental; lo medible y el juicio de comité se registran juntos en la nota documental y estadística.
- Consecuencia: V5 y V6 implementan fielmente el modelo (0 diferencias contra el global 4,2947 de 2025). La clasificación por tipo de fuente no existe en la fuente usada; se declara como limitación de dominio y trabajo futuro, no como defecto de la implementación.
- Pendiente fuera del sistema: confirmar con el responsable institucional del proceso cómo se evalúan las 17 características sin encuesta.

## Extras fuera del protocolo (no medidos)
- FRONTEND-02 (resultado de autoevaluación y carga de archivos de V5) y FRONTEND-03 (ciclos, detalle, proyectos, comparación y carga de V6): hechos el 14-oct-2026 por la sesión administrativa, sin prompt P-0XX y sin tocar `src/`. Commits `2bb14cf`, `5ab63d1`, `0ec0608`, `7553060`, `44c90ae`, `4674a17`; logs en `logs/EXTRA/` (`7ff2950`). Alcance inferido de `DESIGN_SGVA.md` y de los controladores.
- Prueba en Chrome contra el backend con una base aparte cargada con los fixtures de `modelo2025` y `proceso2025`: global 4,29, avance 25 %, semáforo, 7 grupos, 75 preevaluaciones, consolidación de 25 características y PDF con HTTP 200. Sin pruebas automáticas de frontend.
