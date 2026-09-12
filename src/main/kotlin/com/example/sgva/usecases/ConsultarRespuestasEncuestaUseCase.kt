package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.RespuestaEncuesta

/**
 * Consulta las respuestas de encuesta de percepción registradas (Módulo 3): el
 * listado completo o una concreta por su identificador. No modifica el estado
 * del repositorio.
 */
class ConsultarRespuestasEncuestaUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /** Devuelve todas las respuestas registradas, en orden de inserción. */
    fun listarTodas(): List<RespuestaEncuesta> = repositorio.listarRespuestasEncuesta()

    /** Devuelve la respuesta con [id], o `null` si no existe ninguna con ese identificador. */
    fun buscarPorId(id: String): RespuestaEncuesta? = repositorio.buscarRespuestaEncuestaPorId(id)
}
