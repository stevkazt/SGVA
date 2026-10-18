#!/usr/bin/env bash
# Levanta la demo en local: dos backends (datos demo y base limpia) y la puerta.
# Requisitos: contenedor sgva-postgres en marcha, JAR construido (./gradlew bootJar).
# Uso: demo/iniciar-local.sh [directorio-de-logs]
set -euo pipefail
cd "$(dirname "$0")/.."

LOGS="${1:-demo/logs}"
mkdir -p "$LOGS"
JAR="$(ls build/libs/sgva-*-SNAPSHOT.jar | grep -v plain | head -1)"
DB_DEMO=sgva_publica_demo
DB_LIMPIA=sgva_publica_limpia
CONTENEDOR=sgva-postgres

for base in "$DB_DEMO" "$DB_LIMPIA"; do
  docker exec "$CONTENEDOR" psql -U sgva -d postgres -tc "select 1 from pg_database where datname='$base'" | grep -q 1 \
    || docker exec "$CONTENEDOR" psql -U sgva -d postgres -c "create database $base"
done

esperar() { # url
  for _ in $(seq 1 90); do
    curl -s -o /dev/null "$1" && return 0
    sleep 2
  done
  echo "No respondió: $1" >&2; return 1
}

DB_NAME=$DB_DEMO   SERVER_PORT=8090 nohup java -jar "$JAR" > "$LOGS/backend-demo.log" 2>&1 &
echo $! > "$LOGS/backend-demo.pid"
DB_NAME=$DB_LIMPIA SERVER_PORT=8091 nohup java -jar "$JAR" > "$LOGS/backend-limpia.log" 2>&1 &
echo $! > "$LOGS/backend-limpia.pid"
esperar http://localhost:8090/api/ponderaciones
esperar http://localhost:8091/api/ponderaciones

# Siembra solo si la demo está vacía (no pisa lo que ya haya).
if [ "$(docker exec "$CONTENEDOR" psql -U sgva -d "$DB_DEMO" -tAc 'select count(*) from estudiantes')" = "0" ]; then
  node demo/sembrar.mjs http://localhost:8090
fi

nohup node demo/servidor.mjs > "$LOGS/puerta.log" 2>&1 &
echo $! > "$LOGS/puerta.pid"
sleep 1
echo "Demo local lista en http://127.0.0.1:${PUERTO_PUERTA:-8081} (logs en $LOGS)"
