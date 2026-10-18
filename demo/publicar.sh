#!/usr/bin/env bash
# Publica la demo: levanta el backend local, abre un túnel de Cloudflare y
# despliega el frontend en Cloudflare Pages protegido con contraseña.
#
# Requisitos: sesión de wrangler iniciada (npx wrangler login), cloudflared,
# Docker con el contenedor sgva-postgres y el JAR construido (./gradlew bootJar).
# La demo solo responde mientras este computador, el backend y el túnel estén encendidos.
#
# Uso: demo/publicar.sh        (el segundo uso solo renueva el túnel y vuelve a desplegar)
#      demo/detener.sh         (apaga todo)
set -euo pipefail
cd "$(dirname "$0")/.."
RAIZ="$PWD"
PROYECTO="${PAGES_PROYECTO:-sgva-demo}"
SECRETOS="$HOME/respaldos_sgva/.demo_secrets"
LOGS="$RAIZ/demo/logs"
mkdir -p "$LOGS"

# Contraseña y clave: se crean una vez y se guardan fuera del repositorio.
if [ ! -f "$SECRETOS" ]; then
  {
    echo "DEMO_PASSWORD=sgva-$(LC_ALL=C tr -dc 'a-z0-9' < /dev/urandom | head -c 8)"
    echo "DEMO_KEY=$(LC_ALL=C tr -dc 'a-f0-9' < /dev/urandom | head -c 40)"
  } > "$SECRETOS"
  chmod 600 "$SECRETOS"
  echo "Contraseña creada en $SECRETOS"
fi
set -a; source "$SECRETOS"; set +a

./demo/detener.sh quiet
export DEMO_KEY
./demo/iniciar-local.sh "$LOGS"

# Túnel rápido de Cloudflare hacia la puerta local (la URL cambia en cada ejecución).
nohup cloudflared tunnel --no-autoupdate --url "http://127.0.0.1:${PUERTO_PUERTA:-8081}" > "$LOGS/tunel.log" 2>&1 &
echo $! > "$LOGS/tunel.pid"
URL=""
for _ in $(seq 1 30); do
  URL="$(grep -o 'https://[a-z0-9-]*\.trycloudflare\.com' "$LOGS/tunel.log" | head -1 || true)"
  [ -n "$URL" ] && break
  sleep 1
done
[ -n "$URL" ] || { echo "No se obtuvo la URL del túnel; revisa $LOGS/tunel.log" >&2; exit 1; }
sleep 3
echo "Túnel: $URL"

cd frontend
npm run build:demo > "$LOGS/build.log" 2>&1 || { echo "Falló el build; revisa $LOGS/build.log" >&2; exit 1; }

npx --yes wrangler pages project list 2>/dev/null | grep -q " $PROYECTO " \
  || npx --yes wrangler pages project create "$PROYECTO" --production-branch main
for nombre in DEMO_PASSWORD DEMO_KEY; do
  printf '%s' "${!nombre}" | npx --yes wrangler pages secret put "$nombre" --project-name "$PROYECTO" > /dev/null
done
printf '%s' "$URL" | npx --yes wrangler pages secret put BACKEND_URL --project-name "$PROYECTO" > /dev/null
npx --yes wrangler pages deploy dist --project-name "$PROYECTO" --branch main --commit-dirty=true

echo
echo "Demo publicada: https://$PROYECTO.pages.dev"
echo "Contraseña: está en $SECRETOS (clave DEMO_PASSWORD)"
