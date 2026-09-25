# FRONTEND-01 — verificación

**Sesión:** misma que `logs/EXTRA/FRONTEND-01.md` (Session ID
`74d63143-e2ca-4c10-8c36-553b37381cbf`)
**Fecha:** 2026-09-23 14:51–15:00
**Disparador:** Prompt 2 de esa sesión — "Save the last prompt on a log
file similar to the ones we used for each backend version. Then run the
necessary tests to make the frontend work".

## Qué se verificó

En la sesión original (turno anterior) solo se había probado el frontend
contra un backend simulado (mock HTTP local), porque no había acceso a
Docker (`permission denied` en `/var/run/docker.sock`, usuario fuera del
grupo `docker`) ni a un PostgreSQL local. En este turno se encontró que
`podman` sí está disponible y funciona en modo rootless sin sudo, lo que
permitió una verificación real de punta a punta:

1. **Estático (sin backend):**
   - `npx tsc -b` → sin errores.
   - `npx oxlint` → 0 errores, 2 warnings de estilo preexistentes
     (`react(set-state-in-effect)` en `DashboardPage.tsx` y
     `EncuestasPage.tsx`, por resetear el estado de carga al inicio de un
     `useEffect`; no se corrigieron, es un patrón intencional y común para
     este caso).
   - `npm run build` → `BUILD SUCCESSFUL`.

2. **Integración real, backend + PostgreSQL reales (no simulados):**
   - Se encontró un contenedor `sgva-postgres` ya existente (creado hace
     11 días, presumiblemente del propio trabajo previo del investigador
     sobre el backend), detenido. Se inició con `podman start` — **no se
     recreó ni se le pasaron credenciales nuevas**, para no arriesgar datos
     existentes.
   - Se levantó el backend real con `./gradlew bootRun` contra ese
     Postgres (puerto 8080). Arrancó en ~9 s sin errores
     (`Started SgvaApplicationKt`).
   - Se levantó `npm run dev` (puerto 5173) apuntando al backend real vía
     el proxy de Vite (sin mocks).
   - Se confirmó que la base estaba vacía antes de empezar (`GET
     /api/estudiantes|docentes|encuestas` → `[]` los tres).
   - **Carga de archivos** (mismo flujo que usa `UploadPage`, con los
     fixtures reales del backend en `src/test/resources/fixtures/`):
     - `POST /api/estudiantes/archivos` con `datos_estudiantes_test.csv` →
       `{"totalRegistrosLeidos":10,"registrados":10,"omitidos":[]}`.
     - `POST /api/docentes/archivos` con `datos_docentes_test.csv` →
       `{"totalRegistrosLeidos":4,"registrados":4,"omitidos":[]}`.
     - `POST /api/encuestas/archivos` con `datos_encuestas_test.csv` →
       `{"totalRegistrosLeidos":5,"registrados":5,"omitidos":[]}`.
   - **Dashboard** (mismo flujo que usa `DashboardPage`), contra los datos
     recién cargados:
     - `evolucion-matricula` → serie continua de periodos con ceros y
       valores reales (confirma que `SerieUnicaChart` recibe la forma
       esperada).
     - `saber-pro` → `[{"periodo":"CONSOLIDADO","valor":199.6}, ...]` +
       promedios por cohorte — confirma que separar el promedio global
       (`periodo == "CONSOLIDADO"`) del resto en `DashboardPage.tsx` es
       necesario y correcto (si no, el promedio global aparecería mezclado
       como una barra más entre las cohortes).
     - `distribucion-formacion` → `25/50/25` sobre
       `ESPECIALIZACION/MAESTRIA/DOCTORADO` — confirma que ya viene en
       porcentaje `0..100` (no fracción) y que el parseo
       `nombre.split(' - ')[1]` extrae bien el nivel.
     - `capacidad-instalada` → `1.45` (ratio, sin filtro).
     - `tasa-desercion?cohorte=20231` → `50.0` (1 desertor de 2 en esa
       cohorte, coherente con el fixture) — confirma que ya viene en
       porcentaje `0..100`, igual que `distribucion-formacion`.
     - `tasa-desercion` sin `cohorte` → `400` (validado; la UI nunca
       dispara esta llamada porque el campo cohorte es obligatorio antes
       de consultar, ver `DashboardPage.tsx`).
   - **Encuestas** (mismo flujo que usa `EncuestasPage`):
     - `ponderacion-likert` y `matriz-radar` devuelven las formas
       exactas que consumen `LikertChart` y `RadarFactorChart`, incluidos
       estamentos con datos parciales (ej. `EGRESADO` solo tiene
       "Plan de Estudios", no "Infraestructura") — confirma que el
       `promediosPorFactor` parcial no rompe el radar.
   - **Manejo de errores de red/API** (requisito funcional explícito):
     - Error de negocio (`DatosInsuficientesException`, HTTP 422) con
       cuerpo `{"mensaje": "..."}` → se confirmó que `ErrorDeApi` en
       `client.ts` expone ese mensaje tal cual al usuario.
     - Backend caído (se detuvo el proceso Java real, no solo el wrapper
       de Gradle) con Vite todavía arriba → la petición a través del
       proxy responde `502` con cuerpo vacío; `client.ts` cae al mensaje
       genérico `"Error inesperado del servidor (HTTP 502)."` en vez de
       fallar silenciosamente o romper el render. En producción (frontend
       y backend en orígenes distintos, sin proxy de por medio) el mismo
       caso dispara `ErrorDeRed` con el mensaje
       "No se pudo conectar con el backend...", ya cubierto por el
       `catch` de `fetch()` en `client.ts`.

3. **Limpieza tras la verificación:**
   - Se truncaron las tablas `estudiantes`, `docentes` y
     `respuestas_encuesta` (`TRUNCATE TABLE ...`) para dejar la base como
     estaba antes de esta sesión (vacía).
   - Se detuvo el backend real (proceso Java, no solo el wrapper de
     Gradle) y el contenedor `sgva-postgres` (`podman stop`), devolviendo
     ambos al estado en que se encontraron (detenidos). El daemon de
     Gradle no se detuvo — es un proceso de caché de builds que gestiona
     Gradle mismo, no algo iniciado específicamente para esta prueba.

## Resultado

Todo lo verificable sin interacción manual de navegador pasó: tipado,
build, y los tres flujos funcionales (carga de archivos, dashboard,
encuestas) contra un backend y una base de datos reales, incluidos los
casos de error. Sigue sin haber verificación visual en un navegador real
(sin extensión de Chrome conectada en esta sesión) — las capturas de
pantalla o una revisión manual del investigador abriendo
`http://localhost:5173` con el backend real corriendo (`./gradlew bootRun`
+ `npm run dev`) quedan pendientes si se quiere cerrar ese último punto.
