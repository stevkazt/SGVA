#!/usr/bin/env bash
# Actualiza solo el frontend (y el documento) en Cloudflare Pages, reutilizando
# el túnel y los secretos de la última publicación. Tarda unos segundos.
# Si la demo aún no se ha publicado o el túnel cambió, usar demo/publicar.sh.
#
# Uso: demo/desplegar.sh
set -euo pipefail
cd "$(dirname "$0")/../frontend"
PROYECTO="${PAGES_PROYECTO:-sgva-demo}"
LOGS="../demo/logs"
mkdir -p "$LOGS"

npx --yes wrangler pages project list 2>/dev/null | grep -q " $PROYECTO " \
  || { echo "El proyecto $PROYECTO no existe todavía: ejecuta demo/publicar.sh primero." >&2; exit 1; }

npm run build:demo > "$LOGS/build.log" 2>&1 || { echo "Falló el build; revisa demo/logs/build.log" >&2; exit 1; }
npx --yes wrangler pages deploy dist --project-name "$PROYECTO" --branch main --commit-dirty=true
echo "Actualizado: https://$PROYECTO.pages.dev"
