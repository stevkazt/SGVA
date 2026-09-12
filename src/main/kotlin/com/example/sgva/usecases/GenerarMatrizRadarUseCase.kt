package com.example.sgva.usecases

import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.MatrizRadar
import com.example.sgva.domain.SerieRadar

/**
 * Módulo 3 — matriz de comparación visual entre estamentos (`DESIGN_SGVA.md`
 * §2). Organiza los promedios de [CalcularPonderacionLikertUseCase] en la
 * forma que consume un gráfico de radar: un eje por factor, una serie por
 * estamento.
 */
class GenerarMatrizRadarUseCase(
    private val calcularPonderacionLikert: CalcularPonderacionLikertUseCase,
) {

    /**
     * @throws DatosInsuficientesException si no hay respuestas registradas para el filtro dado.
     */
    fun ejecutar(periodo: String? = null): MatrizRadar {
        val ponderaciones = calcularPonderacionLikert.ejecutar(periodo)

        val factores = ponderaciones.map { it.factor }.distinct().sorted()
        val series = ponderaciones
            .groupBy { it.estamento }
            .toSortedMap()
            .map { (estamento, grupo) ->
                SerieRadar(
                    estamento = estamento,
                    promediosPorFactor = grupo.associate { it.factor to it.promedio },
                )
            }

        return MatrizRadar(
            periodo = ponderaciones.first().periodo,
            factores = factores,
            series = series,
        )
    }
}
