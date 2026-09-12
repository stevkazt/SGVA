package com.example.sgva.domain

/**
 * Entidad de negocio pura que representa una respuesta individual de una
 * encuesta de percepción (Módulo 3). No depende de frameworks, bases de datos
 * ni sistemas de archivos.
 *
 * Las invariantes se verifican en el bloque `init`: cualquier registro que las
 * incumpla no puede existir como instancia de `RespuestaEncuesta`.
 *
 * @property id identificador único de la respuesta.
 * @property estamento estamento institucional que responde (ver Sección 0 de `DESIGN_SGVA.md`).
 * @property factor factor del modelo de autoevaluación al que se refiere la
 *   pregunta (ej.: "Infraestructura", "Plan de Estudios"). Es intencionalmente
 *   un `String` abierto, no un enum cerrado (Sección 0 de `DESIGN_SGVA.md`),
 *   para poder etiquetar percepciones sobre múltiples factores sin crear una
 *   entidad nueva por cada uno.
 * @property calificacion calificación en escala Likert de 1 a 5; ver rango válido en Sección 0.1.
 * @property periodo periodo académico del registro en formato `AAAAS` (ej.: "20261"), sin separadores.
 */
data class RespuestaEncuesta(
    val id: String,
    val estamento: TipoEstamento,
    val factor: String,
    val calificacion: Int,
    val periodo: String,
) {
    init {
        ReglasDeFormato.exigirNoVacio(id, "id")
        ReglasDeFormato.exigirNoVacio(factor, "factor")
        ReglasDeFormato.exigirPeriodoAcademico(periodo, "periodo")

        if (calificacion !in RANGO_CALIFICACION) {
            throw CalificacionFueraDeRangoException(calificacion)
        }
    }

    companion object {
        /** Rango válido de la escala Likert de 5 puntos (`DESIGN_SGVA.md` §0.1). */
        val RANGO_CALIFICACION: IntRange = 1..5
    }
}
