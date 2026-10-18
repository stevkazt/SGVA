#!/usr/bin/env bash
# Levanta la puerta de la demo. Los backends y las bases de cada visitante los
# crea la puerta cuando hacen falta; aquí solo se deja lista la plantilla de datos.
# Requisitos: contenedor sgva-postgres en marcha, JAR construido (./gradlew bootJar).
# Uso: demo/iniciar-local.sh [directorio-de-logs]
set -euo pipefail
cd "$(dirname "$0")/.."

LOGS="${1:-demo/logs}"
mkdir -p "$LOGS"
export LOGS_DIR="$LOGS"
export SGVA_JAR="$PWD/$(ls build/libs/sgva-*-SNAPSHOT.jar | grep -v plain | head -1)"
PUERTO="${PUERTO_PUERTA:-8081}"

docker exec sgva-postgres psql -U sgva -d postgres -tc 'select 1' > /dev/null \
  || { echo "No se pudo hablar con el contenedor sgva-postgres." >&2; exit 1; }

nohup node demo/servidor.mjs > "$LOGS/puerta.log" 2>&1 &
echo $! > "$LOGS/puerta.pid"
for _ in $(seq 1 20); do
  curl -s -o /dev/null "http://127.0.0.1:$PUERTO/admin/sesiones" && break
  sleep 1
done

# La primera vez crea y siembra la plantilla (unos 30 s); después es inmediato.
curl -s -X POST "http://127.0.0.1:$PUERTO/admin/preparar-plantilla" > /dev/null
echo "Demo local lista en http://127.0.0.1:$PUERTO (logs en $LOGS)"
