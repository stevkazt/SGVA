package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.PreevaluacionEvaluador

/** Consulta las preevaluaciones de un ciclo (Módulo 5, V6). No modifica el estado del repositorio. */
class ConsultarPreevaluacionUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /** Devuelve las preevaluaciones del [ciclo], en orden de inserción. */
    fun listarPorCiclo(ciclo: String): List<PreevaluacionEvaluador> = repositorio.listarPreevaluacionesPorCiclo(ciclo)
}
