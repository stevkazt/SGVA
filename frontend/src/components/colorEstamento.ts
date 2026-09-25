import type { TipoEstamento } from '../api/types'

// Orden categórico fijo: el mismo estamento siempre tiene el mismo color,
// sin importar cuáles otros estamentos estén presentes en los datos.
export const COLOR_ESTAMENTO: Record<TipoEstamento, string> = {
  ESTUDIANTE: 'var(--series-1)',
  PROFESOR: 'var(--series-2)',
  EGRESADO: 'var(--series-3)',
  EMPLEADOR: 'var(--series-4)',
}

export const ETIQUETA_ESTAMENTO: Record<TipoEstamento, string> = {
  ESTUDIANTE: 'Estudiante',
  PROFESOR: 'Profesor',
  EGRESADO: 'Egresado',
  EMPLEADOR: 'Empleador',
}
