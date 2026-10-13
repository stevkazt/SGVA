package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.ActividadCiclo
import com.example.sgva.domain.CicloNoEncontradoException
import com.example.sgva.domain.EntidadDuplicadaException

/**
 * Registra una [ActividadCiclo] adicional en un ciclo ya existente (Módulo
 * 5, `DESIGN_SGVA.md` §0.1 y §2, V6). Las actividades se registran
 * libremente en cada ciclo, una por una, a diferencia de las fases fijas que
 * crea `RegistrarCicloAutoevaluacionUseCase`.
 */
class RegistrarActividadCicloUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /**
     * @throws CicloNoEncontradoException si no existe el ciclo referenciado por [actividad].
     * @throws EntidadDuplicadaException si ya existe una actividad con el mismo `id`.
     */
    fun ejecutar(actividad: ActividadCiclo): ActividadCiclo {
        repositorio.buscarCicloPorId(actividad.ciclo) ?: throw CicloNoEncontradoException(actividad.ciclo)

        val existente = repositorio.listarActividadesPorCiclo(actividad.ciclo)
            .firstOrNull { it.id == actividad.id }
        if (existente != null) {
            throw EntidadDuplicadaException("actividad", actividad.id)
        }

        return repositorio.guardarActividad(actividad)
    }
}
