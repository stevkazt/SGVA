package com.example.sgva.domain

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Valoración cualitativa institucional derivada de una calificación
 * documental de 1 a 5 (Módulo 4, `DESIGN_SGVA.md` §0.1). Usada únicamente por
 * [EvaluacionDocumental]; no depende de frameworks, bases de datos ni
 * sistemas de archivos.
 */
enum class ValoracionCualitativa {
    SE_CUMPLE_PLENAMENTE,
    SE_CUMPLE_EN_ALTO_GRADO,
    SE_CUMPLE_ACEPTABLEMENTE,
    SE_CUMPLE_INSATISFACTORIAMENTE,
    NO_SE_CUMPLE,
    ;

    companion object {
        /**
         * Deriva la valoración de [calificacion] según la tabla de rangos de
         * la escala institucional, aplicada sobre la calificación redondeada
         * a un decimal con la regla "mitad hacia arriba" (`DESIGN_SGVA.md`
         * §0.1). [calificacion] debe estar en `1.0..5.0`: esa validación ya
         * la aplica el `init` de [EvaluacionDocumental] antes de invocar esta
         * función.
         */
        fun desdeCalificacion(calificacion: Double): ValoracionCualitativa {
            val redondeada = BigDecimal.valueOf(calificacion).setScale(1, RoundingMode.HALF_UP).toDouble()
            return when {
                redondeada >= 4.6 -> SE_CUMPLE_PLENAMENTE
                redondeada >= 4.0 -> SE_CUMPLE_EN_ALTO_GRADO
                redondeada >= 3.0 -> SE_CUMPLE_ACEPTABLEMENTE
                redondeada >= 2.0 -> SE_CUMPLE_INSATISFACTORIAMENTE
                else -> NO_SE_CUMPLE
            }
        }
    }
}
