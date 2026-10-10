package com.example.sgva.domain

/**
 * Evaluación documental y estadística consolidada de una [Caracteristica]
 * del modelo institucional de autoevaluación de 12 factores y 48
 * características, para un programa y un periodo (Módulo 4, `DESIGN_SGVA.md`
 * §1, V5). No depende de frameworks, bases de datos ni sistemas de archivos.
 *
 * Su clave natural es la combinación de [programa], [periodo] y
 * [caracteristica]: no tiene un `id` propio, igual que
 * [ResultadoEncuestaPregunta].
 *
 * Las invariantes se verifican en el bloque `init`: cualquier registro que las
 * incumpla no puede existir como instancia de `EvaluacionDocumental`.
 *
 * @property programa identificador corto y estable del programa (ver §0.1 de `DESIGN_SGVA.md`).
 * @property caracteristica identificador de la [Caracteristica] evaluada.
 * @property periodo periodo académico del registro en formato `AAAAS` (ej.: "20261"), sin separadores.
 * @property calificacion calificación de 1 a 5 de la evidencia documental y
 *   estadística consolidada, almacenada **tal como llega**, sin redondear.
 *   `N/A` no es un valor válido en este campo (ver §0.1 de `DESIGN_SGVA.md`).
 * @property justificacion justificación textual de la calificación; opcional.
 * @property sugerenciasMejora sugerencias de mejora derivadas de la evaluación; opcional.
 */
data class EvaluacionDocumental(
    val programa: String,
    val caracteristica: String,
    val periodo: String,
    val calificacion: Double,
    val justificacion: String? = null,
    val sugerenciasMejora: String? = null,
) {
    init {
        ReglasDeFormato.exigirNoVacio(programa, "programa")
        ReglasDeFormato.exigirNoVacio(caracteristica, "caracteristica")
        ReglasDeFormato.exigirPeriodoAcademico(periodo, "periodo")

        if (calificacion !in RANGO_CALIFICACION) {
            throw CalificacionDocumentalFueraDeRangoException(calificacion)
        }
    }

    /**
     * Valoración cualitativa derivada de [calificacion] según la escala
     * institucional (`DESIGN_SGVA.md` §0.1). No es un parámetro del
     * constructor: se deriva siempre de [calificacion], nunca se recibe ni se
     * persiste por separado.
     */
    val valoracionCualitativa: ValoracionCualitativa
        get() = ValoracionCualitativa.desdeCalificacion(calificacion)

    companion object {
        /** Rango válido de la calificación de 1 a 5 de la escala institucional (`DESIGN_SGVA.md` §0.1). */
        val RANGO_CALIFICACION: ClosedFloatingPointRange<Double> = 1.0..5.0
    }
}
