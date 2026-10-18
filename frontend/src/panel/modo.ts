/**
 * Modo de datos de la demo pública. El backend de la demo tiene dos bases:
 * `demo` (datos de ejemplo) y `limpia` (vacía, para que el visitante cargue
 * sus propios archivos). La elección se guarda en el navegador y se envía en
 * la cabecera X-Sgva-Modo de cada petición a /api.
 */
export type ModoDatos = 'demo' | 'limpia'

const CLAVE = 'sgva.modo'

export const panelActivo = import.meta.env.VITE_PANEL_PRUEBAS === '1'

export function modoActual(): ModoDatos {
  return localStorage.getItem(CLAVE) === 'limpia' ? 'limpia' : 'demo'
}

export function cambiarModo(modo: ModoDatos): void {
  localStorage.setItem(CLAVE, modo)
  // Recargar hace que todas las pantallas vuelvan a consultar la base elegida.
  window.location.reload()
}

/** Añade la cabecera del modo a las peticiones a /api (la demo la lee en la puerta). */
export function instalarModo(): void {
  const original = window.fetch.bind(window)
  window.fetch = (entrada, init) => {
    const ruta = typeof entrada === 'string' ? entrada : entrada instanceof URL ? entrada.pathname : entrada.url
    if (!ruta.startsWith('/api/')) return original(entrada, init)
    const cabeceras = new Headers(init?.headers)
    cabeceras.set('X-Sgva-Modo', modoActual())
    return original(entrada, { ...init, headers: cabeceras })
  }
}

async function accionPanel(ruta: string): Promise<string> {
  const respuesta = await fetch(ruta, { method: 'POST' })
  const cuerpo = (await respuesta.json().catch(() => ({}))) as { mensaje?: string }
  if (!respuesta.ok) throw new Error(cuerpo.mensaje ?? `Error HTTP ${respuesta.status}`)
  return cuerpo.mensaje ?? 'Hecho.'
}

export const restaurarDemo = () => accionPanel('/panel/restaurar-demo')
export const vaciarLimpia = () => accionPanel('/panel/vaciar-limpia')
