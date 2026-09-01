package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.IndicadorCalidad
import com.example.sgva.domain.ParametrosDeAnalisis
import java.time.Clock
import java.time.LocalDate

/**
 * Módulo 1 — evolución de la matrícula (`DESIGN_SGVA.md` §2).
 *
 * Cuenta los estudiantes con estado [EstadoEstudiante.MATRICULADO] agrupados por
 * su cohorte (año + semestre) dentro de la ventana histórica de los últimos
 * [ParametrosDeAnalisis.ANIOS_VENTANA_MATRICULA] años calendario. La ventana se
 * calcula dinámicamente desde la fecha actual del sistema ([reloj]), nunca con
 * un rango fijo (`DESIGN_SGVA.md` §0.1).
 *
 * Devuelve un [IndicadorCalidad] por cada periodo de la ventana, en orden
 * ascendente, incluidos los periodos con valor `0` para que la serie temporal
 * del Dashboard sea continua.
 */
class CalcularEvolucionMatriculaUseCase(
    repositorio: AcademicDataRepository,
    private val reloj: Clock,
) : CalculoDeIndicadorUseCase(repositorio) {

    fun ejecutar(facultad: String? = null): List<IndicadorCalidad> {
        val matriculadosPorCohorte: Map<String, Int> = estudiantesRegistrados(facultad)
            .filter { it.estado == EstadoEstudiante.MATRICULADO }
            .groupBy { it.cohorte }
            .mapValues { (_, estudiantes) -> estudiantes.size }

        val etiqueta = etiquetaFacultad(facultad)
        return ventanaDePeriodos().map { periodo ->
            IndicadorCalidad(
                nombre = IndicadorCalidad.EVOLUCION_MATRICULA,
                valor = (matriculadosPorCohorte[periodo] ?: 0).toDouble(),
                periodo = periodo,
                facultad = etiqueta,
            )
        }
    }

    private fun ventanaDePeriodos(): List<String> {
        val anioActual = LocalDate.now(reloj).year
        val primerAnio = anioActual - ParametrosDeAnalisis.ANIOS_VENTANA_MATRICULA + 1
        return (primerAnio..anioActual)
            .flatMap { anio -> listOf("${anio}1", "${anio}2") }
    }
}
