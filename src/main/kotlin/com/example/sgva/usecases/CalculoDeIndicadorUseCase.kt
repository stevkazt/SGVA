package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.Docente
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.IndicadorCalidad

/**
 * Base común de los casos de uso que producen [IndicadorCalidad] a partir de los
 * datos registrados (Módulos 1 y 2, `DESIGN_SGVA.md` §2). Centraliza el acceso
 * al repositorio, los filtros opcionales por facultad y periodo, y el redondeo
 * de los valores.
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

    /**
     * Docentes registrados, opcionalmente restringidos a una [facultad] (sin
     * distinguir mayúsculas) y a un [periodo] académico exacto.
     */
    protected fun docentesRegistrados(facultad: String?, periodo: String?): List<Docente> {
        val filtroFacultad = facultad?.trim()?.takeIf { it.isNotEmpty() }
        val filtroPeriodo = periodo?.trim()?.takeIf { it.isNotEmpty() }
        return repositorio.listarDocentes().filter { docente ->
            (filtroFacultad == null || docente.facultad.trim().equals(filtroFacultad, ignoreCase = true)) &&
                (filtroPeriodo == null || docente.periodo == filtroPeriodo)
        }
    }

    /** Etiqueta de facultad para el indicador: la facultad indicada o [IndicadorCalidad.TODAS_LAS_FACULTADES]. */
    protected fun etiquetaFacultad(facultad: String?): String =
        facultad?.trim()?.takeIf { it.isNotEmpty() } ?: IndicadorCalidad.TODAS_LAS_FACULTADES

    /** Etiqueta de periodo para el indicador: el periodo indicado o [IndicadorCalidad.CONSOLIDADO]. */
    protected fun etiquetaPeriodo(periodo: String?): String =
        periodo?.trim()?.takeIf { it.isNotEmpty() } ?: IndicadorCalidad.CONSOLIDADO

    /** Fragmento " (periodo '...', facultad '...')" para los mensajes de error, o cadena vacía si no hay filtros. */
    protected fun descripcionFiltro(periodo: String?, facultad: String?): String {
        val partes = listOfNotNull(
            periodo?.trim()?.takeIf { it.isNotEmpty() }?.let { "periodo '$it'" },
            facultad?.trim()?.takeIf { it.isNotEmpty() }?.let { "facultad '$it'" },
        )
        return if (partes.isEmpty()) "" else " (" + partes.joinToString(", ") + ")"
    }

    /** Redondea a dos decimales, la precisión con la que el Dashboard muestra los indicadores. */
    protected fun redondearACentesimas(valor: Double): Double =
        Math.round(valor * 100.0) / 100.0
}
