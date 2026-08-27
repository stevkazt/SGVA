package com.example.sgva.domain

/**
 * Entidad de negocio pura que representa a un estudiante dentro del modelo de
 * autoevaluación académica (Factor 2). No depende de frameworks, bases de datos
 * ni sistemas de archivos.
 *
 * Las invariantes se verifican en el bloque `init`: cualquier registro que las
 * incumpla no puede existir como instancia de `Estudiante`.
 *
 * @property id identificador único anónimo del estudiante.
 * @property programa programa académico al que pertenece (ej.: "Ingeniería de Telecomunicaciones").
 * @property facultad facultad a la que está adscrito el programa (ej.: "Ingeniería").
 * @property cohorte cohorte de ingreso en formato `AAAAS` (ej.: "20211"), sin separadores.
 * @property estado estado de vinculación actual con la institución.
 * @property puntajeSaberPro puntaje global de Saber Pro en el rango `0..300`, o
 *   `null` si el estudiante aún no ha presentado la prueba.
 */
data class Estudiante(
    val id: String,
    val programa: String,
    val facultad: String,
    val cohorte: String,
    val estado: EstadoEstudiante,
    val puntajeSaberPro: Int? = null,
) {
    init {
        ReglasDeFormato.exigirNoVacio(id, "id")
        ReglasDeFormato.exigirNoVacio(programa, "programa")
        ReglasDeFormato.exigirNoVacio(facultad, "facultad")
        ReglasDeFormato.exigirPeriodoAcademico(cohorte, "cohorte")

        if (puntajeSaberPro != null && puntajeSaberPro !in RANGO_SABER_PRO) {
            throw PuntajeSaberProFueraDeRangoException(puntajeSaberPro)
        }
    }

    companion object {
        /** Rango oficial del puntaje global de las pruebas Saber Pro en Colombia. */
        val RANGO_SABER_PRO: IntRange = 0..300
    }
}
