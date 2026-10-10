package com.example.sgva.domain

/**
 * Promedio de una pregunta de la encuesta institucional de autoevaluación,
 * asociada a una [Caracteristica] del modelo de 12 factores y 48
 * características (Módulo 4, `DESIGN_SGVA.md` §1, V5). No depende de
 * frameworks, bases de datos ni sistemas de archivos.
 *
 * Su clave natural es la combinación de [programa], [periodo] y
 * [codigoPregunta]: no tiene un `id` propio, a diferencia de [Estudiante],
 * [Docente] o [RespuestaEncuesta] (`DESIGN_SGVA.md` §1).
 *
 * Las invariantes se verifican en el bloque `init`: cualquier registro que las
 * incumpla no puede existir como instancia de `ResultadoEncuestaPregunta`.
 *
 * @property programa identificador corto y estable del programa (ver §0.1 de `DESIGN_SGVA.md`).
 * @property periodo periodo académico del registro en formato `AAAAS` (ej.: "20261"), sin separadores.
 * @property codigoPregunta código de la pregunta en el instrumento institucional (ej.: "A2").
 * @property caracteristica identificador de la [Caracteristica] a la que se asocia la pregunta.
 * @property estamentos conjunto de [TipoEstamento] que responden la pregunta; no puede estar vacío.
 * @property promedio promedio de 1 a 5 de las respuestas, o `null` si el dato
 *   falta (equivalente al `N/A` del archivo de origen; ver §0.1 de `DESIGN_SGVA.md`).
 *   El valor `0` se considera inválido, no una ausencia de dato.
 */
data class ResultadoEncuestaPregunta(
    val programa: String,
    val periodo: String,
    val codigoPregunta: String,
    val caracteristica: String,
    val estamentos: Set<TipoEstamento>,
    val promedio: Double?,
) {
    init {
        ReglasDeFormato.exigirNoVacio(programa, "programa")
        ReglasDeFormato.exigirPeriodoAcademico(periodo, "periodo")
        ReglasDeFormato.exigirNoVacio(codigoPregunta, "codigoPregunta")
        ReglasDeFormato.exigirNoVacio(caracteristica, "caracteristica")
        ReglasDeFormato.exigirNoVacio(estamentos, "estamentos")

        if (promedio != null && promedio !in RANGO_PROMEDIO) {
            throw PromedioFueraDeRangoException(promedio)
        }
    }

    companion object {
        /** Rango válido del promedio de 1 a 5 de la escala institucional (`DESIGN_SGVA.md` §0.1). */
        val RANGO_PROMEDIO: ClosedFloatingPointRange<Double> = 1.0..5.0
    }
}
