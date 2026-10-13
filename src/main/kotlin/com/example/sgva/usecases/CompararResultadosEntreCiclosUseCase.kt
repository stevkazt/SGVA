package com.example.sgva.usecases

import com.example.sgva.domain.ComparacionResultados
import com.example.sgva.domain.DiferenciaResultado
import com.example.sgva.domain.ResultadoAutoevaluacion

/**
 * Módulo 5 — comparación del resultado de autoevaluación de un programa
 * entre ciclos (`DESIGN_SGVA.md` §0.1 y §2, V6). Reutiliza
 * [CalcularResultadoAutoevaluacionUseCase] para cada periodo y calcula, por
 * factor y global, la diferencia entre cada par de periodos consecutivos en
 * el orden recibido. Una diferencia con un valor ausente en cualquiera de
 * los dos periodos es ausente, nunca `0` (`DESIGN_SGVA.md` §0.1).
 */
class CompararResultadosEntreCiclosUseCase(
    private val calcularResultadoAutoevaluacion: CalcularResultadoAutoevaluacionUseCase,
) {

    fun ejecutar(programa: String, periodos: List<String>): ComparacionResultados {
        val resultados = periodos.map { calcularResultadoAutoevaluacion.ejecutar(programa, it) }

        val diferencias = resultados.zipWithNext { base, comparado ->
            DiferenciaResultado(
                periodoBase = base.periodo,
                periodoComparado = comparado.periodo,
                diferenciasPorFactor = diferenciaPorFactor(base, comparado),
                diferenciaGlobal = diferencia(base.promedioGlobal, comparado.promedioGlobal),
            )
        }

        return ComparacionResultados(programa = programa, resultados = resultados, diferencias = diferencias)
    }

    private fun diferenciaPorFactor(
        base: ResultadoAutoevaluacion,
        comparado: ResultadoAutoevaluacion,
    ): Map<String, Double?> =
        (base.promediosPorFactor.keys + comparado.promediosPorFactor.keys).associateWith { factor ->
            diferencia(base.promediosPorFactor[factor], comparado.promediosPorFactor[factor])
        }

    private fun diferencia(base: Double?, comparado: Double?): Double? =
        if (base == null || comparado == null) null else comparado - base
}
