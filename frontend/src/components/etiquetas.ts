import type { TonoInsignia } from './Insignia'
import type { EstadoActividad, EstadoProyecto, OrigenProyecto, TipoSemaforo } from '../api/types'

export const ETIQUETA_ESTADO_ACTIVIDAD: Record<EstadoActividad, string> = {
  PLANIFICADA: 'Planificada',
  EN_CURSO: 'En curso',
  COMPLETADA: 'Completada',
}

export const TONO_ESTADO_ACTIVIDAD: Record<EstadoActividad, TonoInsignia> = {
  PLANIFICADA: 'neutro',
  EN_CURSO: 'alerta',
  COMPLETADA: 'bueno',
}

export const ETIQUETA_SEMAFORO: Record<TipoSemaforo, string> = {
  ROJO: 'Rojo',
  NARANJA: 'Naranja',
  VERDE: 'Verde',
}

export const TONO_SEMAFORO: Record<TipoSemaforo, TonoInsignia> = {
  ROJO: 'critico',
  NARANJA: 'alerta',
  VERDE: 'bueno',
}

export const ETIQUETA_ESTADO_PROYECTO: Record<EstadoProyecto, string> = {
  PLANIFICADO: 'Planificado',
  EN_EJECUCION: 'En ejecución',
  CERRADO: 'Cerrado',
}

export const ETIQUETA_ORIGEN: Record<OrigenProyecto, string> = {
  AUTOEVALUACION: 'Autoevaluación',
  RECOMENDACION_CNA: 'Recomendación CNA',
}
