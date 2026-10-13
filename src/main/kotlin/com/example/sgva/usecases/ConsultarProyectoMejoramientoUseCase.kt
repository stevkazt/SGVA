package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.ProyectoMejoramiento

/** Consulta los proyectos del plan de mejoramiento (Módulo 5, V6). No modifica el estado del repositorio. */
class ConsultarProyectoMejoramientoUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /** Devuelve todos los proyectos registrados, en orden de inserción. */
    fun listarTodos(): List<ProyectoMejoramiento> = repositorio.listarProyectos()

    /** Devuelve el proyecto con [id], o `null` si no existe. */
    fun buscarPorId(id: String): ProyectoMejoramiento? = repositorio.buscarProyectoPorId(id)
}
