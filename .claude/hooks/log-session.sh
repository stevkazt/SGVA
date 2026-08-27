#!/bin/bash
# .claude/hooks/log-session.sh
# Se ejecuta en el evento SessionEnd de Claude Code (una vez por sesión completa).
# Genera un borrador de entrada P-{ID}.md en logs/{VERSION}/ y lo enlaza en el
# índice PROMPT_LOG.md. No completa resultados de ArchUnit/SonarQube ni el
# análisis del investigador — esos campos quedan como TODO para llenar a mano.

set -euo pipefail
cd "$(dirname "$0")/../.."   # raíz del proyecto (donde vive PROMPT_LOG.md)

INPUT=$(cat)
SESSION_ID=$(echo "$INPUT" | jq -r '.session_id // "desconocido"')
TRANSCRIPT_PATH=$(echo "$INPUT" | jq -r '.transcript_path // "desconocido"')
TIMESTAMP=$(date '+%Y-%m-%d %H:%M')

# Versión del experimento (V1, V2, V3, V4...). Configúrala así antes de iniciar
# Claude Code en cada fase: export SGVA_LOG_VERSION=V2
VERSION="${SGVA_LOG_VERSION:-V1}"

LOG_DIR="logs/${VERSION}"
mkdir -p "$LOG_DIR"

# Calcula el siguiente ID de forma global (no por versión), continuando la
# numeración P-000, P-001, ... que ya usa el proyecto.
LAST_NUM=$(find logs -type f -name 'P-*.md' 2>/dev/null \
  | sed -E 's/.*P-([0-9]+)\.md/\1/' \
  | sort -n | tail -1)
if [ -z "$LAST_NUM" ]; then
  NEXT_NUM="000"
else
  NEXT_NUM=$(printf "%03d" $((10#$LAST_NUM + 1)))
fi
ID="P-${NEXT_NUM}"
FILE="${LOG_DIR}/${ID}.md"

cat > "$FILE" <<EOF
# ${ID}

**Iteración:** ${VERSION}
**Herramienta:** Claude Code CLI
**Tipo:** TODO (Generación inicial / Corrección de error / Refactorización)
**Fecha:** ${TIMESTAMP}
**Session ID:** ${SESSION_ID}
**Transcript:** ${TRANSCRIPT_PATH}

## Contexto previo
TODO — completar manualmente.

## Secuencia de interacción
TODO — revisar el transcript indicado arriba y transcribir cada prompt enviado
como una subsección "### Prompt N", con un resumen de la respuesta/acción de
la IA. No se autocompleta aquí para no arriesgar una transcripción incorrecta
del historial real.

## Archivos generados o modificados
TODO

## Resultado de medición
- **ArchUnit:** TODO (PASS / FAIL)
- **SonarQube:** TODO (deuda técnica, code smells nuevos)

## Notas del investigador
TODO
EOF

# Inserta una fila borrador en el índice, justo debajo del ancla de la tabla.
INDEX="PROMPT_LOG.md"
if [ -f "$INDEX" ] && grep -q "<!-- INDEX_ROW_ANCHOR -->" "$INDEX"; then
  ROW="| ${ID} | ${VERSION} | Claude Code CLI | TODO | TODO — ver ${FILE} | TODO | TODO | [${FILE}](${FILE}) |"
  # Inserta la fila inmediatamente después de la línea ancla, sin borrar el resto.
  awk -v row="$ROW" '{print} /<!-- INDEX_ROW_ANCHOR -->/ {print row}' "$INDEX" > "${INDEX}.tmp" \
    && mv "${INDEX}.tmp" "$INDEX"
fi

echo "Borrador de log creado: ${FILE}" >&2
exit 0
