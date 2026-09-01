package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.IndicadorCalidad

/**
 * Base común de los casos de uso que producen [IndicadorCalidad] a partir de los
 * estudiantes registrados (Módulo 1, `DESIGN_SGVA.md` §2). Centraliza el acceso
 * al repositorio, el filtro opcional por facultad y el redondeo de los valores.
 */
abstract class CalculoDeIndicadorUseCase(
    protected val repositorio: AcademicDataRepository,
) {

    /**
     * Estudiantes registrados, opcionalmente restringidos a una [facultad]
     * (comparación sin distinguir mayúsculas y recortando espacios).
     */
    protected fun estudiantesRegistrados(facultad: String?): List<Estudiante> {
        val todos = repositorio.listarEstudiantes()
        val filtro = facultad?.trim()?.takeIf { it.isNotEmpty() } ?: return todos
        return todos.filter { it.facultad.trim().equals(filtro, ignoreCase = true) }
    }

    /** Etiqueta de facultad para el indicador: la facultad indicada o [IndicadorCalidad.TODAS_LAS_FACULTADES]. */
    protected fun etiquetaFacultad(facultad: String?): String =
        facultad?.trim()?.takeIf { it.isNotEmpty() } ?: IndicadorCalidad.TODAS_LAS_FACULTADES

    /** Redondea a dos decimales, la precisión con la que el Dashboard muestra los indicadores. */
    protected fun redondearACentesimas(valor: Double): Double =
        Math.round(valor * 100.0) / 100.0
}
