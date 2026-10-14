# FRONTEND-03

**Iteración:** Extra práctico — fuera del protocolo de prompting medido (no corresponde a ninguna V del estudio)
**Herramienta:** Claude Code CLI, sesión administrativa (no la del agente que ejecuta los prompts P-0XX)
**Tipo:** Generación — pantallas del seguimiento del proceso de autoevaluación (V6) sobre la API REST existente, sin modificar el backend
**Fecha:** 2026-10-14

## Contexto previo
Backend cerrado hasta V6 (`c29b9bb`). No existía un prompt de FRONTEND-03; el alcance se infirió de `DESIGN_SGVA.md` (Módulo 5) y de los controladores de P-018 a P-026.

## Alcance
- `/ciclos`: lista con filtro y registro de ciclos (`GET`/`POST /api/ciclos`).
- `/ciclos/:id`: avance por fase con fecha de referencia, actividades (vencidas marcadas), participación con semáforo, grupos evaluadores, preevaluaciones, consolidación (`POST /api/preevaluaciones/{ciclo}/consolidacion`) y descarga del PDF con el estado del ciclo (`GET /api/reportes/ejecutivo` con `programa` y `periodo`).
- `/proyectos`: proyectos de mejoramiento (`GET /api/proyectos-mejoramiento`).
- `/comparacion`: resultados de varios periodos y diferencias (`GET /api/comparaciones`).
- Carga de datos: preevaluaciones y participación de actores.
- Pendiente: formularios de alta para actividades, grupos evaluadores, preevaluaciones individuales, participación y proyectos (solo se pueden cargar por archivo o por la API).

## Commits
`0ec0608` (cliente y tipos), `7553060` (componentes y utilidades) y `44c90ae` (pantallas).

## Verificación
`npm run build` sin errores; `npm run lint` sin avisos nuevos. No se probó contra el backend en ejecución ni hay pruebas automáticas de frontend.
