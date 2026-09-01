package com.example.sgva.domain

/**
 * Conversión de la dedicación de un [Docente] a *Tiempo Completo Equivalente*
 * (TCE), usada por el cálculo de capacidad instalada (`DESIGN_SGVA.md` §2,
 * Módulo 2).
 *
 * ⚠️ **Factores provisionales.** `DESIGN_SGVA.md` no define la política de
 * conversión a TCE; estos valores son un supuesto razonable (TC = 1 jornada,
 * medio tiempo = ½ jornada, cátedra ≈ ¼ de jornada) y **deben confirmarse con la
 * normativa de dedicación docente de la institución** antes de usar el indicador
 * para decisiones. Al estar centralizados aquí, ajustarlos no toca los casos de uso.
 */
object EquivalenciaTiempoCompleto {

    /** Fracción de jornada de Tiempo Completo que representa una [dedicacion]. */
    fun factor(dedicacion: TipoDedicacion): Double = when (dedicacion) {
        TipoDedicacion.TIEMPO_COMPLETO -> 1.0
        TipoDedicacion.MEDIO_TIEMPO -> 0.5
        TipoDedicacion.CATEDRA -> 0.25
    }
}
