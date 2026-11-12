# Demo pública de SGVA

Rama `demo/panel-pruebas`. Permite que otra persona pruebe el frontend en internet sin instalar nada.
No modifica `src/`: el backend es el mismo; todo lo nuevo está en `demo/` y en el frontend.

## Cómo funciona

```
Navegador ── https://sgva-demo.pages.dev (contraseña) ──► Cloudflare Pages
                                                           ├─ sirve el frontend (build con panel de pruebas)
                                                           └─ Pages Function: reenvía /api y /panel
                                                                      │  (añade X-Demo-Key)
                                                            túnel de Cloudflare
                                                                      │
Tu Mac:  puerta (demo/servidor.mjs, :8081)
           ├─ X-Sgva-Usuario = usuario del login (lo añade la Pages Function), X-Sgva-Modo = demo | limpia
           └─► un backend SGVA por usuario (puertos 8100+), con su propia base en PostgreSQL:
                 sgva_s_<usuario>_demo   (clon de la plantilla sgva_plantilla, con datos de ejemplo)
                 sgva_s_<usuario>_limpia (vacía)
```

- **Acceso:** HTTP Basic de Cloudflare Pages (`frontend/functions/_middleware.ts`). Solo entran los usuarios de
  `DEMO_USUARIOS` (hoy `johann` y `mauricio`, sin distinguir mayúsculas), todos con la misma contraseña.
  Usuarios y contraseña viven en `~/respaldos_sgva/.demo_secrets` (fuera del repositorio).
- **La puerta local exige `X-Demo-Key`**, así que la URL del túnel no sirve por sí sola sin pasar por el login.
- **Cada usuario tiene su entorno:** lo que carga o borra uno no lo ve el otro, y no depende del navegador: borrar los
  datos del sitio o entrar desde otro equipo con el mismo usuario abre el mismo entorno. Solo corre un backend por usuario
  (al cambiar de modo se apaga uno y arranca el otro, unos 6 s) y la puerta los levanta cuando hacen falta.
- **Sin caducidad:** los entornos no se borran solos. Se pueden liberar con `demo/sesiones.sh liberar <usuario>` (o `todas`).
- **Para añadir o cambiar usuarios:** editar `DEMO_USUARIOS` en `.demo_secrets` y volver a correr `demo/publicar.sh`.
- **Datos demo:** modelo 2025 y proceso 2025 reales (fixtures de `src/test/resources`) más datos sintéticos
  (`demo/generar-datos.mjs`: estudiantes, docentes, respuestas individuales de encuesta y el periodo 20251).
- **Panel:** muestra el usuario; «Restaurar datos demo» y «Vaciar base limpia» afectan solo a su entorno.
- **Memoria:** cada backend usa hasta 512 MB (`SGVA_JVM_MEM`); con dos usuarios, como máximo dos a la vez.

## Uso

```
cd ~/respaldos_sgva/wt-demo      # worktree de esta rama
./gradlew bootJar -x test        # solo si cambió el backend
demo/publicar.sh                 # levanta todo, abre el túnel y despliega
demo/desplegar.sh                # solo actualiza el frontend y el documento (rápido)
demo/detener.sh                  # apaga todo
```

- Cada `publicar.sh` abre un túnel nuevo (URL distinta) y vuelve a desplegar; la dirección pública no cambia.
- La demo solo responde mientras el Mac esté encendido y los procesos corriendo.
- El backend corre con la base de PostgreSQL del contenedor `sgva-postgres`; no toca la base `sgva` ni otras.
- Logs en `demo/logs/`.

## Con Docker (revisores)

Solo requiere Docker; no hay login ni usuarios (la puerta usa el entorno `local`):

```
docker compose up --build
```

Abre http://localhost:5173. Son dos contenedores: `db` (PostgreSQL) y `app` (la puerta de la demo, los backends y el frontend con el panel). La primera vez prepara los datos de ejemplo (unos 30 s). **Cómo saber que funcionó:** en la terminal aparece `Local: http://localhost:5173/` (el servidor del frontend) y, al abrir la página, el panel lateral con los datos de ejemplo. Los avisos `WARN` del arranque son normales. Deja la terminal abierta mientras pruebas; `Ctrl+C` detiene todo. Dentro del contenedor la puerta habla con PostgreSQL por red (`PGHOST`) en vez de `docker exec`.

## Desarrollo local del panel

```
demo/iniciar-local.sh
cd frontend && VITE_PANEL_PRUEBAS=1 VITE_BACKEND_URL=http://127.0.0.1:8081 npx vite
```

Sin `VITE_PANEL_PRUEBAS=1` el frontend es el de siempre (sin panel ni cabecera de modo).

## Esta rama no se publica en el repositorio remoto

- Un hook `pre-push` (en el `.git` común) rechaza cualquier push que incluya ramas `demo/*`, también con `--all` o `--mirror`.
- `branch.demo/panel-pruebas.pushRemote` apunta a un remoto inexistente, así que un `git push` sin argumentos falla.
- Los cambios llegan a internet solo con `demo/publicar.sh` o `demo/desplegar.sh` (Cloudflare Pages, sin pasar por GitHub).
