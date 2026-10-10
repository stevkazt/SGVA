package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.EvaluacionDocumental

/**
 * Registra una nueva evaluación documental (Módulo 4).
 *
 * La validación de formato y rango de los campos ya está garantizada por el
 * bloque `init` de [EvaluacionDocumental]. Este caso de uso solo aporta la
 * regla de aplicación restante: la clave natural (programa, periodo,
 * característica) debe ser única.
 */
class RegistrarEvaluacionDocumentalUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /**
     * Da de alta [evaluacion] y la devuelve tal como quedó persistida.
     *
     * @throws EntidadDuplicadaException si ya existe una evaluación con la misma clave natural.
     */
    fun ejecutar(evaluacion: EvaluacionDocumental): EvaluacionDocumental {
        val existente = repositorio.buscarEvaluacionDocumental(
            evaluacion.programa,
            evaluacion.periodo,
            evaluacion.caracteristica,
        )
        if (existente != null) {
            throw EntidadDuplicadaException("evaluación documental", claveDe(evaluacion))
        }
        return repositorio.guardarEvaluacionDocumental(evaluacion)
    }

    private fun claveDe(evaluacion: EvaluacionDocumental): String =
        "${evaluacion.programa}/${evaluacion.periodo}/${evaluacion.caracteristica}"
}
