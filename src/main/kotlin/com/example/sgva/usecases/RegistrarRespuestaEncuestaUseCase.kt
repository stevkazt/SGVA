package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.RespuestaEncuesta

/**
 * Registra una nueva respuesta de encuesta de percepción (Módulo 3).
 *
 * La validación de formato y rango de los campos ya está garantizada por el
 * bloque `init` de [RespuestaEncuesta]. Este caso de uso solo aporta la regla
 * de aplicación restante: el identificador debe ser único.
 */
class RegistrarRespuestaEncuestaUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /**
     * Da de alta [respuesta] y la devuelve tal como quedó persistida.
     *
     * @throws EntidadDuplicadaException si ya existe una respuesta con el mismo `id`.
     */
    fun ejecutar(respuesta: RespuestaEncuesta): RespuestaEncuesta {
        if (repositorio.buscarRespuestaEncuestaPorId(respuesta.id) != null) {
            throw EntidadDuplicadaException("respuesta de encuesta", respuesta.id)
        }
        return repositorio.guardarRespuestaEncuesta(respuesta)
    }
}
