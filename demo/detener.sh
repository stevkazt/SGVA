#!/usr/bin/env bash
# Apaga la demo local: túnel, puerta y los dos backends.
# Uso: demo/detener.sh [quiet]
cd "$(dirname "$0")/.."
LOGS="$PWD/demo/logs"
for nombre in tunel puerta backend-demo backend-limpia; do
  if [ -f "$LOGS/$nombre.pid" ]; then
    kill "$(cat "$LOGS/$nombre.pid")" 2>/dev/null || true
    rm -f "$LOGS/$nombre.pid"
  fi
done
[ "${1:-}" = quiet ] || echo "Demo local detenida."
