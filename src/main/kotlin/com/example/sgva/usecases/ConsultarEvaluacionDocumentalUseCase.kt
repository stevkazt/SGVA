package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.EvaluacionDocumental

/**
 * Consulta las evaluaciones documentales registradas (Módulo 4): el listado
 * completo o una concreta por su clave natural (programa, periodo,
 * característica). No modifica el estado del repositorio.
 */
class ConsultarEvaluacionDocumentalUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /** Devuelve todas las evaluaciones documentales registradas, en orden de inserción. */
    fun listarTodas(): List<EvaluacionDocumental> = repositorio.listarEvaluacionesDocumentales()

    /**
     * Devuelve la evaluación cuya clave natural es ([programa], [periodo],
     * [caracteristica]), o `null` si no existe ninguna con esa clave.
     */
    fun buscarPorClave(programa: String, periodo: String, caracteristica: String): EvaluacionDocumental? =
        repositorio.buscarEvaluacionDocumental(programa, periodo, caracteristica)
}
