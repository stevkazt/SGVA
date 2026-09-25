// Tipos espejo de los DTOs del dominio Kotlin (com.example.sgva.domain),
// solo los campos que el frontend consume.

export type EstadoEstudiante = 'MATRICULADO' | 'GRADUADO' | 'DESERTOR'
export type NivelFormacion = 'ESPECIALIZACION' | 'MAESTRIA' | 'DOCTORADO'
export type TipoDedicacion = 'TIEMPO_COMPLETO' | 'MEDIO_TIEMPO' | 'CATEDRA'
export type TipoEstamento = 'ESTUDIANTE' | 'PROFESOR' | 'EGRESADO' | 'EMPLEADOR'

export type TipoCarga = 'estudiantes' | 'docentes'

export interface RegistroOmitido {
  id: string
  motivo: string
}

export interface ResultadoIngesta {
  nombreArchivo: string
  totalRegistrosLeidos: number
  registrados: number
  omitidos: RegistroOmitido[]
}

export interface IndicadorCalidad {
  nombre: string
  valor: number
  periodo: string
  facultad: string
}

export interface PonderacionLikert {
  factor: string
  estamento: TipoEstamento
  promedio: number
  periodo: string
}

export interface SerieRadar {
  estamento: TipoEstamento
  promediosPorFactor: Record<string, number>
}

export interface MatrizRadar {
  periodo: string
  factores: string[]
  series: SerieRadar[]
}

export interface ErrorRespuesta {
  mensaje: string
}
