package com.example.sgva.domain

/**
 * Matriz de comparación visual entre estamentos para un mismo conjunto de
 * factores, salida de `GenerarMatrizRadarUseCase` (Módulo 3, `DESIGN_SGVA.md`
 * §2). Organiza los promedios de [PonderacionLikert] en la forma que consume
 * directamente un gráfico de radar del Dashboard: un eje por factor, una serie
 * por estamento.
 *
 * @property periodo periodo académico al que corresponde la matriz, o
 *   [IndicadorCalidad.CONSOLIDADO] cuando agrega todos los periodos.
 * @property factores ejes del radar: todos los factores con al menos una
 *   respuesta registrada, en orden alfabético.
 * @property series una entrada por estamento con respuestas registradas.
 */
data class MatrizRadar(
    val periodo: String,
    val factores: List<String>,
    val series: List<SerieRadar>,
)

/**
 * Promedios de un [TipoEstamento] para cada factor de una [MatrizRadar].
 *
 * @property promediosPorFactor promedio de calificación por factor; un factor
 *   de [MatrizRadar.factores] está ausente aquí si ese estamento no lo calificó.
 */
data class SerieRadar(
    val estamento: TipoEstamento,
    val promediosPorFactor: Map<String, Double>,
)
