package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.Estudiante

/**
 * Registra un nuevo estudiante en el sistema (Factor 2).
 *
 * La validación de formato y rango de los campos ya está garantizada por el
 * bloque `init` de [Estudiante] (una instancia corrupta no puede construirse).
 * Este caso de uso solo aporta la regla de aplicación restante: el
 * identificador debe ser único.
 */
class RegistrarEstudianteUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /**
     * Da de alta [estudiante] y lo devuelve tal como quedó persistido.
     *
     * @throws EntidadDuplicadaException si ya existe un estudiante con el mismo `id`.
     */
    fun ejecutar(estudiante: Estudiante): Estudiante {
        if (repositorio.buscarEstudiantePorId(estudiante.id) != null) {
            throw EntidadDuplicadaException("estudiante", estudiante.id)
        }
        return repositorio.guardarEstudiante(estudiante)
    }
}
