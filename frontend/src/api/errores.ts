import { ErrorDeApi, ErrorDeRed } from './client'

/** Traduce cualquier error capturado a un mensaje apto para mostrar al usuario. */
export function mensajeDeError(error: unknown): string {
  if (error instanceof ErrorDeRed) return error.message
  if (error instanceof ErrorDeApi) return error.message
  if (error instanceof Error) return error.message
  return 'Ocurrió un error inesperado.'
}
