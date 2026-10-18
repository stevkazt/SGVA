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
Tu Mac:  puerta (demo/servidor.mjs, :8081) ── X-Sgva-Modo ──► backend demo   (:8090, base sgva_publica_demo)
                                                           └► backend limpia (:8091, base sgva_publica_limpia)
```

- **Contraseña:** HTTP Basic de Cloudflare Pages (`frontend/functions/_middleware.ts`), cualquier usuario.
  Se genera una vez y queda en `~/respaldos_sgva/.demo_secrets` (fuera del repositorio).
- **La puerta local exige `X-Demo-Key`**, así que la URL del túnel no sirve por sí sola sin pasar por la contraseña.
- **Datos demo:** modelo 2025 y proceso 2025 reales (fixtures de `src/test/resources`) más datos sintéticos
  (`demo/generar-datos.mjs`: estudiantes, docentes, respuestas individuales de encuesta y el periodo 20251).
- **Base limpia:** vacía; el visitante sube sus archivos. «Restaurar datos demo» y «Vaciar base limpia» están en el panel.

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
