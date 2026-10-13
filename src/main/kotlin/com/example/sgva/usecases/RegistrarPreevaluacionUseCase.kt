package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.CicloNoEncontradoException
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.PreevaluacionEvaluador

/**
 * Registra una nueva [PreevaluacionEvaluador] (Módulo 5, `DESIGN_SGVA.md`
 * §0.1 y §2, V6). La clave natural es ciclo + característica + evaluador.
 */
class RegistrarPreevaluacionUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /**
     * @throws CicloNoEncontradoException si no existe el ciclo referenciado por [preevaluacion].
     * @throws EntidadDuplicadaException si ya existe una preevaluación con la misma clave natural.
     */
    fun ejecutar(preevaluacion: PreevaluacionEvaluador): PreevaluacionEvaluador {
        repositorio.buscarCicloPorId(preevaluacion.ciclo) ?: throw CicloNoEncontradoException(preevaluacion.ciclo)

        val existente = repositorio.buscarPreevaluacion(
            preevaluacion.ciclo,
            preevaluacion.caracteristica,
            preevaluacion.evaluador,
        )
        if (existente != null) {
            throw EntidadDuplicadaException("preevaluación", claveDe(preevaluacion))
        }
        return repositorio.guardarPreevaluacion(preevaluacion)
    }

    private fun claveDe(preevaluacion: PreevaluacionEvaluador): String =
        "${preevaluacion.ciclo}/${preevaluacion.caracteristica}/${preevaluacion.evaluador}"
}
