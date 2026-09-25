import type { IndicadorCalidad, MatrizRadar, PonderacionLikert, ResultadoIngesta, TipoCarga } from './types'

/** Error tipado para fallos de red (backend no responde, DNS, CORS, etc.). */
export class ErrorDeRed extends Error {
  constructor() {
    super('No se pudo conectar con el backend. Verifica que el servicio SGVA esté corriendo en el puerto 8080.')
    this.name = 'ErrorDeRed'
  }
}

/** Error tipado para respuestas HTTP no exitosas, con el mensaje que traduce ManejadorGlobalDeErrores. */
export class ErrorDeApi extends Error {
  readonly status: number

  constructor(status: number, mensaje: string) {
    super(mensaje)
    this.status = status
    this.name = 'ErrorDeApi'
  }
}

async function peticion<T>(ruta: string, init?: RequestInit): Promise<T> {
  let respuesta: Response
  try {
    respuesta = await fetch(ruta, init)
  } catch {
    throw new ErrorDeRed()
  }

  if (!respuesta.ok) {
    const mensaje = await respuesta
      .json()
      .then((cuerpo: { mensaje?: string }) => cuerpo.mensaje)
      .catch(() => undefined)
    throw new ErrorDeApi(respuesta.status, mensaje ?? `Error inesperado del servidor (HTTP ${respuesta.status}).`)
  }

  if (respuesta.status === 204) {
    return undefined as T
  }
  return respuesta.json() as Promise<T>
}

function conParametros(ruta: string, parametros: Record<string, string | undefined>): string {
  const busqueda = new URLSearchParams()
  for (const [clave, valor] of Object.entries(parametros)) {
    if (valor !== undefined && valor.trim() !== '') busqueda.set(clave, valor.trim())
  }
  const cadena = busqueda.toString()
  return cadena ? `${ruta}?${cadena}` : ruta
}

const RUTA_CARGA: Record<TipoCarga, string> = {
  estudiantes: '/api/estudiantes/archivos',
  docentes: '/api/docentes/archivos',
}

export function cargarArchivo(tipo: TipoCarga, archivo: File): Promise<ResultadoIngesta> {
  const formulario = new FormData()
  formulario.append('archivo', archivo)
  return peticion(RUTA_CARGA[tipo], { method: 'POST', body: formulario })
}

export interface FiltrosIndicadores {
  facultad?: string
  periodo?: string
  cohorte?: string
}

export const indicadores = {
  evolucionMatricula: (f: FiltrosIndicadores) =>
    peticion<IndicadorCalidad[]>(conParametros('/api/indicadores/evolucion-matricula', { facultad: f.facultad })),

  tasaDesercion: (f: FiltrosIndicadores & { cohorte: string }) =>
    peticion<IndicadorCalidad>(
      conParametros('/api/indicadores/tasa-desercion', { cohorte: f.cohorte, facultad: f.facultad }),
    ),

  saberPro: (f: FiltrosIndicadores) =>
    peticion<IndicadorCalidad[]>(conParametros('/api/indicadores/saber-pro', { facultad: f.facultad })),

  distribucionFormacion: (f: FiltrosIndicadores) =>
    peticion<IndicadorCalidad[]>(
      conParametros('/api/indicadores/distribucion-formacion', { periodo: f.periodo, facultad: f.facultad }),
    ),

  capacidadInstalada: (f: FiltrosIndicadores) =>
    peticion<IndicadorCalidad>(
      conParametros('/api/indicadores/capacidad-instalada', { periodo: f.periodo, facultad: f.facultad }),
    ),
}

export const encuestas = {
  ponderacionLikert: (periodo?: string) =>
    peticion<PonderacionLikert[]>(conParametros('/api/encuestas/ponderacion-likert', { periodo })),

  matrizRadar: (periodo?: string) =>
    peticion<MatrizRadar>(conParametros('/api/encuestas/matriz-radar', { periodo })),
}

async function peticionArchivo(ruta: string): Promise<{ blob: Blob; nombreArchivo: string }> {
  let respuesta: Response
  try {
    respuesta = await fetch(ruta)
  } catch {
    throw new ErrorDeRed()
  }

  if (!respuesta.ok) {
    const mensaje = await respuesta
      .json()
      .then((cuerpo: { mensaje?: string }) => cuerpo.mensaje)
      .catch(() => undefined)
    throw new ErrorDeApi(respuesta.status, mensaje ?? `Error inesperado del servidor (HTTP ${respuesta.status}).`)
  }

  const disposicion = respuesta.headers.get('Content-Disposition') ?? ''
  const nombreArchivo = /filename="?([^"; ]+)"?/.exec(disposicion)?.[1] ?? 'reporte-ejecutivo.pdf'
  const blob = await respuesta.blob()
  return { blob, nombreArchivo }
}

export interface FiltrosReporte {
  periodo?: string
  facultad?: string
  cohorte?: string
}

export const reportes = {
  ejecutivo: (f: FiltrosReporte) =>
    peticionArchivo(
      conParametros('/api/reportes/ejecutivo', { periodo: f.periodo, facultad: f.facultad, cohorte: f.cohorte }),
    ),
}
