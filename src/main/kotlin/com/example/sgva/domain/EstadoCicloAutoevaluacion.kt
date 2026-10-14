package com.example.sgva.domain

/**
 * Diferencia de avance entre el ciclo inmediatamente anterior y el ciclo
 * comparado, para el mismo programa (Módulo 5, `DESIGN_SGVA.md` §0.1, V6).
 * Misma regla que [DiferenciaResultado]: una diferencia con un valor ausente
 * en cualquiera de los dos ciclos es ausente, nunca `0`.
 */
data class DiferenciaAvanceCiclo(
    val periodoBase: String,
    val periodoComparado: String,
    val diferenciasPorFase: Map<Int, Double?>,
    val diferenciaAvanceCiclo: Double?,
)

/**
 * Comparación del avance de un ciclo contra el ciclo inmediatamente
 * anterior del mismo programa (Módulo 5, `DESIGN_SGVA.md` §0.1, V6).
 */
data class ComparacionCicloAnterior(
    val cicloAnterior: CicloAutoevaluacion,
    val avanceAnterior: AvanceCiclo,
    val diferencia: DiferenciaAvanceCiclo,
)

/**
 * Estado consolidado de un [CicloAutoevaluacion] para el reporte ejecutivo
 * (Módulo 5, `DESIGN_SGVA.md` §4, V6): avance por fase y global,
 * participación de los actores, estado del plan de mejoramiento del
 * programa y comparación con el ciclo inmediatamente anterior del mismo
 * programa, si existe.
 *
 * @property ciclo ciclo de autoevaluación consultado.
 * @property avance avance del ciclo (por fase, global, actividades y participación de actores).
 * @property proyectosMejoramiento proyectos del plan de mejoramiento del programa del ciclo.
 * @property comparacionCicloAnterior comparación con el ciclo inmediatamente
 *   anterior del mismo programa, o ausente si no hay ningún ciclo anterior registrado.
 */
data class EstadoCicloAutoevaluacion(
    val ciclo: CicloAutoevaluacion,
    val avance: AvanceCiclo,
    val proyectosMejoramiento: List<ProyectoMejoramiento>,
    val comparacionCicloAnterior: ComparacionCicloAnterior?,
)
