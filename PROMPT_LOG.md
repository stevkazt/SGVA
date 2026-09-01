# Registro de Prompts e Interacciones - Tesis SGVA

## Metodología de Registro
Cada interacción (generación inicial, solución de errores o refactorización) debe registrarse secuencialmente para medir la evolución de la deuda técnica y la calidad arquitectónica.

Dado que muchas interacciones involucran secuencias largas de ediciones (no un solo prompt), el detalle completo de cada una vive en `logs/{VERSION}/P-{ID}.md`. Esta tabla es el índice — resume cada entrada y enlaza al archivo completo.

---

## Tabla de Trazabilidad (Índice)

| ID | Iteración | Herramienta | Tipo | Resumen | ArchUnit | Deuda_SonarQube | Detalle |
|---|---|---|---|---|---|---|---|
<!-- INDEX_ROW_ANCHOR -->
| P-002 | V1 | Claude Code CLI | Generación — casos de uso, puerto de persistencia y adaptador en memoria | Registrar/consultar Estudiante y Docente vía puerto único AcademicDataRepository, adaptador en memoria | PASS 8/8 | A — 0.0% (2 issues preexistentes, 0 nuevos) | [logs/V1/P-002.md](logs/V1/P-002.md) |
| P-001 | V1 | Claude Code CLI | Generación inicial | Modelado de entidades de dominio Estudiante y Docente, con validación de formato (AAAAS) y rango (Saber Pro) vía excepciones propias | PASS | A — 2 Code Smells | [logs/V1/P-001.md](logs/V1/P-001.md) |
| P-000 | V0 | Manual | Configuración | Configuración base del framework e instrumentación de pruebas | PASS | 3 Code Smells (Línea Base) | [logs/V0/P-000.md](logs/V0/P-000.md) |

---

## Cómo agregar una nueva entrada
Las entradas nuevas se generan automáticamente al cerrar cada sesión de Claude Code (ver `.claude/hooks/log-session.sh`). Solo debes:
1. Antes de abrir Claude Code para una nueva versión, fijar la variable de entorno: `export SGVA_LOG_VERSION=V1` (o V2, V3...).
2. Al terminar la sesión, revisar el archivo `logs/{VERSION}/P-{ID}.md` recién creado y completar los campos marcados `TODO` — especialmente la secuencia real de prompts (a partir del transcript enlazado) y los resultados de ArchUnit/SonarQube.
3. Completar también la fila correspondiente en la tabla de arriba (ya se inserta un borrador, solo faltan los valores `TODO`).
