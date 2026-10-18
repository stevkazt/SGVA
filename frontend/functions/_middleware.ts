// Contraseña simple (HTTP Basic) para toda la demo, páginas y API. Cualquier
// usuario sirve; solo se comprueba la contraseña. Si no hay contraseña
// configurada la demo queda cerrada.
import type { Env } from '../demo-edge/proxy'

async function iguales(a: string, b: string): Promise<boolean> {
  const codificador = new TextEncoder()
  const [ha, hb] = await Promise.all([
    crypto.subtle.digest('SHA-256', codificador.encode(a)),
    crypto.subtle.digest('SHA-256', codificador.encode(b)),
  ])
  const va = new Uint8Array(ha)
  const vb = new Uint8Array(hb)
  let diferencia = 0
  for (let i = 0; i < va.length; i++) diferencia |= va[i] ^ vb[i]
  return diferencia === 0
}

export async function onRequest(contexto: { request: Request; env: Env; next: () => Promise<Response> }) {
  const { request, env, next } = contexto
  if (!env.DEMO_PASSWORD) {
    return new Response('La demo no está configurada.', { status: 503 })
  }
  const encabezado = request.headers.get('Authorization') ?? ''
  if (encabezado.startsWith('Basic ')) {
    try {
      const credenciales = atob(encabezado.slice(6))
      const contrasena = credenciales.slice(credenciales.indexOf(':') + 1)
      if (await iguales(contrasena, env.DEMO_PASSWORD)) return next()
    } catch {
      // Cabecera mal formada: se trata como credenciales inválidas.
    }
  }
  return new Response('Acceso restringido. Escribe la contraseña de la demo.', {
    status: 401,
    headers: { 'WWW-Authenticate': 'Basic realm="SGVA demo", charset="UTF-8"' },
  })
}
