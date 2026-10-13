package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.ProyectoMejoramiento

/** Registra un nuevo [ProyectoMejoramiento] del plan de mejoramiento (Módulo 5, V6). */
class RegistrarProyectoMejoramientoUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /** @throws EntidadDuplicadaException si ya existe un proyecto con el mismo `id`. */
    fun ejecutar(proyecto: ProyectoMejoramiento): ProyectoMejoramiento {
        if (repositorio.buscarProyectoPorId(proyecto.id) != null) {
            throw EntidadDuplicadaException("proyecto de mejoramiento", proyecto.id)
        }
        return repositorio.guardarProyecto(proyecto)
    }
}
