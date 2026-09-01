package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.Docente
import com.example.sgva.domain.EntidadDuplicadaException

/**
 * Registra un nuevo docente de la planta profesoral (Factor 3).
 *
 * La validación de formato de los campos ya está garantizada por el bloque
 * `init` de [Docente]. Este caso de uso solo aporta la regla de aplicación
 * restante: el identificador debe ser único.
 */
class RegistrarDocenteUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /**
     * Da de alta [docente] y lo devuelve tal como quedó persistido.
     *
     * @throws EntidadDuplicadaException si ya existe un docente con el mismo `id`.
     */
    fun ejecutar(docente: Docente): Docente {
        if (repositorio.buscarDocentePorId(docente.id) != null) {
            throw EntidadDuplicadaException("docente", docente.id)
        }
        return repositorio.guardarDocente(docente)
    }
}
