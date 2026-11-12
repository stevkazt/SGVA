#!/usr/bin/env bash
# Arranque dentro de docker compose: la puerta de la demo, la plantilla de datos
# y el frontend con el panel de pruebas.
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p "$LOGS_DIR"

node demo/servidor.mjs > "$LOGS_DIR/puerta.log" 2>&1 &
for _ in $(seq 1 30); do
  curl -s -o /dev/null http://127.0.0.1:8081/admin/sesiones && break
  sleep 1
done

echo "Preparando los datos de ejemplo (la primera vez tarda unos 30 s)…"
curl -s -X POST http://127.0.0.1:8081/admin/preparar-plantilla > /dev/null

cd frontend
export VITE_PANEL_PRUEBAS=1 VITE_BACKEND_URL=http://127.0.0.1:8081
exec npx vite --host --port 5173
