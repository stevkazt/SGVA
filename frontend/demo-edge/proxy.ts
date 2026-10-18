/**
 * Proxy de la demo pública: reenvía /api y /panel al backend de demo, que
 * corre en el computador del investigador y se expone con un túnel de
 * Cloudflare. Añade X-Demo-Key para que la puerta local acepte la petición.
 */
import { credencialesDe } from './acceso'

export interface Env {
  /** Usuarios con acceso, separados por coma. Cada uno tiene su propio entorno. */
  DEMO_USUARIOS: string
  /** Contraseña de acceso a la demo. */
  DEMO_PASSWORD: string
  /** Clave compartida con la puerta local (demo/servidor.mjs). */
  DEMO_KEY: string
  /** URL pública del túnel hacia la puerta local. */
  BACKEND_URL: string
}

const CABECERAS_QUE_NO_SE_REENVIAN = ['authorization', 'cookie', 'host', 'x-demo-key', 'x-sgva-usuario', 'x-sgva-sesion']

function json(estado: number, mensaje: string): Response {
  return new Response(JSON.stringify({ mensaje }), {
    status: estado,
    headers: { 'Content-Type': 'application/json; charset=utf-8' },
  })
}

export async function reenviar({ request, env }: { request: Request; env: Env }): Promise<Response> {
  if (!env.BACKEND_URL || !env.DEMO_KEY) {
    return json(503, 'La demo no está disponible en este momento.')
  }
  const entrada = new URL(request.url)
  const destino = new URL(entrada.pathname + entrada.search, env.BACKEND_URL)
  const usuario = credencialesDe(request)?.usuario
  if (!usuario) return json(401, 'Falta el usuario.')
  const cabeceras = new Headers(request.headers)
  for (const nombre of CABECERAS_QUE_NO_SE_REENVIAN) cabeceras.delete(nombre)
  cabeceras.set('x-demo-key', env.DEMO_KEY)
  cabeceras.set('x-sgva-usuario', usuario) // el usuario del login define el entorno

  try {
    return await fetch(destino, {
      method: request.method,
      headers: cabeceras,
      body: request.method === 'GET' || request.method === 'HEAD' ? undefined : request.body,
      redirect: 'manual',
    })
  } catch {
    return json(502, 'No se pudo conectar con el backend de la demo. Puede estar apagado.')
  }
}
