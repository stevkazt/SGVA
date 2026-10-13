package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.CicloNoEncontradoException
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.ParticipacionActor

/**
 * Registra una nueva [ParticipacionActor] de un ciclo (Módulo 5,
 * `DESIGN_SGVA.md` §0.1 y §2, V6). La clave natural es ciclo + actor; el
 * porcentaje y el semáforo se derivan siempre de la entidad, no se reciben
 * ni se persisten por separado.
 */
class RegistrarParticipacionActorUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /**
     * @throws CicloNoEncontradoException si no existe el ciclo referenciado por [participacion].
     * @throws EntidadDuplicadaException si ya existe una participación con la misma clave natural.
     */
    fun ejecutar(participacion: ParticipacionActor): ParticipacionActor {
        repositorio.buscarCicloPorId(participacion.ciclo) ?: throw CicloNoEncontradoException(participacion.ciclo)

        val existente = repositorio.buscarParticipacion(participacion.ciclo, participacion.actor)
        if (existente != null) {
            throw EntidadDuplicadaException("participación", "${participacion.ciclo}/${participacion.actor}")
        }
        return repositorio.guardarParticipacion(participacion)
    }
}
