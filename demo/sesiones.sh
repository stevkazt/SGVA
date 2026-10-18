#!/usr/bin/env bash
# Muestra o libera los entornos de la demo (uno por usuario).
# Uso: demo/sesiones.sh                 lista las sesiones
#      demo/sesiones.sh liberar <usuario>  borra los datos de un usuario (p. ej. johann)
#      demo/sesiones.sh liberar todas      borra los de todos los usuarios
set -euo pipefail
PUERTO="${PUERTO_PUERTA:-8081}"
BASE="http://127.0.0.1:$PUERTO/admin"
if [ "${1:-}" = liberar ]; then
  [ -n "${2:-}" ] || { echo "Falta el usuario (o «todas»)." >&2; exit 1; }
  curl -s -X POST "$BASE/liberar/$2"; echo
else
  curl -s "$BASE/sesiones"; echo
fi
