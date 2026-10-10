package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.ResultadoEncuestaPregunta

/**
 * Registra un nuevo resultado de encuesta por pregunta (Módulo 4).
 *
 * La validación de formato y rango de los campos ya está garantizada por el
 * bloque `init` de [ResultadoEncuestaPregunta]. Este caso de uso solo aporta
 * la regla de aplicación restante: la clave natural (programa, periodo,
 * código de pregunta) debe ser única.
 */
class RegistrarResultadoEncuestaPreguntaUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /**
     * Da de alta [resultado] y lo devuelve tal como quedó persistido.
     *
     * @throws EntidadDuplicadaException si ya existe un resultado con la misma clave natural.
     */
    fun ejecutar(resultado: ResultadoEncuestaPregunta): ResultadoEncuestaPregunta {
        val existente = repositorio.buscarResultadoEncuestaPregunta(
            resultado.programa,
            resultado.periodo,
            resultado.codigoPregunta,
        )
        if (existente != null) {
            throw EntidadDuplicadaException("resultado de encuesta por pregunta", claveDe(resultado))
        }
        return repositorio.guardarResultadoEncuestaPregunta(resultado)
    }

    private fun claveDe(resultado: ResultadoEncuestaPregunta): String =
        "${resultado.programa}/${resultado.periodo}/${resultado.codigoPregunta}"
}
