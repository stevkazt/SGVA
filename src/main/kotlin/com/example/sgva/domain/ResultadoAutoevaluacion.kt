package com.example.sgva.domain

/**
 * Salida de `CalcularResultadoAutoevaluacionUseCase` para un programa y un
 * periodo (Módulo 4, `DESIGN_SGVA.md` §1 y §0.1, V5). No depende de
 * frameworks, bases de datos ni sistemas de archivos.
 *
 * Los tres niveles de resultado (característica, factor, global) pueden ser
 * **ausentes**: se representan siempre como `null`, nunca como `0`
 * (`DESIGN_SGVA.md` §0.1, regla 6).
 *
 * @property programa identificador corto y estable del programa (ver §0.1 de `DESIGN_SGVA.md`).
 * @property periodo periodo académico del resultado en formato `AAAAS`.
 * @property promediosPorCaracteristica promedio de cada [Caracteristica] del
 *   catálogo de la configuración de ponderaciones usada, indexado por su `id`.
 * @property promediosPorFactor promedio de cada factor del catálogo, indexado por su identificador.
 * @property promedioGlobal promedio ponderado de los factores presentes, o ausente si ninguno lo está.
 */
data class ResultadoAutoevaluacion(
    val programa: String,
    val periodo: String,
    val promediosPorCaracteristica: Map<String, Double?>,
    val promediosPorFactor: Map<String, Double?>,
    val promedioGlobal: Double?,
) {
    init {
        ReglasDeFormato.exigirNoVacio(programa, "programa")
        ReglasDeFormato.exigirPeriodoAcademico(periodo, "periodo")
    }
}
