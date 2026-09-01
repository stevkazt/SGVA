package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.Docente

/**
 * Consulta los docentes registrados (Factor 3): el listado completo o uno
 * concreto por su identificador. No modifica el estado del repositorio.
 */
class ConsultarDocentesUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /** Devuelve todos los docentes registrados, en orden de inserción. */
    fun listarTodos(): List<Docente> = repositorio.listarDocentes()

    /** Devuelve el docente con [id], o `null` si no existe ninguno con ese identificador. */
    fun buscarPorId(id: String): Docente? = repositorio.buscarDocentePorId(id)
}
