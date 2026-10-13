package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.GrupoEvaluador

/** Consulta los grupos evaluadores de un ciclo (Módulo 5, V6). No modifica el estado del repositorio. */
class ConsultarGrupoEvaluadorUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /** Devuelve los grupos evaluadores del [ciclo], en orden de inserción. */
    fun listarPorCiclo(ciclo: String): List<GrupoEvaluador> = repositorio.listarGruposEvaluadorPorCiclo(ciclo)
}
