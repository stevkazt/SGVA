package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.EquivalenciaTiempoCompleto
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.IndicadorCalidad

/**
 * Módulo 2 — capacidad instalada / relación estudiantes-profesores
 * (`DESIGN_SGVA.md` §2).
 *
 * Calcula cuántos estudiantes matriculados hay por cada profesor de Tiempo
 * Completo Equivalente (TCE). El numerador son los estudiantes en estado
 * [EstadoEstudiante.MATRICULADO]; el denominador es la suma de los factores de
 * dedicación de los docentes ([EquivalenciaTiempoCompleto]). Ambos conjuntos se
 * pueden restringir por periodo (solo docentes) y por facultad.
 */
class CalcularCapacidadInstaladaUseCase(
    repositorio: AcademicDataRepository,
) : CalculoDeIndicadorUseCase(repositorio) {

    /**
     * @throws DatosInsuficientesException si no hay docentes (el denominador sería 0).
     */
    fun ejecutar(periodo: String? = null, facultad: String? = null): IndicadorCalidad {
        val matriculados = estudiantesRegistrados(facultad)
            .count { it.estado == EstadoEstudiante.MATRICULADO }

        val profesoresTce = docentesRegistrados(facultad, periodo)
            .sumOf { EquivalenciaTiempoCompleto.factor(it.dedicacion) }

        if (profesoresTce == 0.0) {
            throw DatosInsuficientesException(
                "No hay docentes registrados para calcular la relación estudiante/profesor" +
                    descripcionFiltro(periodo, facultad) + ".",
            )
        }

        return IndicadorCalidad(
            nombre = IndicadorCalidad.RELACION_ESTUDIANTE_PROFESOR,
            valor = redondearACentesimas(matriculados / profesoresTce),
            periodo = etiquetaPeriodo(periodo),
            facultad = etiquetaFacultad(facultad),
        )
    }
}
