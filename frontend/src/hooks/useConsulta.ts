import { useEffect, useState } from 'react'
import { mensajeDeError } from '../api/errores'

interface Estado<T> {
  clave: string
  datos: T | null
  error: string | null
}

/**
 * Ejecuta `consulta` cada vez que cambia `clave` y descarta respuestas de claves anteriores.
 * `cargando` se deriva de la clave, sin llamar a `setState` de forma síncrona dentro del efecto.
 */
export function useConsulta<T>(clave: string, consulta: () => Promise<T>) {
  const [estado, setEstado] = useState<Estado<T> | null>(null)

  useEffect(() => {
    let vigente = true
    consulta()
      .then((datos) => vigente && setEstado({ clave, datos, error: null }))
      .catch((error) => vigente && setEstado({ clave, datos: null, error: mensajeDeError(error) }))
    return () => {
      vigente = false
    }
    // `consulta` es una clausura nueva en cada render; la identidad de la consulta la fija `clave`.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [clave])

  const vigente = estado?.clave === clave
  return {
    cargando: !vigente,
    datos: vigente ? estado.datos : null,
    error: vigente ? estado.error : null,
  }
}
