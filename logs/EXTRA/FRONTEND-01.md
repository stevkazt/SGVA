# FRONTEND-01

**Iteración:** Extra práctico — fuera del protocolo de prompting medido (no corresponde a ninguna V del estudio; el propio prompt de origen instruyó explícitamente no aplicar el formato P-00X para este trabajo)
**Herramienta:** Claude Code CLI
**Tipo:** Generación — frontend de prueba/demostración para SGVA (carga de archivos, dashboard de indicadores Módulos 1–2, vista de encuestas Módulo 3), consumiendo la API REST ya existente sin modificar el backend
**Fecha:** 2026-09-23 14:32
**Session ID:** 74d63143-e2ca-4c10-8c36-553b37381cbf
**Transcript:** /home/stevkazt/.claude/projects/-home-stevkazt-SGVA/74d63143-e2ca-4c10-8c36-553b37381cbf.jsonl

## Contexto previo
Backend cerrado hasta V4 (`logs/V4/P-009.md`): reportes ejecutivos en PDF
sobre indicadores + respuestas de encuesta, 145 pruebas en verde. El
investigador pidió un frontend práctico para poder probar y mostrar la
aplicación, aclarando de entrada que **no** es una versión experimental del
estudio y que no debía registrarse con el protocolo de prompting habitual.
Este archivo existe porque, en un prompt posterior de la misma sesión, el
investigador pidió registrar igualmente un log "similar a los que usamos
para cada versión del backend" — se documenta aquí, fuera de la secuencia
`V0`–`V4`/`P-00X`, para no mezclarlo con los datos medidos del estudio.

## Secuencia de interacción

### Prompt 1
"Vamos a construir un frontend para SGVA. Es un extra práctico para poder
probar y mostrar la aplicación — no es una versión experimental del estudio,
así que tienes libertad total de lenguaje, stack y estructura, mientras sea
razonable y mantenible. No apliques el protocolo de prompting ni el formato
de logs de P-00X: esto no se mide.

Antes de escribir código, revisa los controladores REST existentes en el
backend para identificar qué endpoints hay disponibles hoy: carga de
archivos CSV/Excel de estudiantes y docentes, consulta de indicadores de
calidad (Módulo 1 y 2), y lo relacionado con encuestas de percepción (Módulo
3) si ya hay algo expuesto.

Requisitos funcionales:
- Pantalla para subir un archivo CSV/Excel de estudiantes o docentes,
  mostrando el resultado de la carga (éxito o errores de validación).
- Dashboard con los indicadores de calidad ya calculados: evolución de
  matrícula, tasa de deserción, consolidado Saber Pro, distribución de
  formación docente, capacidad instalada. Usa gráficas simples donde tenga
  sentido.
- Si existe endpoint de encuestas, una vista con la ponderación Likert y/o
  la matriz radar por factor/estamento. Si no existe, omite esta parte y
  dilo en el resumen final.
- Manejo básico de errores de red/API (mensajes claros si el backend no
  responde).

Stack: elige tú el lenguaje, framework/librería que consideres más
apropiado para esto — no hay restricción, decide y documenta por qué.

Al terminar, dame un resumen breve con:
1. Lenguaje, stack y estructura de carpetas que usaste, y por qué.
2. Qué endpoints del backend consumiste (método, ruta, qué hace cada uno).
3. Qué quedó pendiente o no se pudo implementar (ej. endpoints que no
   existían).
4. Cómo correr el frontend en local (comandos exactos)."

**Resumen de la respuesta de Claude Code CLI:**

Leyó primero los seis controladores REST existentes (`EstudianteController`,
`DocenteController`, `IndicadorController`, `EncuestaController`,
`ReporteController`, `ManejadorGlobalDeErrores`) y los DTOs de dominio que
exponen, para consumir exactamente las formas reales de la API en vez de
suponerlas. Confirmó que el Módulo 3 (encuestas) ya estaba expuesto
(`/api/encuestas/ponderacion-likert`, `/api/encuestas/matriz-radar`), así
que esa vista sí se implementó.

Eligió **React 19 + TypeScript + Vite** en `frontend/` (proyecto
independiente del backend Kotlin), con `recharts` para las gráficas y
`react-router-dom` para la navegación entre tres páginas: carga de archivos,
dashboard de indicadores y encuestas. Sin framework de CSS (hoja de
variables propia). El dev server de Vite usa un proxy (`/api` →
`localhost:8080`) para evitar problemas de CORS en desarrollo sin tener que
tocar el backend.

Detalle de negocio detectado por lectura directa de los casos de uso (no
asumido): `tasaDesercion.valor` y los valores de `distribucion-formacion`
ya vienen como porcentaje `0..100` desde el backend, no como fracción — el
frontend no los multiplica de nuevo por 100. También separó, en el
indicador de Saber Pro, el promedio global (`periodo == "CONSOLIDADO"`) del
resto de promedios por cohorte, en vez de graficarlos mezclados en la misma
serie.

**Verificación reportada por la IA:**
- `npx tsc -b` → sin errores.
- `npx oxlint` → 0 errores, 2 warnings de estilo (`set-state-in-effect`,
  aceptados sin corregir).
- `npm run build` → `BUILD SUCCESSFUL` (bundle único, sin code-splitting).
- No pudo levantar el backend real: sin membresía en el grupo `docker`
  (`permission denied` al socket) y sin PostgreSQL local, por lo que no se
  probó contra la base de datos real en esta sesión.

### Prompt 2
"Save the last prompt on a log file similar to the ones we used for each
backend version. Then run the necessary tests to make the frontend work"

Petición de registrar este log (con el criterio de ubicación explicado en
"Contexto previo" arriba) y de ejecutar las pruebas necesarias para
confirmar que el frontend funciona. Ver el detalle de esa verificación en
`logs/EXTRA/FRONTEND-01-tests.md` (mismo prompt; se documenta aparte porque
corresponde a la ejecución de pruebas, no a generación de código nueva).

## Archivos generados o modificados

**Generados por Claude Code CLI (producción, `frontend/`):**
- `frontend/index.html`, `frontend/vite.config.ts`
- `frontend/src/main.tsx`, `frontend/src/App.tsx`, `frontend/src/index.css`
- `frontend/src/api/client.ts`, `frontend/src/api/types.ts`, `frontend/src/api/errores.ts`
- `frontend/src/components/ErrorBanner.tsx`, `frontend/src/components/StatTile.tsx`, `frontend/src/components/colorEstamento.ts`
- `frontend/src/components/charts/SerieUnicaChart.tsx`, `BarraSimpleChart.tsx`, `LikertChart.tsx`, `RadarFactorChart.tsx`
- `frontend/src/pages/UploadPage.tsx`, `DashboardPage.tsx`, `EncuestasPage.tsx`

**Backend:** ningún archivo modificado (solo lectura, para conocer los
endpoints y DTOs reales).

**Commits:** ninguno — protocolo de commits manuales del investigador
(instrucción general del proyecto, no específica de esta sesión).

## Resultado de medición
No aplica el set de métricas del estudio (ArchUnit/SonarQube son del
backend Kotlin y este trabajo no lo toca). Verificación propia del
frontend: ver `logs/EXTRA/FRONTEND-01-tests.md`.

## Requisito para ejecutar `./gradlew bootRun`
El contenedor de PostgreSQL debe estar iniciado antes de correr el backend
(`spring.datasource.url=jdbc:postgresql://localhost:5432/sgva`, ver
`src/main/resources/application.properties`). Si no lo está, `bootRun`
falla con `Connection refused` (HikariPool). Iniciarlo con:
```
podman start sgva-postgres
```
(o `docker start sgva-postgres` si se usa Docker en vez de Podman). El
nombre `sgva-postgres` corresponde al contenedor ya existente en este
equipo; si no existe, debe crearse primero, ej.:
```
podman run -d --name sgva-postgres -e POSTGRES_DB=sgva -e POSTGRES_USER=sgva -e POSTGRES_PASSWORD=sgva_dev_password -p 5432:5432 docker.io/library/postgres:16-alpine
```

## Notas del investigador
(pendiente — espacio reservado si el investigador quiere anotar algo aquí,
igual que en los logs `V0`–`V4`).
