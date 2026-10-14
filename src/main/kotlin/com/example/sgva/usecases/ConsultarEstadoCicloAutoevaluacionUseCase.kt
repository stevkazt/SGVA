package com.example.sgva.usecases

import com.example.sgva.domain.AvanceCiclo
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.ComparacionCicloAnterior
import com.example.sgva.domain.DiferenciaAvanceCiclo
import com.example.sgva.domain.EstadoCicloAutoevaluacion
import com.example.sgva.domain.FaseCiclo
import java.time.LocalDate

/**
 * Módulo 5 — estado consolidado del ciclo de autoevaluación de un programa
 * en un periodo, para el reporte ejecutivo (`DESIGN_SGVA.md` §2 y §4, V6):
 * avance por fase, participación de los actores, estado del plan de
 * mejoramiento y comparación con el ciclo inmediatamente anterior del mismo
 * programa. Por composición sobre los casos de uso ya existentes del
 * Módulo 5 (mismo patrón que [GenerarReporteEjecutivoUseCase]); no accede al
 * repositorio directamente.
 */
class ConsultarEstadoCicloAutoevaluacionUseCase(
    private val consultarCicloAutoevaluacion: ConsultarCicloAutoevaluacionUseCase,
    private val calcularAvanceCiclo: CalcularAvanceCicloUseCase,
    private val consultarProyectoMejoramiento: ConsultarProyectoMejoramientoUseCase,
) {

    /**
     * @return el estado del ciclo de ([programa], [periodo]), o `null` si no
     *   hay ningún ciclo registrado con esa clave.
     */
    fun ejecutar(programa: String, periodo: String, fechaReferencia: LocalDate): EstadoCicloAutoevaluacion? {
        val ciclo = consultarCicloAutoevaluacion.buscarPorProgramaYPeriodo(programa, periodo) ?: return null

        val avance = calcularAvanceCiclo.ejecutar(ciclo.id, fechaReferencia)
        val proyectos = consultarProyectoMejoramiento.listarTodos().filter { it.programa == programa }

        return EstadoCicloAutoevaluacion(
            ciclo = ciclo,
            avance = avance,
            proyectosMejoramiento = proyectos,
            comparacionCicloAnterior = comparacionCicloAnterior(ciclo, avance, fechaReferencia),
        )
    }

    /** Ausente si el programa no tiene un ciclo anterior registrado (`DESIGN_SGVA.md` §4, V6). */
    private fun comparacionCicloAnterior(
        ciclo: CicloAutoevaluacion,
        avanceActual: AvanceCiclo,
        fechaReferencia: LocalDate,
    ): ComparacionCicloAnterior? {
        val cicloAnterior = consultarCicloAutoevaluacion.buscarCicloAnterior(ciclo.programa, ciclo.periodo)
            ?: return null
        val avanceAnterior = calcularAvanceCiclo.ejecutar(cicloAnterior.id, fechaReferencia)

        return ComparacionCicloAnterior(
            cicloAnterior = cicloAnterior,
            avanceAnterior = avanceAnterior,
            diferencia = DiferenciaAvanceCiclo(
                periodoBase = cicloAnterior.periodo,
                periodoComparado = ciclo.periodo,
                diferenciasPorFase = diferenciaPorFase(avanceAnterior, avanceActual),
                diferenciaAvanceCiclo = diferencia(avanceAnterior.avanceCiclo, avanceActual.avanceCiclo),
            ),
        )
    }

    private fun diferenciaPorFase(base: AvanceCiclo, comparado: AvanceCiclo): Map<Int, Double?> =
        FaseCiclo.RANGO_NUMERO.associateWith { numero ->
            diferencia(base.avancePorFase[numero], comparado.avancePorFase[numero])
        }

    private fun diferencia(base: Double?, comparado: Double?): Double? =
        if (base == null || comparado == null) null else comparado - base
}
