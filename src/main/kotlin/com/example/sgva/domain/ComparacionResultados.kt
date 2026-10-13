package com.example.sgva.domain

/**
 * Diferencia del resultado de autoevaluación entre dos periodos consecutivos
 * de la comparación (Módulo 5, `DESIGN_SGVA.md` §0.1, V6). Una diferencia con
 * un valor ausente en cualquiera de los dos periodos es ausente, nunca `0`.
 */
data class DiferenciaResultado(
    val periodoBase: String,
    val periodoComparado: String,
    val diferenciasPorFactor: Map<String, Double?>,
    val diferenciaGlobal: Double?,
)

/**
 * Resultado de `CompararResultadosEntreCiclosUseCase` para un [programa]
 * entre dos o más periodos (Módulo 5, `DESIGN_SGVA.md` §0.1 y §2, V6).
 *
 * @property programa identificador corto y estable del programa.
 * @property resultados resultado de autoevaluación de cada periodo comparado, en el orden recibido.
 * @property diferencias diferencia entre cada par de periodos consecutivos, en ese mismo orden.
 */
data class ComparacionResultados(
    val programa: String,
    val resultados: List<ResultadoAutoevaluacion>,
    val diferencias: List<DiferenciaResultado>,
)
