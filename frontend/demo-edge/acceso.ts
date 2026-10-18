/** Lectura de las credenciales HTTP Basic y normalización del usuario. */

export interface Credenciales {
  usuario: string
  contrasena: string
}

/** Minúsculas y solo letras y dígitos: «Mauricio» y «mauricio» son el mismo usuario. */
export function normalizarUsuario(usuario: string): string {
  return usuario.trim().toLowerCase().replace(/[^a-z0-9]/g, '')
}

export function credencialesDe(request: Request): Credenciales | null {
  const encabezado = request.headers.get('Authorization') ?? ''
  if (!encabezado.startsWith('Basic ')) return null
  try {
    const texto = atob(encabezado.slice(6))
    const separador = texto.indexOf(':')
    if (separador < 0) return null
    return { usuario: normalizarUsuario(texto.slice(0, separador)), contrasena: texto.slice(separador + 1) }
  } catch {
    return null
  }
}

/** Usuarios con acceso, separados por coma (variable DEMO_USUARIOS). */
export function usuariosPermitidos(lista: string | undefined): string[] {
  return (lista ?? '').split(',').map(normalizarUsuario).filter(Boolean)
}
