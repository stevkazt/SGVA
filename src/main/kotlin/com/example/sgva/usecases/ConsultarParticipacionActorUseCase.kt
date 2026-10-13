package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.ParticipacionActor

/** Consulta la participación de actores de un ciclo (Módulo 5, V6). No modifica el estado del repositorio. */
class ConsultarParticipacionActorUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /** Devuelve las participaciones del [ciclo], en orden de inserción. */
    fun listarPorCiclo(ciclo: String): List<ParticipacionActor> = repositorio.listarParticipacionPorCiclo(ciclo)
}
