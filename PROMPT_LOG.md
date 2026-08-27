# Registro de Prompts e Interacciones - Tesis SGVA

## Metodología de Registro
Cada interacción (generación inicial, solución de errores o refactorización) debe registrarse secuencialmente para medir la evolución de la deuda técnica y la calidad arquitectónica.

Dado que muchas interacciones involucran secuencias largas de ediciones (no un solo prompt), el detalle completo de cada una vive en `logs/{VERSION}/P-{ID}.md`. Esta tabla es el índice — resume cada entrada y enlaza al archivo completo.

---

## Tabla de Trazabilidad (Índice)

| ID | Iteración | Herramienta | Tipo | Resumen | ArchUnit | Deuda_SonarQube | Detalle |
|---|---|---|---|---|---|---|---|
<!-- INDEX_ROW_ANCHOR -->
| P-000 | V0 | Manual | Configuración | Configuración base del framework e instrumentación de pruebas | PASS | 3 Code Smells (Línea Base) | [logs/V0/P-000.md](logs/V0/P-000.md) |

---

## Cómo agregar una nueva entrada
Las entradas nuevas se generan automáticamente al cerrar cada sesión de Claude Code (ver `.claude/hooks/log-session.sh`). Solo debes:
1. Antes de abrir Claude Code para una nueva versión, fijar la variable de entorno: `export SGVA_LOG_VERSION=V1` (o V2, V3...).
2. Al terminar la sesión, revisar el archivo `logs/{VERSION}/P-{ID}.md` recién creado y completar los campos marcados `TODO` — especialmente la secuencia real de prompts (a partir del transcript enlazado) y los resultados de ArchUnit/SonarQube.
3. Completar también la fila correspondiente en la tabla de arriba (ya se inserta un borrador, solo faltan los valores `TODO`).
