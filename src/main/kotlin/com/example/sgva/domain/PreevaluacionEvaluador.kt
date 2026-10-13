package com.example.sgva.domain

/**
 * Calificación documental preliminar que un evaluador asigna a una
 * [Caracteristica] dentro de un [CicloAutoevaluacion] (Módulo 5,
 * `DESIGN_SGVA.md` §0.1 y §1, V6). No depende de frameworks, bases de datos
 * ni sistemas de archivos.
 *
 * Su clave natural es la combinación de [ciclo], [caracteristica] y
 * [evaluador]: no tiene un `id` propio.
 *
 * El sistema identifica al evaluador con un identificador opaco: no se
 * modelan sus datos personales (`DESIGN_SGVA.md` §0).
 *
 * @property ciclo identificador del [CicloAutoevaluacion] al que pertenece.
 * @property caracteristica identificador de la [Caracteristica] evaluada.
 * @property evaluador identificador opaco del evaluador.
 * @property calificacion calificación de 1 a 5, la misma escala de
 *   [EvaluacionDocumental]; `N/A` no es un valor válido.
 * @property justificacion justificación textual de la calificación; opcional.
 * @property fortaleza fortaleza identificada por el evaluador; opcional.
 * @property oportunidadMejora oportunidad de mejora identificada por el evaluador; opcional.
 */
data class PreevaluacionEvaluador(
    val ciclo: String,
    val caracteristica: String,
    val evaluador: String,
    val calificacion: Double,
    val justificacion: String? = null,
    val fortaleza: String? = null,
    val oportunidadMejora: String? = null,
) {
    init {
        ReglasDeFormato.exigirNoVacio(ciclo, "ciclo")
        ReglasDeFormato.exigirNoVacio(caracteristica, "caracteristica")
        ReglasDeFormato.exigirNoVacio(evaluador, "evaluador")
        if (calificacion !in EvaluacionDocumental.RANGO_CALIFICACION) {
            throw CalificacionPreevaluacionFueraDeRangoException(calificacion)
        }
    }
}
