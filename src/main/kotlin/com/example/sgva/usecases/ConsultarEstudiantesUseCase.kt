package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.Estudiante

/**
 * Consulta los estudiantes registrados (Factor 2): el listado completo o uno
 * concreto por su identificador. No modifica el estado del repositorio.
 */
class ConsultarEstudiantesUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /** Devuelve todos los estudiantes registrados, en orden de inserción. */
    fun listarTodos(): List<Estudiante> = repositorio.listarEstudiantes()

    /** Devuelve el estudiante con [id], o `null` si no existe ninguno con ese identificador. */
    fun buscarPorId(id: String): Estudiante? = repositorio.buscarEstudiantePorId(id)
}
