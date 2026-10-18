// Acceso a la demo por HTTP Basic: solo entran los usuarios de DEMO_USUARIOS
// (por ejemplo «johann,mauricio»), todos con la misma contraseña. El usuario
// identifica el entorno de datos de cada persona, así que no depende del
// navegador. Si falta la contraseña o la lista de usuarios, la demo queda cerrada.
import { credencialesDe, usuariosPermitidos } from '../demo-edge/acceso'
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
  const permitidos = usuariosPermitidos(env.DEMO_USUARIOS)
  if (!env.DEMO_PASSWORD || permitidos.length === 0) {
    return new Response('La demo no está configurada.', { status: 503 })
  }
  const credenciales = credencialesDe(request)
  const contrasenaCorrecta = credenciales ? await iguales(credenciales.contrasena, env.DEMO_PASSWORD) : false
  if (credenciales && contrasenaCorrecta && permitidos.includes(credenciales.usuario)) return next()

  return new Response('Acceso restringido. Escribe tu usuario y la contraseña de la demo.', {
    status: 401,
    headers: { 'WWW-Authenticate': 'Basic realm="SGVA demo", charset="UTF-8"' },
  })
}
