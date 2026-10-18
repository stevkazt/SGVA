/**
 * Modo de datos de la demo pública. Cada usuario (el del login) tiene dos
 * bases: `demo` (datos de ejemplo) y `limpia` (vacía, para que cargue sus
 * propios archivos). La elección se guarda en el navegador y se envía en la
 * cabecera X-Sgva-Modo de cada petición a /api.
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

/** Añade el modo a las peticiones a /api y /panel (la demo lo lee en la puerta). */
export function instalarModo(): void {
  const original = window.fetch.bind(window)
  window.fetch = (entrada, init) => {
    const ruta = typeof entrada === 'string' ? entrada : entrada instanceof URL ? entrada.pathname : entrada.url
    if (!ruta.startsWith('/api/') && !ruta.startsWith('/panel/')) return original(entrada, init)
    const cabeceras = new Headers(init?.headers)
    cabeceras.set('X-Sgva-Modo', modoActual())
    return original(entrada, { ...init, headers: cabeceras })
  }
}

interface RespuestaPanel {
  mensaje?: string
  usuario?: string
}

async function accionPanel(ruta: string): Promise<RespuestaPanel> {
  const respuesta = await fetch(ruta, { method: 'POST' })
  const cuerpo = (await respuesta.json().catch(() => ({}))) as RespuestaPanel
  if (!respuesta.ok) throw new Error(cuerpo.mensaje ?? `Error HTTP ${respuesta.status}`)
  return cuerpo
}

/** Pide a la demo que deje listo el entorno del usuario (puede tardar unos segundos la primera vez). */
export const prepararEntorno = async (): Promise<string> => (await accionPanel('/panel/preparar')).usuario ?? ''
export const restaurarDemo = async (): Promise<string> => (await accionPanel('/panel/restaurar-demo')).mensaje ?? 'Hecho.'
export const vaciarLimpia = async (): Promise<string> => (await accionPanel('/panel/vaciar-limpia')).mensaje ?? 'Hecho.'
