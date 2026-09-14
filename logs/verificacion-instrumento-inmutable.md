# Verificación: ArchitectureTest.kt no fue modificado durante el experimento

**Propósito:** confirmar objetivamente, vía historial de git, que el archivo
de reglas arquitectónicas (`ArchitectureTest.kt`) permaneció sin cambios
durante todo el ciclo P-001–P-009 (V1–V4), tal como afirman repetidamente
los logs de cada iteración ("sin tocar ArchitectureTest.kt").

## Comando

```bash
git log --format="%h %ad %s" --date=short -- src/test/kotlin/com/example/sgva/ArchitectureTest.kt
```

## Resultado

Tres commits en todo el historial del repositorio, **todos anteriores a
P-001** (1 de septiembre de 2026):

| Commit | Fecha | Descripción |
|---|---|---|
| `f6a7f10` | 2026-07-28 | chore(setup): establecer entorno base del laboratorio y calibración V0 |
| `f2563ae` | 2026-07-28 (mismo día) | refactor(tests): actualizar reglas de ArchUnit y refinamiento metodológico |
| `da4f829` | 2026-08-27 | test(arquitectura): permite sufijo Port en interfaces de dominio |

Los tres caen dentro de la ventana de calibración de V0, con al menos 5 días
de margen (`da4f829`) hasta más de un mes de margen (`f6a7f10`/`f2563ae`)
antes de que existiera cualquier código de dominio generado por IA.

## Naturaleza de los cambios

- **`f6a7f10`:** versión inicial del archivo, con `ImportOption.DoNotIncludeTests()`
  (excluía clases de test del análisis).
- **`f2563ae`, mismo día:** amplía las reglas (aislamiento más estricto,
  convenciones de nomenclatura) y traduce los nombres de las pruebas al
  español; en este punto se elimina `DoNotIncludeTests()` — decisión que
  explica retroactivamente por qué, en P-002 y P-003, las clases de prueba
  quedaron sujetas a la regla de sufijo `UseCase` (una de las causas
  documentadas de las Restauraciones Arquitectónicas Autodetectadas).
- **`da4f829`:** dos correcciones técnicas menores (nombre de método de la
  API de ArchUnit; `allowEmptyShould(true)`), sin cambiar qué se permite o
  prohíbe.

Ningún cambio ajusta el criterio arquitectónico en respuesta a un resultado
observado — los tres ocurrieron antes de que hubiera ningún resultado que
observar.

## Conclusión

`ArchitectureTest.kt` quedó fijado el 27 de agosto de 2026, al menos cinco
días antes de P-001, tal como exige la Fase 5 del anteproyecto ("el
conjunto de reglas... será definido, programado y bloqueado manualmente por
el investigador antes de iniciar la Fase 3"). No hay ningún commit posterior
a P-001 que lo toque — ni del investigador ni generado por Claude Code CLI.
Esto confirma objetivamente, vía git, lo que cada log de iteración afirmaba
de forma narrativa.
