#!/usr/bin/env bash
# Apaga la demo local: túnel, puerta y los backends de los visitantes.
# Los datos de cada visitante se conservan en PostgreSQL (se liberan con demo/sesiones.sh).
# Uso: demo/detener.sh [quiet]
cd "$(dirname "$0")/.."
LOGS="$PWD/demo/logs"
for nombre in tunel puerta; do
  if [ -f "$LOGS/$nombre.pid" ]; then
    kill "$(cat "$LOGS/$nombre.pid")" 2>/dev/null || true
    rm -f "$LOGS/$nombre.pid"
  fi
done
sleep 2 # la puerta apaga sus backends al recibir la señal
if [ -f "$LOGS/hijos.pid" ]; then
  while read -r pid; do kill "$pid" 2>/dev/null || true; done < "$LOGS/hijos.pid"
  rm -f "$LOGS/hijos.pid"
fi
[ "${1:-}" = quiet ] || echo "Demo local detenida."
