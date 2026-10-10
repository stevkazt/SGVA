package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.ResultadoEncuestaPregunta

/**
 * Consulta los resultados de encuesta por pregunta registrados (Módulo 4): el
 * listado completo o uno concreto por su clave natural (programa, periodo,
 * código de pregunta). No modifica el estado del repositorio.
 */
class ConsultarResultadoEncuestaPreguntaUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /** Devuelve todos los resultados registrados, en orden de inserción. */
    fun listarTodos(): List<ResultadoEncuestaPregunta> = repositorio.listarResultadosEncuestaPregunta()

    /**
     * Devuelve el resultado cuya clave natural es ([programa], [periodo],
     * [codigoPregunta]), o `null` si no existe ninguno con esa clave.
     */
    fun buscarPorClave(programa: String, periodo: String, codigoPregunta: String): ResultadoEncuestaPregunta? =
        repositorio.buscarResultadoEncuestaPregunta(programa, periodo, codigoPregunta)
}
