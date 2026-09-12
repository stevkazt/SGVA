package com.example.sgva.domain

/**
 * Promedio de calificaciones Likert de las [RespuestaEncuesta] agrupadas por
 * `factor` y `estamento`, salida de `CalcularPonderacionLikertUseCase` (Módulo
 * 3, `DESIGN_SGVA.md` §2). No es un [IndicadorCalidad]: esa entidad es el
 * output consolidado exclusivo de los Módulos 1 y 2 (facultad/Estudiante y
 * Docente), y las respuestas de encuesta no tienen `facultad`.
 *
 * @property factor factor del modelo de autoevaluación al que corresponde el promedio.
 * @property estamento estamento institucional al que corresponde el promedio.
 * @property promedio promedio de `calificacion` (escala 1 a 5) del grupo.
 * @property periodo periodo académico `AAAAS` al que corresponde, o
 *   [IndicadorCalidad.CONSOLIDADO] cuando agrega todos los periodos.
 */
data class PonderacionLikert(
    val factor: String,
    val estamento: TipoEstamento,
    val promedio: Double,
    val periodo: String,
) {
    init {
        ReglasDeFormato.exigirNoVacio(factor, "factor")
        ReglasDeFormato.exigirNoVacio(periodo, "periodo")
    }
}
