// Tipos espejo de los DTOs del dominio Kotlin (com.example.sgva.domain),
// solo los campos que el frontend consume.

export type EstadoEstudiante = 'MATRICULADO' | 'GRADUADO' | 'DESERTOR'
export type NivelFormacion = 'ESPECIALIZACION' | 'MAESTRIA' | 'DOCTORADO'
export type TipoDedicacion = 'TIEMPO_COMPLETO' | 'MEDIO_TIEMPO' | 'CATEDRA'
export type TipoEstamento = 'ESTUDIANTE' | 'PROFESOR' | 'EGRESADO' | 'EMPLEADOR'

export type TipoCarga =
  | 'estudiantes'
  | 'docentes'
  | 'resultados-encuesta-pregunta'
  | 'evaluaciones-documentales'
  | 'preevaluaciones'
  | 'participacion-actores'

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

export interface ResultadoAutoevaluacion {
  programa: string
  periodo: string
  promediosPorCaracteristica: Record<string, number | null>
  promediosPorFactor: Record<string, number | null>
  promedioGlobal: number | null
}

export type EstadoActividad = 'PLANIFICADA' | 'EN_CURSO' | 'COMPLETADA'
export type TipoSemaforo = 'ROJO' | 'NARANJA' | 'VERDE'
export type EstadoProyecto = 'PLANIFICADO' | 'EN_EJECUCION' | 'CERRADO'
export type OrigenProyecto = 'AUTOEVALUACION' | 'RECOMENDACION_CNA'

export interface CicloAutoevaluacion {
  id: string
  programa: string
  periodo: string
}

export interface FaseCiclo {
  ciclo: string
  numero: number
  nombre: string
}

export interface ActividadCiclo {
  id: string
  ciclo: string
  fase: number
  descripcion: string
  responsable: string
  fechaInicioPlanificada: string
  fechaFinPlanificada: string
  estado: EstadoActividad
}

export interface DetalleCiclo {
  ciclo: CicloAutoevaluacion
  fases: FaseCiclo[]
  actividades: ActividadCiclo[]
}

export interface ParticipacionActor {
  ciclo: string
  actor: string
  poblacion: number
  muestra: number
  respuestas: number
}

export interface ParticipacionConSemaforo {
  participacion: ParticipacionActor
  semaforo: TipoSemaforo | null
}

/** Los avances son fracciones entre 0 y 1; `null` cuando no hay actividades que medir. */
export interface AvanceCiclo {
  ciclo: string
  avancePorFase: Record<string, number | null>
  avanceCiclo: number | null
  actividadesVencidas: ActividadCiclo[]
  actividadesProximasAVencer: ActividadCiclo[]
  factoresConPreevaluacionPendiente: string[]
  participacionActores: ParticipacionConSemaforo[]
}

export interface GrupoEvaluador {
  ciclo: string
  factores: string[]
  taller: number
  fechaTaller: string | null
}

export interface PreevaluacionEvaluador {
  ciclo: string
  caracteristica: string
  evaluador: string
  calificacion: number
  justificacion: string | null
  fortaleza: string | null
  oportunidadMejora: string | null
}

export interface EvaluacionDocumental {
  programa: string
  caracteristica: string
  periodo: string
  calificacion: number
  justificacion: string | null
  sugerenciasMejora: string | null
}

export interface ProyectoMejoramiento {
  id: string
  programa: string
  nombre: string
  aspectosMejora: string[]
  origen: OrigenProyecto
  objetivo: string
  actividades: string[]
  factores: string[]
  estado: EstadoProyecto
}

export interface DiferenciaResultado {
  periodoBase: string
  periodoComparado: string
  diferenciasPorFactor: Record<string, number | null>
  diferenciaGlobal: number | null
}

export interface ComparacionResultados {
  programa: string
  resultados: ResultadoAutoevaluacion[]
  diferencias: DiferenciaResultado[]
}

export interface ErrorRespuesta {
  mensaje: string
}
